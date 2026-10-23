package com.socialnetwork.module.file.repository;

import com.socialnetwork.module.file.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, UUID> {
}