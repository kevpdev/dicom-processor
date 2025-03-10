package fr.kevpdev.dicom_processor.service.rules;

import fr.kevpdev.dicom_processor.entity.EViewType;
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
     * Get the view type of the DICOM dataset
     * CC, MLO, ML, SPOT, MAG
     * @param dataset DICOM dataset
     * @return EViewType
     */
    public EViewType getViewType(Attributes dataset) {
        String viewPosition = dataset.getString(Tag.ViewPosition);

        if (viewPosition == null || viewPosition.isEmpty() || viewPosition.equalsIgnoreCase("UNKNOWN")) {
            logger.warn("getViewType - ViewPosition is null, empty, or explicitly set to UNKNOWN");
            return EViewType.UNKNOWN;
        }

        List<String> fullViewValues = List.of("CC", "MLO", "ML");
        List<String> partialViewValues = List.of("SPOT", "MAG");

        EViewType viewType;
        if (fullViewValues.contains(viewPosition)) {
            viewType = EViewType.FULL;
        } else if (partialViewValues.contains(viewPosition)) {
            viewType = EViewType.PARTIAL;
        } else {
            viewType = EViewType.UNKNOWN;
            logger.warn("ViewPosition is not recognized");
        }

        logger.debug("getViewType - get mammography type - viewPosition - {}", viewPosition);
        return viewType;
    }




}
