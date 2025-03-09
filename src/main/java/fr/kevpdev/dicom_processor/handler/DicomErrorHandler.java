package fr.kevpdev.dicom_processor.handler;

import fr.kevpdev.dicom_processor.dto.MetaDataDicomFileDTO;
import fr.kevpdev.dicom_processor.exception.DicomFileWriteException;
import fr.kevpdev.dicom_processor.service.DicomProcessingLogService;
import fr.kevpdev.dicom_processor.service.io.DicomIOService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class DicomErrorHandler {

    private final DicomIOService dicomIOService;

    private final DicomProcessingLogService dicomProcessingLogService;

    private final Logger logger = LogManager.getLogger(DicomErrorHandler.class);

    public DicomErrorHandler(DicomIOService dicomIOService, DicomProcessingLogService dicomProcessingLogService) {
        this.dicomIOService = dicomIOService;
        this.dicomProcessingLogService = dicomProcessingLogService;
    }

    @ServiceActivator(inputChannel = "errorChannel")
    public void handleError(Message<MessagingException> message) {
        Throwable rootCause = getRootCause(message.getPayload());
        String errorMessage = rootCause.getMessage();
        Message<?> failedMessage = message.getPayload().getFailedMessage();

        logger.error("captured error :", rootCause);

        if (failedMessage == null) {
            logger.error("Impossible of identify failed message.");
            return;
        }

        MetaDataDicomFileDTO metaDataDicomFileDTO = (MetaDataDicomFileDTO) failedMessage.getPayload();
        String sopInstanceUID = metaDataDicomFileDTO.sopInstanceUID();
        logger.error("SOP Instance UID : {}", sopInstanceUID);

        try {
            dicomProcessingLogService.saveLogWhenFileIsFailed(metaDataDicomFileDTO, errorMessage);
            dicomIOService.moveFileToTargetFolder(metaDataDicomFileDTO.file(), false);
        } catch (DicomFileWriteException e) {
            logger.error("Error while moving file to failed folder", e);
        }
    }

    /**
     * Get root cause of exception
     * @param throwable Throwable
     * @return Throwable
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

}
