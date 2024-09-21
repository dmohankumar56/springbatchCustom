package com.batch.customSpringBatch.batch;

import com.batch.customSpringBatch.dao.PayloadStubRepository;
import com.batch.customSpringBatch.dto.*;
import com.batch.customSpringBatch.model.PayloadStubFile;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CustomProcessor implements ItemProcessor<IncomingFileDto, OutgoingFileDto> {

    private final PayloadStubRepository payloadStubRepository;

    public CustomProcessor(PayloadStubRepository payloadStubRepository) {
        this.payloadStubRepository = payloadStubRepository;
    }


    @Override
    public OutgoingFileDto process(IncomingFileDto incomingFileDto) throws Exception {

        // Map header
        HeaderDto headerDto = HeaderDto.builder()
                .indicator(incomingFileDto.getHeaderDto().getIndicator())
                .fileName(incomingFileDto.getHeaderDto().getFileName())
                .fileUniqueId(incomingFileDto.getHeaderDto().getFileUniqueId())
                .dateTime(incomingFileDto.getHeaderDto().getDateTime())
                .build();

        // Map details
        List<OutgoingDetailDto> outgoingDetails = incomingFileDto.getDetailDtoList().stream()
                .map(detail -> {
                    // Fetch the PayloadStubFile based on fileName, fileToken, fileDateTime, and appOrgId
                    Optional<PayloadStubFile> payloadStub = payloadStubRepository.findByFileNameAndFileTokenAndFileDateTimeAndAppOrgId(
                            detail.getFileName(),
                            detail.getFileToken(),
                            detail.getDateTime(),
                            7434 // You can pass appOrgId here from your logic or configuration
                    );

                    // Calculate sum of errors (loadErrors + relatedFileErrors + relatedConfigErrors)
                    int totalErrors = payloadStub.map(ps -> ps.getLoadErrors() + ps.getRelatedFileErrors() + ps.getRelatedConfigErrors()).orElse(0);

                    // Map the incoming detail to the outgoing detail DTO
                    return OutgoingDetailDto.builder()
                            .indicator(detail.getIndicator())
                            .fileName(detail.getFileName())
                            .totalRecords(detail.getTotalRecords())
                            .fileToken(detail.getFileToken())
                            .dateTime(detail.getDateTime())
                            // Initialize success and failure records based on payload stub data
                            .successRecords(payloadStub.map(PayloadStubFile::getSuccesRecords).orElse(0))
                            .failedRecords(totalErrors)
                            .status(String.valueOf(payloadStub.map(PayloadStubFile::getStatus))) // Set status based on whether the payload exists
                            .build();
                })
                .collect(Collectors.toList());

        // Map footer
        OutgoingFooterDto outgoingFooterDto = OutgoingFooterDto.builder()
                .indicator(incomingFileDto.getIncomingFooterDto().getIndicator())
                .totalRecords(incomingFileDto.getIncomingFooterDto().getTotalRecords())
                // Count the processed records (Loaded and Notified are successful)
                .processedRecords((int) outgoingDetails.stream()
                        .filter(detail -> detail.getStatus().equals("Loaded") || detail.getStatus().equals("Notified"))
                        .count())
                // Count the failed records (Error is a failure)
                .failedRecords((int) outgoingDetails.stream()
                        .filter(detail -> detail.getStatus().equals("Error"))
                        .count()) // Initialize based on your logic
                .build();

        return OutgoingFileDto.builder()
                .headerDto(headerDto)
                .detailDtoList(outgoingDetails)
                .outgoingFooterDto(outgoingFooterDto)
                .build();
    }
}
