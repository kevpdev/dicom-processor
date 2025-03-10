package fr.kevpdev.dicom_processor.repository;

import fr.kevpdev.dicom_processor.entity.DicomProcessingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DicomProcessingLogRepository extends JpaRepository<DicomProcessingLog, Long> {

    //recup lastest log by SOPInstanceUID whith @query and limit 1
    @Query(value = "select * from dicom_processing_log p where p.sop_instance_uid = ?1 order by p.last_processed_date desc limit 1",
            nativeQuery = true)
    public Optional<DicomProcessingLog> findBySopInstanceUIDAndLimitOne(String sopInstanceUID);

}
