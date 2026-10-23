package com.socialnetwork.module.file.controller;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.common.response.ApiResponse;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.file.dto.response.FileResponse;
import com.socialnetwork.module.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileResponse> upload(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file
    ) {
        if (userDetails == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        UUID userId = userDetails.getUser().getId();

        FileResponse response = fileService.upload(file, userId);

        return ApiResponse.success(
                "Tải file lên thành công",
                response
        );
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> getFile(
            @PathVariable UUID fileId
    ) {
        FileResponse file = fileService.getFile(fileId);
        Resource resource = fileService.load(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""
                                + file.getFileId()
                                + "\""
                )
                .body(resource);
    }
}