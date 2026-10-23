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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

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
            throw new IllegalArgumentException(
                    "Dung lượng file không được vượt quá 50 MB"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException(
                    "Định dạng file không được hỗ trợ"
            );
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null || originalName.isBlank()) {
            originalName = "upload";
        }

        // Chỉ giữ tên file, không dùng đường dẫn do client gửi lên.
        originalName = Paths.get(originalName)
                .getFileName()
                .toString();

        String extension = getSafeExtension(originalName);

        String storedName = UUID.randomUUID() + extension;
        Path root = Paths.get(storageDir).toAbsolutePath().normalize();
        Path destination = root.resolve(storedName).normalize();

        if (!destination.startsWith(root)) {
            throw new IllegalArgumentException("Đường dẫn file không hợp lệ");
        }

        try {
            Files.createDirectories(root);
            file.transferTo(destination);

            FileEntity entity = FileEntity.builder()
                    .originalName(originalName)
                    .storedName(storedName)
                    .contentType(contentType.toLowerCase(Locale.ROOT))
                    .sizeBytes(file.getSize())
                    .storagePath(destination.toString())
                    .uploadedBy(userId)
                    .build();

            entity = fileRepository.save(entity);

            return toResponse(entity);

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
                        new IllegalArgumentException("Không tìm thấy file")
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

    private String getSafeExtension(String originalName) {
        int index = originalName.lastIndexOf('.');

        if (index < 0) {
            return "";
        }

        String extension = originalName.substring(index).toLowerCase(Locale.ROOT);

        if (!Set.of(
                ".jpg", ".jpeg", ".png", ".webp", ".gif",
                ".mp4", ".webm"
        ).contains(extension)) {
            return "";
        }

        return extension;
    }
}