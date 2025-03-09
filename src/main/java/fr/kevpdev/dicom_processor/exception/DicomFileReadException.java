package fr.kevpdev.dicom_processor.exception;

import java.io.IOException;

public class DicomFileReadException extends IOException {

    public DicomFileReadException(String message) {
        super(message);
    }
    public DicomFileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
