package com.socialnetwork.module.file.service;

import com.socialnetwork.module.file.dto.response.FileResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileService {

    FileResponse upload(MultipartFile file, UUID userId);

    Resource load(UUID fileId);

    FileResponse getFile(UUID fileId);
}