package com.baranova.cat_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baranova.cat_service.dto.CatDTO;
import com.baranova.cat_service.entity.CatCache;
import com.baranova.cat_service.entity.Photo;
import com.baranova.cat_service.repository.PhotoRepository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    private final Map<Integer, CatCache> userPhotosCache = new ConcurrentHashMap<>();

    @Autowired
    private PhotoRepository photoRepository;

    public CatDTO getPhotoById(Long id) {
        return CatConverter.fromEntity(photoRepository.findById(id).orElse(null));
    }

    public Long savePhoto(Long chatId, CatDTO photoDto) {
        Photo photo = CatConverter.toEntity(photoDto, chatId);
        Photo saved = photoRepository.save(photo);
        allCats.computeIfAbsent(chatId, k -> getAllSortedPhotoFromDatabase()).add(photoDto);
        return saved.getId();
    }

    public void resetState(Long chatId) {
        allCats.put(chatId, getAllSortedPhotoFromDatabase());
    }

    public void resetStateWithPagination(Long chatId, int page, int size) {
        allCats.put(chatId, getPhotosWithPagination(page, size));
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

    public void clearCache() {
        userPhotosCache.clear();
        logger.info("Cache cleared. Remaining cache size: " + userPhotosCache.size());
    }

    private void clearCachePartially() {
        List<Map.Entry<Integer, CatCache>> sortedCache = userPhotosCache.entrySet()
                .stream()
                .sorted((e1, e2) -> e1.getValue().getLastAccessed().compareTo(e2.getValue().getLastAccessed()))
                .collect(Collectors.toList());

        int pagesToRemove = sortedCache.size() / 2;
        for (int i = 0; i < pagesToRemove; i++) {
            Map.Entry<Integer, CatCache> entry = sortedCache.get(i);
            userPhotosCache.remove(entry.getKey());
        }
        logger.info("Cache cleared. Remaining cache size: " + userPhotosCache.size());
    }

    public Queue<CatDTO> getPhotosWithPagination(int page, int size) {
        if (userPhotosCache.size() > 50) clearCachePartially();

        if (userPhotosCache.containsKey(page)) {
            userPhotosCache.get(page).setLastAccessed(System.currentTimeMillis());
            return new ConcurrentLinkedQueue<>(userPhotosCache.get(page).getCatDto());
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadedAt"));
        Page<Photo> photoPage = photoRepository.findAll(pageable);
        List<CatDTO> photos = photoPage.stream().map(CatConverter::fromEntity).collect(Collectors.toList());
        userPhotosCache.put(page, new CatCache(photos, System.currentTimeMillis()));
        return new ConcurrentLinkedQueue<>(photos);

    }

    public List<CatDTO> getUserPhotosWithPagination(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("uploadedAt"));
        Page<Photo> photoPage = photoRepository.findByAuthor(userId, pageable);
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
