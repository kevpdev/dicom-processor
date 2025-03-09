package fr.kevpdev.dicom_processor.exception;

public class InvalidDicomMetadataException extends RuntimeException {
    public InvalidDicomMetadataException(String message) {
        super(message);
    }
}
