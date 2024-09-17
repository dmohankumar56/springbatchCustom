package com.batch.customSpringBatch.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OutgoingFileDto {
    private HeaderDto headerDto;
    private List<OutgoingDetailDto> detailDtoList;
    private OutgoingFooterDto outgoingFooterDto;
}
