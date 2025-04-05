package com.baranova.tg_service.services;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PhotoService {
    private final Logger logger = LoggerFactory.getLogger(PhotoService.class);

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
}
