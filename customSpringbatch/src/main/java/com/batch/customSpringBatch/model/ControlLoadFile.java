package com.batch.customSpringBatch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Entity
@Table(name="CONTROL_LOAD_FILE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ControlLoadFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="CONTROL_LOAD_ID")
    private Long controlLoadId;
    @Column(name ="FILE_NAME")
    private String fileName;
    @Column(name ="FILE_UNIQUE_ID")
    private String fileUniqueId;
    @Column(name ="DATE_TIME")
    private LocalDateTime dateTime;
    @Column(name ="TOTAL_FILES")
    private int totalFiles;
    @Column(name ="FILE_LOADED_CNT")
    private int fileLoadedCnt;
    @Column(name ="FILE_REJECT_CNT")
    private int fileRejectCnt;
    @Column(name ="STATUS")
    private String status;
    @Column(name ="PAYSTUBLOADTRANSID")
    private int paystubLoadTransactionId;
    @OneToMany(mappedBy = "controlLoadFile", cascade = CascadeType.ALL)
    private List<ControlDetailRecords> controlDetailRecords;

}
