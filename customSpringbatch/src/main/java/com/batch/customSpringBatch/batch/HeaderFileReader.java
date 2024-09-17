/*
package com.test.fiserv.walmartscheduler.batch;

import model.com.batch.customSpringBatch.ControlLoadFile;
import org.springframework.batch.item.ItemReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class HeaderFileReader implements ItemReader<ControlLoadFile> {
    private boolean isHeaderRead = false;

    @Override
    public ControlLoadFile read() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\mohan\\OneDrive\\Documents\\walbatch\\inbound\\incoming.txt"));
        if (!isHeaderRead) {
            String line = reader.readLine();
            if (line != null && line.startsWith("H")) {
                LocalDateTime dateTime = getLocalDateTime(line);
                ControlLoadFile header = ControlLoadFile.builder()
                        .fileName(line.substring(1, 21).trim())
                        .fileUniqueId(line.substring(21, 57).trim())
                        .dateTime(dateTime)
                        .build();

                isHeaderRead = true; // Mark as header read
                return header;
            }
        }
        return null; // No more header data
    }

    private static LocalDateTime getLocalDateTime(String line) {
        String dateTimeStr = line.substring(57, 73).trim(); // 2024061200:00:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH:mm:ss");
        return LocalDateTime.parse(dateTimeStr, formatter);
    }
}

*/
