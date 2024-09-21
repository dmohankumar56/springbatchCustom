
package com.batch.customSpringBatch.batch;


import com.batch.customSpringBatch.model.ControlLoadFile;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class HeaderFileReader implements ItemReader<ControlLoadFile> {
    private boolean isHeaderRead = false;

    @Override
    public ControlLoadFile read() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\mohan\\OneDrive\\Documents\\walbatch\\inbound\\incoming1.txt"));
        if (!isHeaderRead) {
            String line = reader.readLine();
            if (line != null && line.startsWith("H")) {
                LocalDateTime dateTime = getLocalDateTime(line);
                ControlLoadFile header = ControlLoadFile.builder()
                        .fileName(line.substring(1, 20).trim())
                        .fileUniqueId(line.substring(21, 56).trim())
                        .dateTime(dateTime)
                        .build();

                isHeaderRead = true; // Mark as header read
                return header;
            }
        }
        return null; // No more header data
    }

    private static LocalDateTime getLocalDateTime(String line) {
        String dateTimeStr = line.substring(56, 72).trim(); // 2024061200:00:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH:mm:ss");
        return LocalDateTime.parse(dateTimeStr, formatter);
    }
}


