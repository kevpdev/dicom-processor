package fr.kevpdev.dicom_processor.handler;

import fr.kevpdev.dicom_processor.dto.MetaDataDicomFileDTO;
import fr.kevpdev.dicom_processor.exception.DicomFileWriteException;
import fr.kevpdev.dicom_processor.service.DicomIOService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class DicomErrorHandler {

    private final DicomIOService dicomIOService;

    private final Logger logger = LogManager.getLogger(DicomErrorHandler.class);

    public DicomErrorHandler(DicomIOService dicomIOService) {
        this.dicomIOService = dicomIOService;
    }

    @ServiceActivator(inputChannel = "errorChannel")
    public void handleError(Message<MessagingException> message) {
        Throwable exception = message.getPayload();
        Message<?> failedMessage = message.getPayload().getFailedMessage();
        logger.error("Erreur capturée : {}", exception.getMessage());

        if (failedMessage != null) {
            logger.error("Message ayant échoué : {}", failedMessage);
            MetaDataDicomFileDTO metaDataDicomFileDTO = (MetaDataDicomFileDTO) failedMessage.getPayload();
            String sopInstanceUID = metaDataDicomFileDTO.sopInstanceUID();
            logger.error("SOP Instance UID : {}", sopInstanceUID);

            // edit and save file in database with error message and status
            // dicomFileService.save....
            // move file to error folder
            try {
                dicomIOService.moveFileToTargetFolder(metaDataDicomFileDTO.file(), false);
            } catch (DicomFileWriteException e) {
                logger.error("Error while moving file to error folder", e);
            }

        }
    }
}
