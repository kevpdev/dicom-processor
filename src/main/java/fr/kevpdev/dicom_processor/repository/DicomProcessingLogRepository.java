package fr.kevpdev.dicom_processor.repository;

import fr.kevpdev.dicom_processor.entity.DicomProcessingLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DicomProcessingLogRepository extends JpaRepository<DicomProcessingLog, String> {
}
