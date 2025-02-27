package fr.kevpdev.dicom_processor.exceptions;

public class DicomFileReadException extends Exception{

    public DicomFileReadException(String message) {
        super(message);
    }
    public DicomFileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
