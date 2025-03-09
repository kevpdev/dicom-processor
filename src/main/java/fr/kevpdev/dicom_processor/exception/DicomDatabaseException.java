package fr.kevpdev.dicom_processor.exception;

public class DicomDatabaseException extends RuntimeException {
    public DicomDatabaseException(String message) {
        super(message);
    }
    public DicomDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
