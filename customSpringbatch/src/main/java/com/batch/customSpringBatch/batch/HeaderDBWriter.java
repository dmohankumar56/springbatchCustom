package com.batch.customSpringBatch.batch;

import com.batch.customSpringBatch.dao.ControlLoadFileRepository;
import com.batch.customSpringBatch.model.ControlLoadFile;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HeaderDBWriter implements ItemWriter<ControlLoadFile> {


    private ControlLoadFileRepository controlLoadFileRepository; // Your JPA repository for saving header

    @Autowired
    public HeaderDBWriter(ControlLoadFileRepository controlLoadFileRepository) {
        this.controlLoadFileRepository = controlLoadFileRepository;
    }


    @Override
    public void write(Chunk<? extends ControlLoadFile> chunk)  {
            controlLoadFileRepository.save(chunk.getItems().get(0));
        }

}
