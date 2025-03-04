package com.baranova.cat_bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PhotoService {
    private final Logger logger = LoggerFactory.getLogger(PhotoService.class);
    private final Map<String, Queue<String>> chatDirs = new ConcurrentHashMap<>();

    @Value("${telegram.media.local_root_path}")
    private String rootPath;

    public String savePhoto(String chatId, InputStream is, String fileExtension) throws IOException {
        String userDirPath = rootPath + "/" + chatId + "/";
        Files.createDirectories(Paths.get(userDirPath));
        String filename = System.currentTimeMillis() + fileExtension;
        Files.copy(is, Paths.get(userDirPath + filename));
        chatDirs.computeIfAbsent(chatId, this::getSortedPhotoFromDirectory).add(filename);
        return filename;
    }

    public void resetState(String chatId) {
        chatDirs.put(chatId, getSortedPhotoFromDirectory(chatId));
    }

    public String getNextPhoto(String chatId) {
        if (!chatDirs.containsKey(chatId) || chatDirs.get(chatId).isEmpty()) {
            return null;
        }
        String filename = chatDirs.get(chatId).poll();
        return rootPath + "/" + chatId + "/" + filename;
    }

    private Queue<String> getSortedPhotoFromDirectory(String chatId) {
        String userDirPath = rootPath + "/" + chatId + "/";
        List<String> files = new ArrayList<>();
        try {
            Files.list(Paths.get(userDirPath)).forEach(path -> files.add(path.getFileName().toString()));
        } catch (IOException e) {
            logger.error("Failed to get files from directory", e);
        }

        Map<String, String> fileExtensions = new HashMap<>();
        files.forEach(filename -> {
            String extension = filename.substring(filename.lastIndexOf('.'));
            String nameWithoutExtension = filename.replaceAll("\\.[^.]*$", "");
            fileExtensions.put(nameWithoutExtension, extension);
        });

        List<String> sortedList = files.stream()
                .map(filename -> filename.replaceAll("\\.[^.]*$", ""))
                .sorted(Comparator.comparing(Long::parseLong))
                .map(filename -> filename + fileExtensions.get(filename))
                .collect(Collectors.toList());
        return new ConcurrentLinkedQueue<>(sortedList);
    }

}
