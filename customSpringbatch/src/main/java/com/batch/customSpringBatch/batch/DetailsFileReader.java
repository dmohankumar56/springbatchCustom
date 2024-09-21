package com.batch.customSpringBatch.batch;

import com.batch.customSpringBatch.dto.HeaderDto;
import com.batch.customSpringBatch.dto.IncomingDetailDto;
import com.batch.customSpringBatch.dto.IncomingFileDto;
import com.batch.customSpringBatch.dto.IncomingFooterDto;
import com.batch.customSpringBatch.model.ControlLoadFile;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@Component
public class DetailsFileReader implements ItemReader<IncomingFileDto> {

    private final BufferedReader reader;
    private final List<IncomingDetailDto> detailList = new ArrayList<>();
    private String nextLine;

    // Initialize BufferedReader in the constructor, so it only reads the file once
    public DetailsFileReader() throws IOException {
        this.reader = new BufferedReader(new FileReader("C:\\Users\\mohan\\OneDrive\\Documents\\walbatch\\inbound\\incoming1.txt"));
        this.nextLine = reader.readLine(); // Read the first line
    }

    @Override
    public IncomingFileDto read() throws IOException {
        // If no lines are left, close the reader and return null
        if (nextLine == null) {
            reader.close();
            return null; // Signals end of file to Spring Batch
        }
        IncomingFileDto currentFileDto = new IncomingFileDto();

        while (nextLine != null) {
            String line = nextLine;
            nextLine = reader.readLine(); // Move to the next line for the next call

            if (line.startsWith("H")) {
                // Parse header
                String dateTimeStr = line.substring(56, 72).trim(); // e.g., 2024061200:00:00
                LocalDateTime dateTime = getLocalDateTime(dateTimeStr);
                HeaderDto headerDto = HeaderDto.builder()
                        .fileName(line.substring(1, 20).trim())
                        .fileUniqueId(line.substring(21, 56).trim())
                        .dateTime(dateTime)
                        .build();
                currentFileDto.setHeaderDto(headerDto);
            }

            // Check if line starts with 'D' for details
            if (line.startsWith("D")) {
                IncomingDetailDto detail = new IncomingDetailDto();
                detail.setIndicator(line.substring(0, 1).trim()); // 'D'
                detail.setFileName(line.substring(1, 60).trim()); // FileName (40 char)
                detail.setTotalRecords(Integer.parseInt(line.substring(61, 67).trim())); // TotalRecords (4 char)
                detail.setFileToken(line.substring(67, 103).trim()); // FileToken (20 char)
                detail.setDateTime(getLocalDateTime(line.substring(103).trim())); // DateTime
                detailList.add(detail);
            }

            // Handle footer
            if (line.startsWith("T")) {
                IncomingFooterDto footer = new IncomingFooterDto();
                footer.setIndicator(line.substring(0, 1).trim()); // 'T'
                footer.setTotalRecords(Integer.parseInt(line.substring(1).trim()));
                currentFileDto.setIncomingFooterDto(footer);
            }
        }

        // If details were collected, set them in the file DTO
        if (!detailList.isEmpty()) {
            currentFileDto.setDetailDtoList(detailList);
        }



        return currentFileDto;
    }

    // Helper method to parse LocalDateTime
    private static LocalDateTime getLocalDateTime(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH:mm:ss");
        return LocalDateTime.parse(dateTimeStr, formatter);
    }
}



