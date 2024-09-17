package com.batch.customSpringBatch.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Table(name ="CONTROL_DETAIL_RECORDS")
@Data
@Builder
public class ControlDetailRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="CONTROL_DETAIL_RECORD_ID")
    private Long controlDetailRecordId;
    @Column(name ="CONTROL_LOAD_ID")
    private Long controlLoadId;
    @Column(name ="FILE_NAME")
    private String fileName;
    @Column(name ="TOTAL_RECORDS")
    private int totalRecords;
    @Column(name ="FILE_TOKEN")
    private String fileToken;
    @Column(name ="FILE_DATE")
    private LocalDateTime fileDate;
    @Column(name ="SUCCESS_RECORDS")
    private int successRecordsCnt;
    @Column(name ="FAILED_RECORDS")
    private int failedRecordsCnt;
    @Column(name ="FILE_STATUS")
    private String fileStatus;


}
