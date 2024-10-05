package com.batch.customSpringBatch.batch;

import com.batch.customSpringBatch.dto.OutgoingFileDto;
import com.batch.customSpringBatch.dto.OutgoingFooterDto;
import com.batch.customSpringBatch.dto.HeaderDto;
import com.batch.customSpringBatch.dto.OutgoingDetailDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@StepScope
public class CustomFileWriter implements ItemWriter<OutgoingFileDto> {

    @Override
    public void write(Chunk<? extends OutgoingFileDto> chunk) throws Exception {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        OutgoingFileDto firstItem = (OutgoingFileDto) chunk.getItems().get(0);
        String baseFileName = firstItem.getHeaderDto().getFileName(); // Assuming the header DTO has a file name
        String dynamicFileName = String.format("outgoing_file_%s_%d.txt", baseFileName, System.currentTimeMillis());
        FlatFileItemWriter<OutgoingFileDto> writer = new FlatFileItemWriterBuilder<OutgoingFileDto>()
                .name("outgoingFileWriter")
                .resource(new FileSystemResource("C:\\Users\\mohan\\OneDrive\\Documents\\walbatch\\outbound\\"+dynamicFileName))
                .lineAggregator(outgoingFileDto -> {
                    StringBuilder sb = new StringBuilder();

                    // Write Header
                    HeaderDto header = outgoingFileDto.getHeaderDto();
                    sb.append(String.format("%-1s", header.getIndicator()))  // Indicator (length 1)
                            .append(String.format("%-20s", header.getFileName()).trim())  // File Name (length 20, no space after Indicator)
                            .append(String.format("%-36s", header.getFileUniqueId())) // File Unique ID (length 36)
                            .append(String.format("%-16s", header.getDateTime()))   // DateTime (length 16)
                            .append("\n");

                    // Write Details
                    for (OutgoingDetailDto detail : outgoingFileDto.getDetailDtoList()) {
                        if(detail.isErrorFlag()){
                            sb.append(String.format("%-1s", detail.getDetailLineInfo()))
                                    .append(String.format("%-5s", detail.getStatus()))
                                    .append("\n");
                        }
                        else {
                            sb.append(String.format("%-1s", detail.getIndicator())) // Indicator
                                    .append(String.format("%-40s", detail.getFileName())) // File Name (adjust length as needed)
                                    .append(String.format("%-5d", detail.getTotalRecords())) // Total Records
                                    .append(String.format("%-20s", detail.getFileToken())) // File Token
                                    .append(String.format("%-16s", detail.getDateTime())) // DateTime
                                    .append(String.format("%-5d", detail.getSuccessRecords())) // Success Records
                                    .append(String.format("%-5d", detail.getFailedRecords())) // Failed Records
                                    .append(String.format("%-10s", detail.getStatus()))  // Status
                                    .append("\n");
                        }
                    }

                    // Write Footer
                    OutgoingFooterDto footer = outgoingFileDto.getOutgoingFooterDto();
                    sb.append(String.format("%-1s", footer.getIndicator()))  // Indicator
                            .append(String.format("%-5d", footer.getTotalRecords()))  // Total Records
                            .append(String.format("%-5d", footer.getProcessedRecords()))  // Processed Records
                            .append(String.format("%-5d", footer.getFailedRecords()))  // Failed Records
                            .append("\n");
                    return sb.toString();
                })
                .build();

        ExecutionContext executionContext = new ExecutionContext();

        writer.open(executionContext);

        // Write each item in the chunk
        List<OutgoingFileDto> outgoingFileDtos = (List<OutgoingFileDto>) chunk.getItems();

        // Write the chunk items
        writer.write(chunk);

        // Close the writer after writing
        writer.close();

    }



}
