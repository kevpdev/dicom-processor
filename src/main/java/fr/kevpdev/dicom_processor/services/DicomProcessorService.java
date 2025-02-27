package fr.kevpdev.dicom_processor.services;

import fr.kevpdev.dicom_processor.dtos.MetaDataDicomFileDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DicomProcessorService {

    private Logger logger = LogManager.getLogger(DicomProcessorService.class);

    private DicomFileService dicomFileService;

    private MetaDataExtractorService metaDataExtractorService;

    public DicomProcessorService(DicomFileService dicomFileService,
                                 MetaDataExtractorService metaDataExtractorService) {
        this.dicomFileService = dicomFileService;
        this.metaDataExtractorService = metaDataExtractorService;
    }

    /**
     * Extract SOP Instance UID from DICOM file
     * @param file DICOM file
     * @return SOP Instance UID
     */
    public MetaDataDicomFileDTO extractMetaData(File file)  {
        logger.info("extractMetaData - Payload {}", file.getAbsolutePath());
        String sopInstanceUID = metaDataExtractorService.extractSOPInstanceUID(file);
        return new MetaDataDicomFileDTO(sopInstanceUID, file);
    }

    /**
     * Verify if file is already processed in database
     * @param metaDataDicomFileDTO MetaDataDicomFileDTO
     * @return MetaDataDicomFileDTO
     */
    public MetaDataDicomFileDTO verifyFileInDatabase(MetaDataDicomFileDTO metaDataDicomFileDTO) {
        logger.info("verifyFileInDatabase - Payload {}", metaDataDicomFileDTO);

        if(dicomFileService.isFileAlreadyProcessed(metaDataDicomFileDTO.sopInstanceUID())) {
            logger.info("File {} already processed", metaDataDicomFileDTO.file().getAbsolutePath());
            return null;
        }
        else {
            logger.info("File {} not processed", metaDataDicomFileDTO.file().getAbsolutePath());
            return metaDataDicomFileDTO;
        }
    }

    public void process(MetaDataDicomFileDTO metaDataDicomFileDTO) {
        logger.info("process - Payload {}", metaDataDicomFileDTO);

        // Lire le fichier dicom
        // Rechercher les tags (0002,0012) et (0002,0013) dans le fichier dicom
        // Si tag présent, ecrire dans un nouveau fichier pour modifier leur valeur
        // Enregistrer le nouveau fichier dans le dossier de traitement
        // Mettre à jour la table de la base de données avec le nouveau fichier traité
    }


}
