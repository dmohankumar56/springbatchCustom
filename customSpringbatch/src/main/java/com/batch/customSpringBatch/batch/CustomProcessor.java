package com.batch.customSpringBatch.batch;

import com.batch.customSpringBatch.dto.IncomingDetailDto;
import com.batch.customSpringBatch.dto.OutgoingFileDto;
import com.test.fiserv.walmartscheduler.dto.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class CustomProcessor implements ItemProcessor<IncomingDetailDto, OutgoingFileDto> {

    @Override
    public OutgoingFileDto process(IncomingDetailDto incomingFileDto) throws Exception {

        return null;
        // Map header
        /*HeaderDto headerDto = HeaderDto.builder()
                .indicator(incomingFileDto.getHeaderDto().getIndicator())
                .fileName(incomingFileDto.getHeaderDto().getFileName())
                .fileUniqueId(incomingFileDto.getHeaderDto().getFileUniqueId())
                .dateTime(incomingFileDto.getHeaderDto().getDateTime())
                .build();

        // Map details
        List<OutgoingDetailDto> outgoingDetails = incomingFileDto.getDetailDtoList().stream()
                .map(detail -> OutgoingDetailDto.builder()
                        .indicator(incomingFileDto.getIndicator())
                        .fileName(detail.getFileName())
                        .totalRecords(detail.getTotalRecords())
                        .fileToken(detail.getFileToken())
                        .dateTime(detail.getDateTime())
                        .successRecords(0) // Initialize based on your logic
                        .failedRecords(0) // Initialize based on your logic
                        .status("Notified") // Initialize based on your logic
                        .build())
                .collect(Collectors.toList());

        // Map footer
        OutgoingFooterDto outgoingFooterDto = OutgoingFooterDto.builder()
                .indicator(incomingFileDto.getIncomingFooterDto().getIndicator())
                .totalRecords(incomingFileDto.getIncomingFooterDto().getTotalRecords())
                .processedRecords(0) // Initialize based on your logic
                .failedRecords(0) // Initialize based on your logic
                .build();

        return OutgoingFileDto.builder()
                .headerDto(headerDto)
                .detailDtoList(outgoingDetails)
                .outgoingFooterDto(outgoingFooterDto)
                .build();*/
    }
}
