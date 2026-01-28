package com.board.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private final String uploadDir;

    public FileService() {
        // 프로젝트 루트의 uploads 디렉토리 사용 (절대 경로로 변환)
        this.uploadDir = System.getProperty("user.dir") + "/uploads/";
        createUploadDir();
    }

    private void createUploadDir() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 고유한 파일명 생성
        String filename = UUID.randomUUID().toString() + extension;
        Path path = Paths.get(uploadDir + filename);

        Files.write(path, file.getBytes());

        return "/uploads/" + filename;
    }
}
