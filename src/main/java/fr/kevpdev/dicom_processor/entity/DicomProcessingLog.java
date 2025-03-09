package fr.kevpdev.dicom_processor.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity(name = "dicom_processing_log")
public class DicomProcessingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sop_instance_uid", nullable = false)
    private String sopInstanceUID;

    @Column(name = "original_path", nullable = false)
    private String originalPath;

    @Column(name = "processed_path")
    private String processedPath;

    @Column(name = "partial_view")
    private Boolean partialView;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EStatus status;

    @Column(name = "processing_count", nullable = false)
    private int processingCount;

    @Column(name = "last_processed_date", nullable = false)
    private ZonedDateTime lastProcessedDate;

    @Column(name = "error_message")
    private String errorMessage;
}
