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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class DicomProcessingLogService {

    private final DicomProcessingLogRepository dicomProcessingLogRepository;
    private final TransactionTemplate transactionTemplate;
    private final Logger logger = LogManager.getLogger(DicomProcessingLogService.class);

    public DicomProcessingLogService(DicomProcessingLogRepository dicomProcessingLogRepository, TransactionTemplate transactionTemplate) {
        this.dicomProcessingLogRepository = dicomProcessingLogRepository;
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * Verify if file is already processed in database
     * @param metaDataDicomFileDTO MetaDataDicomFileDTO
     * @return MetaDataDicomFileDTO
     */
    @Transactional
    public MetaDataDicomFileDTO verifyFileInDatabase(MetaDataDicomFileDTO metaDataDicomFileDTO)
            throws DicomFileAlreadyProcessedException {

        logger.debug("VerifyFileInDatabase - Payload {}", metaDataDicomFileDTO);

       try {

           EStatus status = metaDataDicomFileDTO.getStatus();

           Optional<DicomProcessingLog> optionalLatestLog = dicomProcessingLogRepository
                   .findBySopInstanceUIDAndLimitOne(metaDataDicomFileDTO.getSopInstanceUID());

           if(optionalLatestLog.isPresent()) {

               DicomProcessingLog dcmProcessingLog = optionalLatestLog.get();

               if(status == EStatus.SUCCESS) {
                   throw new DicomFileAlreadyProcessedException("File "
                           + metaDataDicomFileDTO.getFile().getAbsolutePath()
                           + " already processed");
               } else if(status == EStatus.ERROR) {
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

    /**
     * Create new log in progress
     * @param metaDataDicomFileDTO
     * @param processingCount
     * @return MetaDataDicomFileDTO
     */
    public MetaDataDicomFileDTO createNewInProgressProcessing(MetaDataDicomFileDTO metaDataDicomFileDTO, int processingCount) {
        DicomProcessingLog newLog = new DicomProcessingLog();
        newLog.setSopInstanceUID(metaDataDicomFileDTO.getSopInstanceUID());
        newLog.setOriginalPath(metaDataDicomFileDTO.getFile().getAbsolutePath());
        newLog.setStatus(EStatus.IN_PROGRESS);
        newLog.setProcessingCount(processingCount);
        newLog.setLastProcessedDate(ZonedDateTime.now());
        dicomProcessingLogRepository.save(newLog);
        return metaDataDicomFileDTO;
    }


    /**
     * Update log and return metaDataDicomFileDTO
     *
     * @param metaDataDicomFileDTO
     */
    public void updateDicomProcessingLog(MetaDataDicomFileDTO metaDataDicomFileDTO) {
        logger.debug("UpdateDicomProcessingLog - Payload {}", metaDataDicomFileDTO);

        transactionTemplate.execute(statusTransaction -> {
            try {
                Optional<DicomProcessingLog> optionalLatestLog = dicomProcessingLogRepository
                        .findBySopInstanceUIDAndLimitOne(metaDataDicomFileDTO.getSopInstanceUID());


                if (metaDataDicomFileDTO.getStatus() == EStatus.SUCCESS && !validateSuccessCondition(optionalLatestLog, metaDataDicomFileDTO)) {
                    return null;
                }

                return createOrUpdateLog(optionalLatestLog, metaDataDicomFileDTO);

            } catch (DataAccessException e) {
                throw new DicomDatabaseException("Impossible to update log", e);
            }
        });
    }

    /**
     * Update log and return metaDataDicomFileDTO
     * @param metaDataDicomFileDTO MetaDataDicomFileDTO
     * @return MetaDataDicomFileDTO
     */
    public MetaDataDicomFileDTO updateLogAndReturnMetaData(MetaDataDicomFileDTO metaDataDicomFileDTO) {
            updateDicomProcessingLog(metaDataDicomFileDTO);
        return metaDataDicomFileDTO;
    }

    /**
     * Check if the log is in progress and if it is the case, return true.
     * @param optionalLatestLog
     * @param metaDataDicomFileDTO
     * @return
     */
    private boolean validateSuccessCondition(Optional<DicomProcessingLog> optionalLatestLog, MetaDataDicomFileDTO metaDataDicomFileDTO) {
        if (!optionalLatestLog.isPresent()) {
            logger.warn("Impossible mark file ({}) as SUCCESS : no log found",
                    metaDataDicomFileDTO.getSopInstanceUID());
            return false;
        }
        if (optionalLatestLog.get().getStatus() != EStatus.IN_PROGRESS) {
            logger.warn("Impossible mark file ({}) as SUCCESS : log is not in progress",
                    metaDataDicomFileDTO.getSopInstanceUID());
            return false;
        }
        return true;
    }

    /**
     * Create or update the log
     * @param optionalLatestLog Optional<DicomProcessingLog>
     * @param metaDataDicomFileDTO MetaDataDicomFileDTO
     */
    private DicomProcessingLog createOrUpdateLog(Optional<DicomProcessingLog> optionalLatestLog, MetaDataDicomFileDTO metaDataDicomFileDTO) {

        String errorMessage = metaDataDicomFileDTO.getErrorMessage() != null ? metaDataDicomFileDTO.getErrorMessage() : "";
        EStatus status = metaDataDicomFileDTO.getStatus();

        if (optionalLatestLog.isPresent()) {
            DicomProcessingLog latestLog = optionalLatestLog.get();
            latestLog.setStatus(status);
            latestLog.setPartialView(metaDataDicomFileDTO.getViewType());
            latestLog.setLastProcessedDate(ZonedDateTime.now());

            if (status == EStatus.ERROR) {
                latestLog.setErrorMessage(errorMessage);
                logger.error("File ({}) marked as FAILED : {}", metaDataDicomFileDTO.getSopInstanceUID(), errorMessage);
            } else {
                logger.info("File ({}) marked as SUCCESS", metaDataDicomFileDTO.getSopInstanceUID());
            }

            return dicomProcessingLogRepository.save(latestLog);

        }

        if (status == EStatus.ERROR) {

            DicomProcessingLog newLog = new DicomProcessingLog();
            newLog.setSopInstanceUID(metaDataDicomFileDTO.getSopInstanceUID());
            newLog.setOriginalPath(metaDataDicomFileDTO.getFile().getAbsolutePath());
            newLog.setStatus(EStatus.ERROR);
            newLog.setErrorMessage(errorMessage);
            newLog.setProcessingCount(1);
            newLog.setLastProcessedDate(ZonedDateTime.now());

            logger.error("No log found for file ({}) and marking it as FAILED : {}", metaDataDicomFileDTO.getSopInstanceUID(), errorMessage);

            return dicomProcessingLogRepository.save(newLog);

        }
        return null;
    }


}
