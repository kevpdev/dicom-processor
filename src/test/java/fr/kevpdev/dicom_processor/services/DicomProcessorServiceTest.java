package fr.kevpdev.dicom_processor.services;

import fr.kevpdev.dicom_processor.dtos.MetaDataDicomFileDTO;
import fr.kevpdev.dicom_processor.services.generators.DicomTestFileGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DicomProcessorServiceTest {

    DicomProcessorService dicomProcessorService;

    DicomFileService dicomFileService;

    MetaDataExtractorService metaDataExtractorService;

    @BeforeEach
    void setUp() {
        dicomFileService = mock(DicomFileService.class);
        metaDataExtractorService = mock(MetaDataExtractorService.class);
        dicomProcessorService = new DicomProcessorService(dicomFileService, metaDataExtractorService);

    }

    @Test
    void shouldExtractMetaData() throws IOException {
     File file = DicomTestFileGenerator.generateValidDicomFile("1.2.826.0.1.3680043.2.1125.1.1.20220125100010");

     when(metaDataExtractorService.extractSOPInstanceUID(file)).thenReturn("1.2.826.0.1.3680043.2.1125.1.1.20220125100010");
     MetaDataDicomFileDTO metaDataDicomFileDTO = dicomProcessorService.extractMetaData(file);

     Assertions.assertEquals("1.2.826.0.1.3680043.2.1125.1.1.20220125100010", metaDataDicomFileDTO.sopInstanceUID());
     Assertions.assertEquals(file, metaDataDicomFileDTO.file());
     verify(metaDataExtractorService).extractSOPInstanceUID(file);

    }

    @Test
    void shouldVerifyFileInDatabase() throws IOException {
        File file = DicomTestFileGenerator.generateValidDicomFile("1.2.826.0.1.3680043.2.1125.1.1.20220125100010");
        when(dicomFileService.isFileAlreadyProcessed("1.2.826.0.1.3680043.2.1125.1.1.20220125100010")).thenReturn(false);
        MetaDataDicomFileDTO metaDataDicomFileDTO = dicomProcessorService.verifyFileInDatabase(new MetaDataDicomFileDTO("1.2.826.0.1.3680043.2.1125.1.1.20220125100010", file));
        Assertions.assertEquals(file, metaDataDicomFileDTO.file());
        verify(dicomFileService).isFileAlreadyProcessed("1.2.826.0.1.3680043.2.1125.1.1.20220125100010");
    }

    @Test
    void shouldVerifyFileInDatabaseWhenFileIsAlreadyProcessed() throws IOException {
        File file = DicomTestFileGenerator.generateValidDicomFile("1.2.826.0.1.3680043.2.1125.1.1.20220125100010");
        when(dicomFileService.isFileAlreadyProcessed("1.2.826.0.1.3680043.2.1125.1.1.20220125100010")).thenReturn(true);
        MetaDataDicomFileDTO metaDataDicomFileDTO = dicomProcessorService.verifyFileInDatabase(new MetaDataDicomFileDTO("1.2.826.0.1.3680043.2.1125.1.1.20220125100010", file));
        Assertions.assertNull(metaDataDicomFileDTO);
        verify(dicomFileService).isFileAlreadyProcessed("1.2.826.0.1.3680043.2.1125.1.1.20220125100010");
    }

}