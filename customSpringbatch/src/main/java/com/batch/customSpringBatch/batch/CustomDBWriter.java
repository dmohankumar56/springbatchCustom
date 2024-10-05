package com.batch.customSpringBatch.batch;

import com.batch.customSpringBatch.dao.ControlDetailRecordsRepository;
import com.batch.customSpringBatch.dao.ControlLoadFileRepository;
import com.batch.customSpringBatch.dto.HeaderDto;
import com.batch.customSpringBatch.dto.OutgoingDetailDto;
import com.batch.customSpringBatch.dto.OutgoingFileDto;
import com.batch.customSpringBatch.dto.OutgoingFooterDto;
import com.batch.customSpringBatch.model.ControlDetailRecords;
import com.batch.customSpringBatch.model.ControlLoadFile;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomDBWriter implements ItemWriter<OutgoingFileDto> {

    private final ControlLoadFileRepository controlLoadFileRepository;
    private final ControlDetailRecordsRepository controlDetailRecordsRepository;

    // Constructor injection
    @Autowired
    public CustomDBWriter(ControlLoadFileRepository controlLoadFileRepository,
                          ControlDetailRecordsRepository controlDetailRecordsRepository) {
        this.controlLoadFileRepository = controlLoadFileRepository;
        this.controlDetailRecordsRepository = controlDetailRecordsRepository;
    }

    @Override
    public void write(Chunk<? extends OutgoingFileDto> chunk) throws Exception {
        OutgoingFileDto outgoingFile = chunk.getItems().get(0);
        HeaderDto header = outgoingFile.getHeaderDto();
        OutgoingFooterDto footerDto = outgoingFile.getOutgoingFooterDto();

        // Create and save ControlLoadFile entity
        ControlLoadFile controlLoadFile = ControlLoadFile.builder()
                .fileName(header.getFileName())
                .fileUniqueId(header.getFileUniqueId())
                .dateTime(header.getDateTime())
                .totalFiles(outgoingFile.getDetailDtoList().size())
                .fileLoadedCnt(footerDto.getProcessedRecords())  // Adjust as needed
                .fileRejectCnt(footerDto.getFailedRecords())  // Adjust as needed
                .status("Sent")  // Adjust as needed
                .paystubLoadTransactionId(0)  // Adjust as needed
                .build();
       // controlLoadFileRepository.save(controlLoadFile);

        List<ControlDetailRecords> detailRecordsList = new ArrayList<>();
        // Iterate through detail DTOs and save ControlDetailRecords entities
        for (OutgoingFileDto outgoingFileDto : chunk.getItems()) {
            for (OutgoingDetailDto detail : outgoingFileDto.getDetailDtoList()) {
                ControlDetailRecords controlDetailRecords = ControlDetailRecords.builder()
                        .controlLoadFile(controlLoadFile)
                        .fileName(detail.getFileName())
                        .totalRecords(detail.getTotalRecords())
                        .fileToken(detail.getFileToken())
                        .fileDate(detail.getDateTime())
                        .successRecordsCnt(detail.getSuccessRecords())
                        .failedRecordsCnt(detail.getFailedRecords())
                        .fileStatus(detail.getStatus())
                        .build();
                //controlDetailRecordsRepository.save(controlDetailRecords);
                detailRecordsList.add(controlDetailRecords);
            }

        }
        controlLoadFile.setControlDetailRecords(detailRecordsList);
        // Save the controlLoadFile (this will automatically save the detail records due to cascade = CascadeType.ALL)
        controlLoadFileRepository.save(controlLoadFile);
    }

}


