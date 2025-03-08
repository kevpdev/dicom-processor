package fr.kevpdev.dicom_processor.service;

import fr.kevpdev.dicom_processor.config.PropertyConfig;
import fr.kevpdev.dicom_processor.dto.MetaDataDicomFileDTO;
import fr.kevpdev.dicom_processor.exception.DicomFileReadException;
import fr.kevpdev.dicom_processor.exception.DicomFileWriteException;
import fr.kevpdev.dicom_processor.factory.DicomInputStreamFactory;
import fr.kevpdev.dicom_processor.factory.DicomOutputStreamFactory;
import fr.kevpdev.dicom_processor.util.DicomFileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class DicomIOService {

    private final PropertyConfig propertyConfig;

    private final DicomImageService dicomImageService;

    private final DicomMetaDataRulesService dicomMetaDataRulesService;

    private final DicomInputStreamFactory dicomInputStreamFactory;

    private final DicomOutputStreamFactory dicomOutputStreamFactory;

    private final Logger logger = LogManager.getLogger(DicomIOService.class);

    public DicomIOService(PropertyConfig propertyConfig, DicomImageService dicomImageService,
                          DicomMetaDataRulesService dicomMetaDataRulesService,
                          DicomInputStreamFactory dicomInputStreamFactory,
                          DicomOutputStreamFactory dicomOutputStreamFactory) {

        this.propertyConfig = propertyConfig;
        this.dicomImageService = dicomImageService;
        this.dicomMetaDataRulesService = dicomMetaDataRulesService;
        this.dicomInputStreamFactory = dicomInputStreamFactory;
        this.dicomOutputStreamFactory = dicomOutputStreamFactory;
    }

    /**
     * Read and update DICOM file
     * @param metaDataDicomFileDTO MetaDataDicomFileDTO
     * @return MetaDataDicomFileDTO
     * @throws DicomFileReadException
     */
    public MetaDataDicomFileDTO readAndUpdateDicomFile(MetaDataDicomFileDTO metaDataDicomFileDTO)
            throws DicomFileReadException {

        File originalFile = metaDataDicomFileDTO.file();

        try(DicomInputStream dis = dicomInputStreamFactory.createDicomInputStream(originalFile)) {
            Attributes fmi = dis.readFileMetaInformation();
            Attributes dataset = dis.readDataset();
            boolean isCompleteMammography = dicomMetaDataRulesService.isCompleteMammography(dataset);

            updateDicomDataset(metaDataDicomFileDTO.file(), dataset, fmi, isCompleteMammography);

            logger.info("End process - Payload {}", metaDataDicomFileDTO);

        }catch (IOException e) {
            throw new DicomFileReadException("Error while reading DICOM file " + originalFile.getAbsolutePath(), e);
        }

        return metaDataDicomFileDTO;
    }

    /**
     * Update DICOM dataset
     * @param originalFile DICOM file
     * @param dataset DICOM dataset
     * @param fmi FileMetaInformation
     */
    public void updateDicomDataset(File originalFile, Attributes dataset, Attributes fmi, boolean isCompleteMammography)
            throws DicomFileWriteException {
        logger.debug("UpdateDicomDataset - file : {}", originalFile.getAbsolutePath());

        String newOutPutFileName = DicomFileUtil.extractFileameWhitoutExtension(originalFile.getName()) + "_processed.dcm";
        File newOutPutFile = new File(propertyConfig.getDicomProcessedPath(), newOutPutFileName);

        try(DicomOutputStream dos = dicomOutputStreamFactory.createDicomOutputStream(newOutPutFile)) {
            dataset.setString(Tag.ImplementationClassUID, VR.UI, "1.2.3.4.56");
            dataset.setString(Tag.ImplementationVersionName, VR.SH, "Hera-MI");

            if(isCompleteMammography) {
                dicomImageService.addLogoImageAndProcessingInfo(dataset);
            }
            dos.writeDataset(fmi, dataset);


        }catch (IOException e) {
            throw new DicomFileWriteException("Error while writing DICOM file " + originalFile.getAbsolutePath(), e);
        }
    }

    /**
     * Move DICOM file to target folder
     * @param metaDataDicomFileDTO MetaDataDicomFileDTO
     * @throws DicomFileWriteException
     */
    public void moveDicomFile(MetaDataDicomFileDTO metaDataDicomFileDTO) throws DicomFileWriteException {
         moveFileToTargetFolder(metaDataDicomFileDTO.file(), true);
    }

    /**
     *  Move file to target folder
     * @param dicomFileToMove File to move
     * @param isSuccess true if file is success, false if file is failed
     * @throws DicomFileWriteException
     */
    public void moveFileToTargetFolder(File dicomFileToMove, boolean isSuccess) throws DicomFileWriteException {
        String targetFolder = isSuccess ? propertyConfig.getDicomProcessedPath() : propertyConfig.getDicomFailedPath();
        String suffix = isSuccess ? "processed" : "failed";

        moveFileToFolder(dicomFileToMove, targetFolder, suffix);
    }

    /**
     * Move file to target folder
     * @param file File to move
     * @param targetFolder Target folder
     * @param suffix Suffix of the new file name
     * @throws DicomFileWriteException
     */
    private void moveFileToFolder(File file, String targetFolder, String suffix) throws DicomFileWriteException {
        Path sourcePath = file.toPath();
        String newFileName = addSuffixToFileName(file.getName(), suffix);
        Path targetPath = Path.of(targetFolder, newFileName);

        try {
            Files.createDirectories(targetPath.getParent());
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File {} moved to {}", file.getAbsolutePath(), targetPath.toFile().getAbsolutePath());
        } catch (IOException e) {
            throw new DicomFileWriteException("Error while moving file " + file.getAbsolutePath(), e);
        }
    }

    /**
     *  Add suffix to file name
     * @param fileName File name
     * @param suffix Suffix
     * @return File name with suffix
     */
    private String addSuffixToFileName(String fileName, String suffix) {
        return fileName.replaceAll("\\.dcm$", "_" + suffix + ".dcm");
    }
}
