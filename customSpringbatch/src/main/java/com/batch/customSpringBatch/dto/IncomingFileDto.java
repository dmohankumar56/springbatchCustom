package com.batch.customSpringBatch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomingFileDto {
    private HeaderDto headerDto;
    private List<IncomingDetailDto> detailDtoList;
    private IncomingFooterDto incomingFooterDto;
}
