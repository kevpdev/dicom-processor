package fr.kevpdev.dicom_processor.factory;

import org.dcm4che3.io.DicomOutputStream;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class DicomOutputStreamFactory {
    public DicomOutputStream createDicomOutputStream(File file) throws IOException {
        return new DicomOutputStream(file);
    }
}
