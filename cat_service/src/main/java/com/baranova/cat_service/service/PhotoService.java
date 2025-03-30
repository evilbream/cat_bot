package com.baranova.cat_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baranova.cat_service.dto.CatDTO;
import com.baranova.cat_service.entity.Photo;
import com.baranova.cat_service.repository.PhotoRepository;
import com.baranova.cat_service.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.baranova.cat_service.entity.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

import com.baranova.cat_service.dto.converters.CatConverter;

@Service
public class PhotoService {
    private final Logger logger = LoggerFactory.getLogger(PhotoService.class);
    private final Map<Long, Queue<CatDTO>> allCats = new ConcurrentHashMap<>();

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

    public CatDTO getPhotoById(Long id) {
        return CatConverter.fromEntity(photoRepository.findById(id).orElse(null));
    }

    @Async
    public void savePhoto(Long chatId, CatDTO photoDto) {
        User user = userRepository.findById(chatId).orElse(null);
        Photo photo = CatConverter.toEntity(photoDto, user);
        photoRepository.save(photo);
        allCats.computeIfAbsent(chatId, k -> getAllSortedPhotoFromDatabase()).add(photoDto);
    }

    public void resetState(Long chatId) {
        allCats.put(chatId, getAllSortedPhotoFromDatabase());
    }

    public CatDTO getNextPhoto(Long chatId) {
        if (allCats.get(chatId) == null || allCats.get(chatId).isEmpty()) {
            return null;
        }
        return allCats.get(chatId).poll();
    }

    public byte[] toBytes(InputStream fileStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        try {
            while ((nRead = fileStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            logger.error("Error saving photo in chat ", e);
            return null;
        }
    }

    public List<CatDTO> getPhotosWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadedAt"));
        Page<Photo> photoPage = photoRepository.findAll(pageable);
        return photoPage.stream().map(CatConverter::fromEntity).collect(Collectors.toList());

    }

    public List<CatDTO> getUserPhotosWithPagination(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadedAt"));
        Page<Photo> photoPage = photoRepository.findByAuthorId(userId, pageable);
        return photoPage.stream().map(CatConverter::fromEntity).collect(Collectors.toList());
    }

    private Queue<CatDTO> getAllSortedPhotoFromDatabase() {
        List<CatDTO> photos = photoRepository.findAll(Sort.by(Sort.Direction.DESC, "uploadedAt"))
                .stream()
                .map(CatConverter::fromEntity).toList();
        return new ConcurrentLinkedQueue<>(photos);
    }

    public boolean existsById(Long id) {
        return photoRepository.existsById(id);
    }

    @Async
    @Transactional
    public void deletePhoto(Long photoId) throws IllegalArgumentException {
        photoRepository.deleteById(photoId);
    }


}
