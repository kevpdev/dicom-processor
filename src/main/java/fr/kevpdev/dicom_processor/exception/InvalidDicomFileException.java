package fr.kevpdev.dicom_processor.exception;

public class InvalidDicomFileException extends RuntimeException{
    public InvalidDicomFileException(String message) {
        super(message);
    }
}
