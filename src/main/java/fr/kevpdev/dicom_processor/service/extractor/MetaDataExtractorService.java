package fr.kevpdev.dicom_processor.service.extractor;

import fr.kevpdev.dicom_processor.dto.MetaDataDicomFileDTO;
import fr.kevpdev.dicom_processor.exception.DicomFileReadException;
import fr.kevpdev.dicom_processor.exception.InvalidDicomMetadataException;
import fr.kevpdev.dicom_processor.factory.DicomInputStreamFactory;
import fr.kevpdev.dicom_processor.util.DicomFileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class MetaDataExtractorService {


    private final Logger logger = LogManager.getLogger(MetaDataExtractorService.class);

    private final DicomInputStreamFactory dicomInputStreamFactory;

    public MetaDataExtractorService(DicomInputStreamFactory dicomInputStreamFactory) {
        this.dicomInputStreamFactory = dicomInputStreamFactory;
    }
    /**
     * Extract SOP Instance UID from DICOM filename
     * @param file DICOM file
     * @return SOP Instance UID
     */
    public String extractSOPInstanceUID(File file) {
        return DicomFileUtil.extractFileameWhitoutExtension(file.getName());
    }


    /**
     * Extract SOP Instance UID from metadata of DICOM file
     * @param file DICOM file
     * @return SOP Instance UID
     */
    public String extractSOPInstanceUIDfromMetaData(File file) throws DicomFileReadException {
        try(DicomInputStream dis = dicomInputStreamFactory.createDicomInputStream(file)) {
            Attributes attributes = dis.readDataset();


            String sopInstanceUID = (attributes != null) ? attributes.getString(Tag.SOPInstanceUID) : null;

            if (sopInstanceUID == null) {
                throw new InvalidDicomMetadataException("No SOP Instance UID found in DICOM file "
                        + file.getAbsolutePath());
            }

            return sopInstanceUID;
            
        } catch (IOException e) {
           throw new DicomFileReadException("Error while reading DICOM file " + file.getAbsolutePath(), e);
        }

    }


    /**
     * Extract SOP Instance UID from DICOM file
     * @param file DICOM file
     * @return SOP Instance UID and DICOM file in MetaDataDicomFileDTO
     */
    public MetaDataDicomFileDTO prepareMetaData(File file) throws DicomFileReadException {
        logger.debug("ExtractMetaData - Payload {}", file.getAbsolutePath());
        String sopInstanceUID = extractSOPInstanceUIDfromMetaData(file);
        return new MetaDataDicomFileDTO(sopInstanceUID, file);
    }


}
