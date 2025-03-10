package fr.kevpdev.dicom_processor.service.image.converter;

import fr.kevpdev.dicom_processor.exception.InvalidDicomMetadataException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Service
public class DicomImageConverterService {

    private static final String BITS_ALLOCATED_NOT_SUPPORTED = "Bits Allocated not supported : ";
    private final Logger logger = LogManager.getLogger(DicomImageConverterService.class);

    /**
     * Transform buffered image to pixel data byte array
     * Supported type : MONOCHROME2, MONOCHROME1, RGB
     * Supported bits allocated : 8, 16, 12
     * @param dicomImage  buffered image
     * @param bitsAllocated bits allocated
     * @return pixel data byte array
     */
    public byte[] convertBufferedImageToPixelDataByteArray(BufferedImage dicomImage, int bitsAllocated) {

        return switch (dicomImage.getType()) {
            case BufferedImage.TYPE_BYTE_GRAY -> convertBufferedImageByteGrayToDicomPixelData(dicomImage);
            case BufferedImage.TYPE_USHORT_GRAY -> convertBufferedImageUShortGrayToDicomPixelData(dicomImage, bitsAllocated);
            case BufferedImage.TYPE_3BYTE_BGR -> convertBufferedImageRGBToDicomPixelData(dicomImage);
            default -> throw new UnsupportedOperationException("Photometric Interpretation not supported : " + dicomImage.getType());
        };
    }

    /**
     *  Convert buffered image to pixel data byte array
     *  Supported type : MONOCHROME2
     *  Supported bits allocated : 8
     * @param dicomImage  buffered image
     * @return pixel data byte array
     */
    public byte[] convertBufferedImageByteGrayToDicomPixelData(BufferedImage dicomImage) {
        return ((DataBufferByte) dicomImage.getRaster().getDataBuffer()).getData();
    }

    /**
     * Convert buffered image to pixel data byte array
     * Supported type : MONOCHROME2 , MONOCHROME1
     * Supported bits allocated : 16 , 12
     * @param dicomImage  buffered image
     * @param bitsAllocated bits allocated
     * @return pixel data byte array
     */
    public byte[] convertBufferedImageUShortGrayToDicomPixelData(BufferedImage dicomImage, int bitsAllocated) {
        short[] shortSizeDicomImage = ((DataBufferUShort) dicomImage.getRaster().getDataBuffer()).getData();
        int maxSizeNewPixelData = shortSizeDicomImage.length * 2;
        byte[] newPixelData = new byte[maxSizeNewPixelData];

        for(int index = 0; index < shortSizeDicomImage.length; index++) {

            if(bitsAllocated == 16) {
                newPixelData[2 * index] = (byte) (shortSizeDicomImage[index] & 0xFF);
                newPixelData[2 * index + 1] = (byte) ((shortSizeDicomImage[index] >> 8) & 0xFF);

            }else if(bitsAllocated == 12) {
                newPixelData[2 * index] = (byte) ((shortSizeDicomImage[index]) & 0x0F);
                newPixelData[2 * index + 1] = (byte) ((shortSizeDicomImage[index] >> 8) & 0xFF);


            }else {
                throw new UnsupportedOperationException(BITS_ALLOCATED_NOT_SUPPORTED + bitsAllocated);
            }

        }

        return newPixelData;
    }

    /**
     * Convert buffered image to pixel data byte array
     * Supported type : RGB
     * Supported bits allocated : 8
     * @param dicomImage  buffered image
     * @return pixel data byte array
     */
    public byte[] convertBufferedImageRGBToDicomPixelData(BufferedImage dicomImage) {

        short[] shortSizeDicomImage = ((DataBufferUShort) dicomImage.getRaster().getDataBuffer()).getData();
        int maxSizeNewPixelData = shortSizeDicomImage.length * 3;
        byte[] newPixelData = new byte[maxSizeNewPixelData];

        for(int index = 0; index < shortSizeDicomImage.length; index++) {

            int red = shortSizeDicomImage[index] & 0xFF;
            int green = shortSizeDicomImage[index + 1] & 0xFF;
            int blue = shortSizeDicomImage[index + 2] & 0xFF;

            newPixelData[index * 3] = (byte) red;
            newPixelData[index * 3 + 1] = (byte) green;
            newPixelData[index * 3 + 2] = (byte) blue;
        }

        return newPixelData;
    }

    /**
     * Convert dicom dataset to buffered image
     * @param dataset
     * @param pixelData
     * @param bitsAllocated
     * @return buffered image
     */
    public BufferedImage convertToBufferedImage(Attributes dataset, byte[] pixelData, int bitsAllocated) {

        int width = dataset.getInt(Tag.Columns, -1);
        int height = dataset.getInt(Tag.Rows, -1);
        String photometric = dataset.getString(Tag.PhotometricInterpretation);

        if (width <= 0 || height <= 0 || bitsAllocated <= 0 || photometric == null) {
            throw new InvalidDicomMetadataException("Tags image missing or invalid");
        }

        return switch (photometric) {
            case "MONOCHROME2" , "MONOCHROME1" -> convertToBufferedImageMonochrome(pixelData, bitsAllocated, width, height);
            case "RGB" -> convertToBufferedImageRGB(pixelData, bitsAllocated, width, height);
            default -> throw new UnsupportedOperationException("Photometric Interpretation not supported : " + photometric);
        };
    }

    /**
     * Convert dicom dataset to buffered image monochrome
     * @param pixelData
     * @param bitsAllocated
     * @param width
     * @param height
     * @return buffered image
     */
    private BufferedImage convertToBufferedImageMonochrome(byte[] pixelData, int bitsAllocated,
                                                           int width, int height) {
        BufferedImage image;
        image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
        WritableRaster raster = image.getRaster();

        if (bitsAllocated == 8) {
            logger.debug("convertToBufferedImage - 8 bits");
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int index = y * width + x;
                    int pixel = pixelData[index] & 0xFF;  // 8 bits = 1 byte
                    raster.setSample(x, y, 0, pixel);
                }
            }
        } else if (bitsAllocated == 16) {
            logger.debug("convertToBufferedImage - 16 bits");
            ByteBuffer buffer = ByteBuffer.wrap(pixelData).order(ByteOrder.LITTLE_ENDIAN);  // DICOM = souvent little endian
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = buffer.getShort() & 0xFFFF;  // 16 bits = 2 bytes/pixel
                    raster.setSample(x, y, 0, pixel);
                }
            }
        } else {
            throw new UnsupportedOperationException(BITS_ALLOCATED_NOT_SUPPORTED + bitsAllocated);
        }

        return image;
    }

    /**
     * Convert dicom dataset to buffered image RGB
     * @param pixelData
     * @param bitsAllocated
     * @param width
     * @param height
     * @return buffered image
     */
    private BufferedImage convertToBufferedImageRGB(byte[] pixelData, int bitsAllocated,
                                                    int width, int height) {

        if (bitsAllocated != 8) {
            throw new UnsupportedOperationException("RGB superior to 8 bits not supported directly by BufferedImage");
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = image.getRaster();
        int samplesPerPixel = 3;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * samplesPerPixel;
                int blue = pixelData[index] & 0xFF;
                int green = pixelData[index + 1] & 0xFF;
                int red = pixelData[index + 2] & 0xFF;
                raster.setPixel(x, y, new int[]{blue, green, red});
            }
        }

        return image;
    }
}
