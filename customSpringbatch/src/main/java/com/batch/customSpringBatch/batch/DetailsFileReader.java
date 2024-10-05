package com.batch.customSpringBatch.batch;

import com.batch.customSpringBatch.dto.HeaderDto;
import com.batch.customSpringBatch.dto.IncomingDetailDto;
import com.batch.customSpringBatch.dto.IncomingFileDto;
import com.batch.customSpringBatch.dto.IncomingFooterDto;
import com.batch.customSpringBatch.exception.CustomBatchException;
import com.batch.customSpringBatch.model.ControlLoadFile;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@Component
@StepScope
public class DetailsFileReader implements ItemReader<IncomingFileDto>, ItemStream {

    private  BufferedReader reader;
    private final List<IncomingDetailDto> detailList = new ArrayList<>();
    private String nextLine;
    private String filePath;
    private StepExecution stepExecution;

    // Initialize BufferedReader in the constructor, so it only reads the file once
    public void setFilePath(String filePath) throws IOException {
        this.reader = new BufferedReader(new FileReader(filePath));
        this.nextLine = reader.readLine(); // Read the first line
    }
    /*public DetailsFileReader() throws IOException {
        this.reader = new BufferedReader(new FileReader("C:\\Users\\mohan\\OneDrive\\Documents\\walbatch\\inbound\\incoming1.txt"));
        this.nextLine = reader.readLine(); // Read the first line
    }*/

    @Override
    public void open(ExecutionContext executionContext) {
        // Set the file path in the Job ExecutionContext
        System.out.println("Opening reader and setting sourceFilePath: " + filePath);
    }

    @Override
    public void close() {
        // Ensure the file reader is properly closed
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing file reader: " + e.getMessage());
        }
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


                try {
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
                        continue;
                    }
                } catch (Exception e) {
                    throw new CustomBatchException("Error reading header: " + e.getMessage());
                }

                try {
                    // Handle footer
                    if (line.startsWith("T")) {
                        IncomingFooterDto footer = new IncomingFooterDto();
                        footer.setIndicator(line.substring(0, 1).trim()); // 'T'
                        footer.setTotalRecords(Integer.parseInt(line.substring(1).trim()));
                        currentFileDto.setIncomingFooterDto(footer);
                        continue;
                    }
                } catch (Exception e) {
                    throw new CustomBatchException("Error reading footer: " + e.getMessage());
                }


                if (line.startsWith(" ")) {
                    // The line starts with a space instead of 'D', mark it as an error record
                    IncomingDetailDto errorDetail = new IncomingDetailDto();
                    // Set the error details, you can also store the entire line for reference
                    errorDetail.setFileName("Error Record");
                    errorDetail.setDetailLineInfo(line);  // Store the faulty line information
                    errorDetail.setErrorFlag(true); // Indicate there was an error
                    errorDetail.setErrorMessage("Starts with space"); // Capture error message
                    // Add the error detail to a separate error list or log it as an error
                    detailList.add(errorDetail);  // Assuming you have a list to capture error details
                    // Move to the next line after logging the error
                    continue; // Skip the current line and move to the next
                }
                try{
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
            }
            catch (Exception e){
                IncomingDetailDto errorDetail = new IncomingDetailDto();
                errorDetail.setFileName("Error Record");
                errorDetail.setDetailLineInfo(line); // Set the erroneous line
                errorDetail.setErrorFlag(true); // Indicate there was an error
                errorDetail.setErrorMessage(e.getMessage()); // Capture error message
                detailList.add(errorDetail);
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



