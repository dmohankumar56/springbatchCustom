package com.batch.customSpringBatch.dao;

import com.batch.customSpringBatch.model.ControlDetailRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControlDetailRecordsRepository extends JpaRepository<ControlDetailRecords,Long> {
}
