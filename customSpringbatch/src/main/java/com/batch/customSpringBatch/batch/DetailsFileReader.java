package com.batch.customSpringBatch.batch;

import com.batch.customSpringBatch.dto.IncomingDetailDto;
import com.batch.customSpringBatch.dto.IncomingFileDto;
import org.springframework.batch.item.ItemReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailsFileReader implements ItemReader<IncomingDetailDto> {

    private BufferedReader reader;
    private String line;
    private boolean isHeaderProcessed = false;
    private IncomingFileDto currentFileDto;
    private List<IncomingDetailDto> detailList = new ArrayList<>();

    public DetailsFileReader(String filePath) throws IOException {
        this.reader = new BufferedReader(new FileReader(filePath));
    }

    @Override
    public IncomingDetailDto read() throws IOException {
        String line = reader.readLine();
        if (line != null && line.startsWith("D")) {
            IncomingDetailDto detail = new IncomingDetailDto();

            detail.setIndicator(line.substring(0, 1).trim()); // D
            detail.setFileName(line.substring(1, 41).trim()); // FileName (40 char)
            detail.setTotalRecords(Integer.parseInt(line.substring(41, 45).trim())); // TotalRecords (4 char)
            detail.setFileToken(line.substring(45, 65).trim()); // FileToken (20 char)
            detail.setDateTime(line.substring(65).trim()); // DateTime

            return detail;
        }
        return null; // No more details
    }

}
