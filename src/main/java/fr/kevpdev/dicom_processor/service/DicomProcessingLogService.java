package fr.kevpdev.dicom_processor.service;

import fr.kevpdev.dicom_processor.dto.MetaDataDicomFileDTO;
import fr.kevpdev.dicom_processor.entity.DicomProcessingLog;
import fr.kevpdev.dicom_processor.entity.EStatus;
import fr.kevpdev.dicom_processor.exception.DicomDatabaseException;
import fr.kevpdev.dicom_processor.exception.DicomFileAlreadyProcessedException;
import fr.kevpdev.dicom_processor.repository.DicomProcessingLogRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class DicomProcessingLogService {

    private final DicomProcessingLogRepository dicomProcessingLogRepository;

    private final Logger logger = LogManager.getLogger(DicomProcessingLogService.class);

    public DicomProcessingLogService(DicomProcessingLogRepository dicomProcessingLogRepository) {
        this.dicomProcessingLogRepository = dicomProcessingLogRepository;
    }


    /**
     * Verify if file is already processed in database
     * @param metaDataDicomFileDTO MetaDataDicomFileDTO
     * @return MetaDataDicomFileDTO
     */
    public MetaDataDicomFileDTO verifyFileInDatabase(MetaDataDicomFileDTO metaDataDicomFileDTO)
            throws DicomFileAlreadyProcessedException {

        logger.debug("VerifyFileInDatabase - Payload {}", metaDataDicomFileDTO);

       try {
           Optional<DicomProcessingLog> optionalLatestLog = dicomProcessingLogRepository
                   .findBySopInstanceUIDAndLimitOne(metaDataDicomFileDTO.sopInstanceUID());

           //succes
           if(optionalLatestLog.isPresent()) {

               DicomProcessingLog dcmProcessingLog = optionalLatestLog.get();

               if(dcmProcessingLog.getStatus().equals(EStatus.SUCCESS)) {
                   throw new DicomFileAlreadyProcessedException("File "
                           + metaDataDicomFileDTO.file().getAbsolutePath()
                           + " already processed");
               } else if(dcmProcessingLog.getStatus().equals(EStatus.ERROR)) {
                   return createNewInProgressProcessing(metaDataDicomFileDTO, dcmProcessingLog.getProcessingCount() + 1);
               }
               else {
                   return metaDataDicomFileDTO;
               }
           }

           return createNewInProgressProcessing(metaDataDicomFileDTO, 1);

       }catch (DataAccessException e) {
            throw new DicomDatabaseException("Error while verifying file in database", e);
       }
    }

    public MetaDataDicomFileDTO createNewInProgressProcessing(MetaDataDicomFileDTO metaDataDicomFileDTO, int processingCount) {
        DicomProcessingLog newLog = new DicomProcessingLog();
        newLog.setSopInstanceUID(metaDataDicomFileDTO.sopInstanceUID());
        newLog.setOriginalPath(metaDataDicomFileDTO.file().getAbsolutePath());
        newLog.setStatus(EStatus.IN_PROGRESS);
        newLog.setProcessingCount(processingCount);
        newLog.setLastProcessedDate(ZonedDateTime.now());
        dicomProcessingLogRepository.save(newLog);
        return metaDataDicomFileDTO;
    }

    public void saveLogWhenFileIsFailed(MetaDataDicomFileDTO metaDataDicomFileDTO, String errorMessage) {
        Optional<DicomProcessingLog> optionalLatestLog = dicomProcessingLogRepository
                .findBySopInstanceUIDAndLimitOne(metaDataDicomFileDTO.sopInstanceUID());

        if (optionalLatestLog.isPresent()) {

            DicomProcessingLog latestLog = optionalLatestLog.get();
            latestLog.setStatus(EStatus.ERROR);
            latestLog.setErrorMessage(errorMessage);
            latestLog.setLastProcessedDate(ZonedDateTime.now());

            dicomProcessingLogRepository.save(latestLog);
        } else {

            DicomProcessingLog newLog = new DicomProcessingLog();
            newLog.setSopInstanceUID(metaDataDicomFileDTO.sopInstanceUID());
            newLog.setOriginalPath(metaDataDicomFileDTO.file().getAbsolutePath());
            newLog.setStatus(EStatus.ERROR);
            newLog.setErrorMessage(errorMessage);
            newLog.setProcessingCount(1);
            newLog.setLastProcessedDate(ZonedDateTime.now());

            dicomProcessingLogRepository.save(newLog);
        }

        logger.error("Fichier ({}) marqué en FAILED : {}", metaDataDicomFileDTO.sopInstanceUID(), errorMessage);
    }





}
