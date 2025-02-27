package fr.kevpdev.dicom_processor.exceptions;

public class InvalidDicomFileException extends RuntimeException{
    public InvalidDicomFileException(String message) {
        super(message);
    }
}
