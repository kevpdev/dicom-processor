package fr.kevpdev.dicom_processor.service.image;

import fr.kevpdev.dicom_processor.config.PropertyConfig;
import fr.kevpdev.dicom_processor.exception.DicomFileWriteException;
import fr.kevpdev.dicom_processor.service.image.converter.DicomImageConverterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DicomImageService {
    
    private final PropertyConfig propertyConfig;
    
    private final DicomImageConverterService dicomImageConverterService;

    private final Logger logger = LogManager.getLogger(DicomImageService.class);
    
    public DicomImageService(PropertyConfig propertyConfig, DicomImageConverterService dicomImageConverterService) {
        this.propertyConfig = propertyConfig;
        this.dicomImageConverterService = dicomImageConverterService;
    }

    /**
     * Add logo image and processing info to DICOM file
     * @param dataset
     * @throws IOException
     */
    public void addLogoImageAndProcessingInfo(Attributes dataset) throws IOException {

        byte[] pixelData = dataset.getBytes(Tag.PixelData);
        BufferedImage logoImage = ImageIO.read(new File(propertyConfig.getDicomLogoPath()));
        int bitsAllocated = dataset.getInt(Tag.BitsAllocated, -1);
        BufferedImage dicomImage = dicomImageConverterService.convertToBufferedImage(dataset, pixelData, bitsAllocated);
        String laterality = dataset.getString(Tag.Laterality);
        String imageLaterality = dataset.getString(Tag.ImageLaterality);

        logger.debug("addLogoAndLegendToDicomFile - laterality - {}", laterality);
        logger.debug("addLogoAndLegendToDicomFile - imageLaterality - {}", imageLaterality);

        int margin = dicomImage.getWidth() / 10;
        int x = calculateAxisX(margin, laterality, imageLaterality, dicomImage, logoImage);
        int y = calculateAxisY(margin, dicomImage, logoImage);


        drawLogoImageToDicomImage(dicomImage, logoImage, x, y, margin);

        byte[] newPixelData = dicomImageConverterService.convertBufferedImageToPixelDataByteArray(dicomImage, bitsAllocated);

        dataset.setBytes(Tag.PixelData, VR.OB, newPixelData);

    }



    /**
     * Draw logo image to dicom image
     * @param dicomImage
     * @param logoImage
     * @param x
     * @param y
     */
    private void drawLogoImageToDicomImage(BufferedImage dicomImage, BufferedImage logoImage, int x, int y, int margin) {

        int textMargin = margin/4;
        int yText = y + logoImage.getHeight() + textMargin;
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String processingDate = "Processing date : " + currentDate;

        Graphics2D g2d = dicomImage.createGraphics();
        g2d.drawImage(logoImage, x, y, null);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        g2d.drawString(processingDate, x, yText);
        g2d.dispose();
    }

    /**
     * Calculate axis x
     * check if laterality is L or R else throw exception
     * @param margin
     * @param laterality
     * @param imageLaterality
     * @param dicomImage
     * @param logoImage
     * @return axis x
     * @throws DicomFileWriteException
     */
    private int calculateAxisX(int margin, String laterality, String imageLaterality, BufferedImage dicomImage, BufferedImage logoImage) throws DicomFileWriteException {

        if(laterality.equals("L") || imageLaterality.equals("L")) {
            return dicomImage.getWidth() - margin - logoImage.getWidth();
        }
        else if(laterality.equals("R") || imageLaterality.equals("R")) {
            return margin;
        }

        throw new DicomFileWriteException("Laterality not defined, impossible to determine position of logo");
    }

    /**
     * Calculate axis y
     * @param margin
     * @param dicomImage
     * @param logoImage
     * @return axis y
     */
    private int calculateAxisY(int margin, BufferedImage dicomImage, BufferedImage logoImage) {
        return dicomImage.getHeight() - margin - logoImage.getHeight();
    }
}
