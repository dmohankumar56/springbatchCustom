package com.batch.customSpringBatch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="PAYLOAD_STUB_FILE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayloadStubFile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="PAYSTUBLOADID")
    private Long paystubLoadId;
    @Column(name="FILE_NAME")
    private String fileName;
    @Column(name="APP_ORG_ID")
    private int appOrgId;
    @Column(name="FILE_TOKEN")
    private String fileToken;
    @Column(name="SUCCESS_RECORDS")
    private int succesRecords;
    @Column(name="RELATED_LOAD_ERRORS")
    private int loadErrors;
    @Column(name="RELATED_FILE_ERRORS")
    private int relatedFileErrors;
    @Column(name="RELATED_CONFIG_ERRORS")
    private int relatedConfigErrors;
    @Column(name="DATE_TIME")
    private LocalDateTime fileDateTime;
    @Column(name ="status")
    private String status;



}
