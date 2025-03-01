package fr.kevpdev.dicom_processor.repositories;

import fr.kevpdev.dicom_processor.entities.DicomFileProcessingLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DicomFileRepository extends JpaRepository<DicomFileProcessingLog, String> {
}
