package com.batch.customSpringBatch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OutgoingDetailDto {
    private String indicator;
    private String fileName;
    private int totalRecords;
    private String fileToken;
    private String dateTime;
    private int successRecords;
    private int failedRecords;
    private String status;
}
