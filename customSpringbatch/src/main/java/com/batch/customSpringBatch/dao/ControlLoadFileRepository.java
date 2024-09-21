package com.batch.customSpringBatch.dao;

import com.batch.customSpringBatch.model.ControlLoadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ControlLoadFileRepository extends JpaRepository<ControlLoadFile,Long> {
}
