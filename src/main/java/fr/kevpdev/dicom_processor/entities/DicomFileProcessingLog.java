package fr.kevpdev.dicom_processor.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity(name = "dicom_files_processing_log")
public class DicomFileProcessingLog {

    @Id
    private String sopInstanceUID;

    private String originalPath;

    private String processedPath;

    private Boolean partilView;

    @Enumerated(EnumType.STRING)
    private EStatus status;

    private int processingCount;

    private ZonedDateTime lastProcessedDate;

    private String errorMessage;
}
