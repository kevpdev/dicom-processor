package fr.kevpdev.dicom_processor.services.generators;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DicomTestFileGenerator {

    private static final String BASE_PATH = "src/test/resources/dcm/";

    /**
     * Generate a valid DICOM file
     * @return File generated
     * @throws IOException
     */
    public static File generateValidDicomFile(String fileName) throws IOException {

        Attributes attrs = new Attributes();
        attrs.setString(Tag.SOPInstanceUID, VR.UI, fileName);
        attrs.setString(Tag.Modality, VR.CS, "MR");

        File file = createFile(BASE_PATH + "valid/"+fileName+".dcm");
        try (DicomOutputStream dos = new DicomOutputStream(file)) {
            dos.writeDataset(null, attrs);
        }
        return file;
    }

    /**
     * Generate a DICOM file with empty content
     * @return File generated
     * @throws IOException
     */
    public static File generateEmptyFile(String fileName) throws IOException {
        File file = createFile(BASE_PATH + "empty/"+fileName+".dcm");
        Files.write(file.toPath(), new byte[0]);
        return file;
    }

    /**
     * Generate a DICOM file with invalid content
     * @return File generated
     * @throws IOException
     */
    public static File generateInvalidFile(String fileName) throws IOException {
        File file = createFile(BASE_PATH + "invalid/"+fileName+".dcm");
        Files.write(file.toPath(), "This is not a DICOM file".getBytes(StandardCharsets.UTF_8));
        return file;
    }

    /**
     * Generate a DICOM file without SOPInstanceUID
     * @return File generated
     * @throws IOException
     */
    public static File generateDicomWithoutSOPInstanceUID(String fileName) throws IOException {
        Attributes attrs = new Attributes();
        attrs.setString(Tag.Modality, VR.CS, "MR");

        File file = createFile(BASE_PATH + "missingTag/"+fileName+".dcm");
        try (DicomOutputStream dos = new DicomOutputStream(file)) {
            dos.writeDataset(null, attrs);
        }
        return file;
    }

    /**
     * Generate a DICOM file with invalid SO
     * @param path
     * @return File generated
     * @throws IOException
     */
    private static File createFile(String path) throws IOException {
        File file = new File(path);
        Files.createDirectories(file.getParentFile().toPath());
        if (!file.exists()) {
            Files.createFile(Paths.get(path));
        }
        return file;
    }
}
