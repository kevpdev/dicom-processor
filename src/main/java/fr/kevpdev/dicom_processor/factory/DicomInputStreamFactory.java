package fr.kevpdev.dicom_processor.factory;

import org.dcm4che3.io.DicomInputStream;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class DicomInputStreamFactory {
    public DicomInputStream createDicomInputStream(File file) throws IOException {
        return new DicomInputStream(file);
    }
}
