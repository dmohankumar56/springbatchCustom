package com.batch.customSpringBatch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderDto {
    private String indicator;
    private String fileName;
    private String fileUniqueId;
    private String dateTime;
}
