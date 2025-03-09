package fr.kevpdev.dicom_processor.util;

import fr.kevpdev.dicom_processor.exception.InvalidDicomFileException;

public class DicomFileUtil {

    /**
     * Extract file name whitout extension
     * @param fileName File name
     * @return File name without extension
     */
    public static String extractFileameWhitoutExtension(String fileName) {
        if(fileName == null || fileName.isEmpty()) {
            throw new InvalidDicomFileException("Filename is null or empty");
        }

        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf <=0) {
            throw new InvalidDicomFileException("File doesn't valid name with extension");
        }
        return fileName.substring(0, lastIndexOf);
    }
}
