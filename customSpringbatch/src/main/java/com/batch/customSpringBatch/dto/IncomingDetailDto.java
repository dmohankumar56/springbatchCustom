package com.batch.customSpringBatch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncomingDetailDto {
    private String indicator;
    private String fileName;
    private int totalRecords;
    private String fileToken;
    private LocalDateTime dateTime;
}
