package fr.kevpdev.dicom_processor.service;

import fr.kevpdev.dicom_processor.dto.MetaDataDicomFileDTO;
import fr.kevpdev.dicom_processor.exception.DicomFileAlreadyProcessedException;
import fr.kevpdev.dicom_processor.repository.DicomProcessingLogRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class DicomProcessingLogService {

    private final DicomProcessingLogRepository dicomProcessingLogRepository;

    private final Logger logger = LogManager.getLogger(DicomProcessingLogService.class);

    public DicomProcessingLogService(DicomProcessingLogRepository dicomProcessingLogRepository) {
        this.dicomProcessingLogRepository = dicomProcessingLogRepository;
    }

    /**
     * Check if file is already processed
     * @param sopInstanceUID SOP Instance UID
     * @return true if file is already processed
     */
    public boolean isFileAlreadyProcessed(String sopInstanceUID) {
        return dicomProcessingLogRepository.existsById(sopInstanceUID);
    }

    /**
     * Verify if file is already processed in database
     * @param metaDataDicomFileDTO MetaDataDicomFileDTO
     * @return MetaDataDicomFileDTO
     */
    public MetaDataDicomFileDTO verifyFileInDatabase(MetaDataDicomFileDTO metaDataDicomFileDTO) throws DicomFileAlreadyProcessedException {
        logger.debug("VerifyFileInDatabase - Payload {}", metaDataDicomFileDTO);

        if(isFileAlreadyProcessed(metaDataDicomFileDTO.sopInstanceUID())) {
           throw new DicomFileAlreadyProcessedException("File " + metaDataDicomFileDTO.file().getAbsolutePath() + " already processed");
        }
        else {
            logger.debug("File {} not processed", metaDataDicomFileDTO.file().getAbsolutePath());
            return metaDataDicomFileDTO;
        }
    }


}
