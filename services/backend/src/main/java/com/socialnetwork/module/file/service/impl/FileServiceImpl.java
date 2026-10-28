package com.socialnetwork.module.file.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.file.dto.response.FileResponse;
import com.socialnetwork.module.file.entity.FileEntity;
import com.socialnetwork.module.file.repository.FileRepository;
import com.socialnetwork.module.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final Tika TIKA = new Tika();

    private static final long MAX_FILE_SIZE = 50L * 1024 * 1024;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif",
            "video/mp4",
            "video/webm"
    );

    private final FileRepository fileRepository;

    @Value("${app.file.storage-dir:uploads}")
    private String storageDir;


    @Override
    @Transactional
    public FileResponse upload(MultipartFile file, UUID userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Dung lượng file tối đa là 50 MB");
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null || originalName.isBlank()) {
            originalName = "upload";
        }

        try {
            String contentType;

            // Kiểm tra nội dung thực tế của file
            try (InputStream inputStream = file.getInputStream()) {
                contentType = TIKA.detect(inputStream, originalName);
            }

            if (!ALLOWED_TYPES.contains(contentType)) {
                throw new IllegalArgumentException(
                        "Định dạng file không được hỗ trợ"
                );
            }

            String extension = getExtensionFromContentType(contentType);
            String storedName = UUID.randomUUID() + extension;

            Path root = Paths.get(storageDir)
                    .toAbsolutePath()
                    .normalize();
            Files.createDirectories(root);

            Path destination = root.resolve(storedName).normalize();

            // Đảm bảo file không nằm ngoài thư mục upload
            if (!destination.startsWith(root)) {
                throw new IllegalArgumentException("Đường dẫn file không hợp lệ");
            }

            boolean fileWritten = false;
            boolean metadataSaved = false;

            try {
                file.transferTo(destination);
                fileWritten = true;

                FileEntity entity = new FileEntity();
                entity.setOriginalName(originalName);
                entity.setStoredName(storedName);
                entity.setContentType(contentType);
                entity.setSizeBytes(file.getSize());
                entity.setStoragePath(destination.toString());
                entity.setUploadedBy(userId);

                entity = fileRepository.save(entity);
                metadataSaved = true;

                return toResponse(entity);
            } finally {
                // Nếu lưu metadata thất bại, xóa file vật lý đã tạo
                if (fileWritten && !metadataSaved) {
                    try {
                        Files.deleteIfExists(destination);
                    } catch (IOException cleanupException) {
                        // Nên ghi log để xử lý file rác còn sót
                        log.error("Không thể xóa file sau khi upload thất bại: {}",
                                destination, cleanupException);
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Không thể lưu file", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource load(UUID fileId) {
        FileEntity entity = findFile(fileId);

        try {
            Path path = Paths.get(entity.getStoragePath())
                    .toAbsolutePath()
                    .normalize();

            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException("File không tồn tại trên ổ đĩa");
            }

            return resource;

        } catch (IOException e) {
            throw new IllegalStateException("Không thể đọc file", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FileResponse getFile(UUID fileId) {
        return toResponse(findFile(fileId));
    }

    private FileEntity findFile(UUID fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
                );
    }

    private FileResponse toResponse(FileEntity entity) {
        return FileResponse.builder()
                .fileId(entity.getId())
                .originalName(entity.getOriginalName())
                .contentType(entity.getContentType())
                .sizeBytes(entity.getSizeBytes())
                .url("/api/files/" + entity.getId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private String getExtensionFromContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            case "video/mp4" -> ".mp4";
            case "video/webm" -> ".webm";
            default -> throw new IllegalArgumentException(
                    "Định dạng file không được hỗ trợ"
            );
        };
    }
}