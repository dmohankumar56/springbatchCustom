package com.batch.customSpringBatch.dao;

import com.batch.customSpringBatch.model.PayloadStubFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PayloadStubRepository extends JpaRepository<PayloadStubFile,Long> {


    Optional<PayloadStubFile> findByFileNameAndFileTokenAndFileDateTimeAndAppOrgId(
            String fileName, String fileToken, LocalDateTime fileDateTime, int appOrgId
    );

}
