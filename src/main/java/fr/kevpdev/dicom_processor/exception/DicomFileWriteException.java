package fr.kevpdev.dicom_processor.exception;

import java.io.IOException;

public class DicomFileWriteException extends IOException {

    public DicomFileWriteException(String message) {
        super(message);
    }
    public DicomFileWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
