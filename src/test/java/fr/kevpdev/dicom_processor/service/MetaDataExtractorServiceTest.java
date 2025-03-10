package fr.kevpdev.dicom_processor.service;

import fr.kevpdev.dicom_processor.dto.MetaDataDicomFileDTO;
import fr.kevpdev.dicom_processor.exception.DicomFileReadException;
import fr.kevpdev.dicom_processor.factory.DicomInputStreamFactory;
import fr.kevpdev.dicom_processor.service.extractor.MetaDataExtractorService;
import generators.DicomTestFileGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;


class MetaDataExtractorServiceTest {


    private MetaDataExtractorService metaDataExtractorService;
    private DicomInputStreamFactory dicomInputStreamFactory;

    @BeforeEach
    void setUp() {

        dicomInputStreamFactory = new DicomInputStreamFactory();
        metaDataExtractorService = spy(new MetaDataExtractorService(dicomInputStreamFactory));
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
        String sopInstanceUID = metaDataExtractorService.extractSOPInstanceUIDfromMetaData(file);
        Assertions.assertEquals("1.2.826.0.1.3680043.2.1125.1.1.20220125100010", sopInstanceUID);

    }

    @Test
    void shouldExtractSOPInstanceUIDfromMetaDataWhenReadException() throws IOException {
        File file = DicomTestFileGenerator.generateInvalidFile("1.2.826.0.1.3680043.2.1125.1.1.20220125100010");
        DicomInputStreamFactory mockDicomInputStreamFactory = mock(DicomInputStreamFactory.class);
        doThrow(IOException.class).when(mockDicomInputStreamFactory).createDicomInputStream(file);
        Assertions.assertThrows(DicomFileReadException.class, () -> metaDataExtractorService.extractSOPInstanceUIDfromMetaData(file));

    }

    @Test
    void shouldprepareMetaData() throws IOException {
        File file = DicomTestFileGenerator.generateValidDicomFile("1.2.826.0.1.3680043.2.1125.1.1.20220125100010");

        when(metaDataExtractorService.extractSOPInstanceUIDfromMetaData(file)).thenReturn("1.2.826.0.1.3680043.2.1125.1.1.20220125100010");

        MetaDataDicomFileDTO metaDataDicomFileDTO = metaDataExtractorService.prepareMetaData(file);

        Assertions.assertEquals("1.2.826.0.1.3680043.2.1125.1.1.20220125100010", metaDataDicomFileDTO.getSopInstanceUID());
        Assertions.assertEquals(file, metaDataDicomFileDTO.getFile());

        verify(metaDataExtractorService).extractSOPInstanceUIDfromMetaData(file);

    }











}