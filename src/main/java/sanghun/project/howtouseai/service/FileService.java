package sanghun.project.howtouseai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    private static final String CATEGORY_ICON_PATH = "src/main/resources/static/images/categories/";
    private static final String[] ALLOWED_EXTENSIONS = {".png", ".jpg", ".jpeg", ".svg"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String uploadCategoryIcon(MultipartFile file) throws IOException {
        log.info("카테고리 아이콘 업로드 시작: filename={}, size={}", 
                file.getOriginalFilename(), file.getSize());

        // 파일 검증
        validateFile(file);

        // 파일 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        // 고유한 파일명 생성
        String uniqueFilename = generateUniqueFilename(extension);

        // 파일 저장 경로
        Path uploadPath = Paths.get(CATEGORY_ICON_PATH);
        Path filePath = uploadPath.resolve(uniqueFilename);

        // 디렉토리가 없으면 생성
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일 저장
        Files.copy(file.getInputStream(), filePath);
        
        log.info("카테고리 아이콘 업로드 완료: savedPath={}", filePath);

        // 웹 접근 URL 반환
        return "/images/categories/" + uniqueFilename;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 비어있습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기가 5MB를 초과합니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }

        String extension = getFileExtension(originalFilename);
        boolean isValidExtension = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                isValidExtension = true;
                break;
            }
        }

        if (!isValidExtension) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. (png, jpg, jpeg, svg만 허용)");
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }
        return filename.substring(lastDotIndex).toLowerCase();
    }

    private String generateUniqueFilename(String extension) {
        return UUID.randomUUID().toString() + extension;
    }
} 