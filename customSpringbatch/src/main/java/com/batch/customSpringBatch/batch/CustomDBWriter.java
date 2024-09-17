package com.batch.customSpringBatch.batch;

import com.batch.customSpringBatch.dto.OutgoingFileDto;
import com.batch.customSpringBatch.dao.ControlDetailRecordsRepository;
import com.batch.customSpringBatch.dao.ControlLoadFileRepository;
import com.batch.customSpringBatch.dto.HeaderDto;
import com.batch.customSpringBatch.dto.OutgoingDetailDto;
import com.batch.customSpringBatch.model.ControlDetailRecords;
import com.batch.customSpringBatch.model.ControlLoadFile;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        // Create and save ControlLoadFile entity
        ControlLoadFile controlLoadFile = ControlLoadFile.builder()
                .fileName(header.getFileName())
                .fileUniqueId(header.getFileUniqueId())
                .dateTime(parseDate(header.getDateTime()))
                .totalFiles(outgoingFile.getDetailDtoList().size())
                .fileLoadedCnt(0)  // Adjust as needed
                .fileRejectCnt(0)  // Adjust as needed
                .status("Notified")  // Adjust as needed
                .paystubLoadTransactionId(0)  // Adjust as needed
                .build();
        controlLoadFile = controlLoadFileRepository.save(controlLoadFile);

        // Iterate through detail DTOs and save ControlDetailRecords entities
        for (OutgoingFileDto outgoingFileDto : chunk.getItems()) {
            for (OutgoingDetailDto detail : outgoingFileDto.getDetailDtoList()) {
                ControlDetailRecords controlDetailRecords = ControlDetailRecords.builder()
                        .controlLoadId(controlLoadFile.getControlLoadId())
                        .fileName(detail.getFileName())
                        .totalRecords(detail.getTotalRecords())
                        .fileToken(detail.getFileToken())
                        .fileDate(parseDate(detail.getDateTime()))
                        .successRecordsCnt(detail.getSuccessRecords())
                        .failedRecordsCnt(detail.getFailedRecords())
                        .fileStatus(detail.getStatus())
                        .build();
                controlDetailRecordsRepository.save(controlDetailRecords);
            }
        }
    }

    private LocalDateTime parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  // Adjust the format as needed
        return LocalDateTime.parse(dateStr, formatter);
    }
}


