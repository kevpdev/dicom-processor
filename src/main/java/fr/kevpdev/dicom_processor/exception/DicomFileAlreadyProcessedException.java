package fr.kevpdev.dicom_processor.exception;

public class DicomFileAlreadyProcessedException extends RuntimeException {
    public DicomFileAlreadyProcessedException(String message) {
        super(message);
    }
}
