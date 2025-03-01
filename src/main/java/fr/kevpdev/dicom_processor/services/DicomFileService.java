package fr.kevpdev.dicom_processor.services;

import fr.kevpdev.dicom_processor.repositories.DicomFileRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DicomFileService {

    private final DicomFileRepository dicomFileRepository;

    private Logger logger = LogManager.getLogger(DicomFileService.class);

    public DicomFileService(DicomFileRepository dicomFileRepository) {
        this.dicomFileRepository = dicomFileRepository;
    }

    /**
     * Check if file is already processed
     * @param sopInstanceUID SOP Instance UID
     * @return true if file is already processed
     */
    @Transactional
    public boolean isFileAlreadyProcessed(String sopInstanceUID) {
        return dicomFileRepository.existsById(sopInstanceUID);
    }


}
