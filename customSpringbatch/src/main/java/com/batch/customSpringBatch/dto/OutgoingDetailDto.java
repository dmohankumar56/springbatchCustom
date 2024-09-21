package com.batch.customSpringBatch.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OutgoingDetailDto {
    private String indicator;
    private String fileName;
    private int totalRecords;
    private String fileToken;
    private LocalDateTime dateTime;
    private int successRecords;
    private int failedRecords;
    private String status;
}
