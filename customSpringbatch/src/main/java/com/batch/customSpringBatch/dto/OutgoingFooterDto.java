package com.batch.customSpringBatch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OutgoingFooterDto {
    private String indicator;
    private int totalRecords;
    private int processedRecords;
    private int failedRecords;
}
