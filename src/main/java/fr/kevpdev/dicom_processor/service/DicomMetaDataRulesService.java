package fr.kevpdev.dicom_processor.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DicomMetaDataRulesService {

    private final Logger logger = LogManager.getLogger(DicomMetaDataRulesService.class);

    /**
     * Check if the viewPosition is CC, MLO or ML
     * @param dataset DICOM dataset
     * @return  true if the viewPosition is CC, MLO or ML
     */
    public boolean isCompleteMammography(Attributes dataset) {
        List<String> viewPositionValues = List.of("CC", "MLO", "ML");
        String viewPosition = dataset.getString(Tag.ViewPosition);
        if(viewPosition == null || viewPosition.isEmpty()) {
            logger.warn("isCompleteMammography - ViewPosition is null or empty");
            return false;
        }
        logger.debug("isCompleteMammography - viewPosition - {}", viewPosition);
        return viewPositionValues.contains(viewPosition);

    }



}
