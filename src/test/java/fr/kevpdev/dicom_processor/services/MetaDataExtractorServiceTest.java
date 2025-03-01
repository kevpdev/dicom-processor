package fr.kevpdev.dicom_processor.services;

import fr.kevpdev.dicom_processor.services.generators.DicomTestFileGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


class MetaDataExtractorServiceTest {


    private MetaDataExtractorService metaDataExtractorService;

    @BeforeEach
    void setUp() {
        metaDataExtractorService = new MetaDataExtractorService();
    }

    @Test
    void shouldExtractSOPInstanceUID() throws IOException {

        String fileName = "1.2.826.0.1.3680043.2.1125.1.1.20220125100010";
        File file = DicomTestFileGenerator.generateValidDicomFile(fileName);
        String sopInstanceUID = metaDataExtractorService.extractSOPInstanceUID(file);
        Assertions.assertEquals(fileName, sopInstanceUID);
    }

    @Test
    void shouldExtractSOPInstanceUIDfromMetaData() throws IOException {

        String fileName = "1.2.826.0.1.3680043.2.1125.1.1.20220125100010";
        File file = DicomTestFileGenerator.generateValidDicomFile(fileName);
        Assertions.assertTrue(metaDataExtractorService.extractSOPInstanceUIDfromMetaData(file).isPresent());
        String sopInstanceUID = metaDataExtractorService.extractSOPInstanceUIDfromMetaData(file).get();
        Assertions.assertEquals("1.2.826.0.1.3680043.2.1125.1.1.20220125100010", sopInstanceUID);

    }

    @Test
    void shouldExtractfromMetaDataWhenFileIsEmpty() throws IOException {

        String fileName = "1.2.826.0.1.3680043.2.1125.1.1.20220125100010";
        File file = DicomTestFileGenerator.generateEmptyFile(fileName);
        Assertions.assertFalse(metaDataExtractorService.extractSOPInstanceUIDfromMetaData(file).isPresent());

    }

    @Test
    void shouldExtractUIDfromMetaDataWithoutSOPInstanceUID() throws IOException {
        String fileName = "1.2.826.0.1.3680043.2.1125.1.1.20220125100010";
        File file = DicomTestFileGenerator.generateDicomWithoutSOPInstanceUID(fileName);
        Assertions.assertFalse(metaDataExtractorService.extractSOPInstanceUIDfromMetaData(file).isPresent());
    }











}