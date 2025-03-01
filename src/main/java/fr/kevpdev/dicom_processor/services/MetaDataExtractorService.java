package fr.kevpdev.dicom_processor.services;

import fr.kevpdev.dicom_processor.exceptions.InvalidDicomFileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class MetaDataExtractorService {

    private Logger logger = LogManager.getLogger(MetaDataExtractorService.class);
    /**
     * Extract SOP Instance UID from DICOM filename
     * @param file DICOM file
     * @return SOP Instance UID
     */
    public String extractSOPInstanceUID(File file) {
        logger.info("extractSOPInstanceUID - file : {}", file.getAbsolutePath());
        int lastIndexOf = file.getName().lastIndexOf(".");
        if (lastIndexOf <=0) {
            throw new InvalidDicomFileException("File doesn't have a SOP Instance UID");
        }
        return file.getName().substring(0, lastIndexOf);
    }


    /**
     * Extract SOP Instance UID from metadata of DICOM file
     * @param file DICOM file
     * @return SOP Instance UID
     */
    public Optional<String> extractSOPInstanceUIDfromMetaData(File file) {
        logger.info("extractSOPInstanceUIDfromMetaData - file : {}", file.getAbsolutePath());
        try(DicomInputStream dis = new DicomInputStream(file)) {
            Attributes attributes = new Attributes();
            dis.readAttributes(attributes, -1, -1);

//            String studyDesc = attributes.getString(Tag.StudyDescription, "Inconnu");
//            String seriesDesc = attributes.getString(Tag.SeriesDescription, "Inconnu");
//            String bodyPart = attributes.getString(Tag.BodyPartExamined, "Inconnu");
//            String laterality = attributes.getString(Tag.Laterality, "Inconnu");
//            String viewPosition = attributes.getString(Tag.ViewPosition, "Inconnu");
//            String partialView = attributes.getString(Tag.PartialView, "Inconnu");
//            String viewModifierCodeSequence = attributes.getString(Tag.ViewModifierCodeSequence, "Inconnu");
//            String partialViewDescription = attributes.getString(Tag.PartialViewDescription, "Inconnu");
//            String partialViewCodeSequence = attributes.getString(Tag.PartialViewCodeSequence, "Inconnu");
            if(attributes.getString(Tag.SOPInstanceUID) != null){
              return Optional.of(attributes.getString(Tag.SOPInstanceUID));
            }
            
        } catch (IOException e) {
           logger.error("Error while reading DICOM file {}", file.getAbsolutePath(), e);
        }
           return Optional.empty();
    }


}
