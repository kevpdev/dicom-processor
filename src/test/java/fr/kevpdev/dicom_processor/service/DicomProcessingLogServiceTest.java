package fr.kevpdev.dicom_processor.service;

import fr.kevpdev.dicom_processor.dto.MetaDataDicomFileDTO;
import fr.kevpdev.dicom_processor.exception.DicomFileAlreadyProcessedException;
import fr.kevpdev.dicom_processor.repository.DicomProcessingLogRepository;
import fr.kevpdev.dicom_processor.service.generators.DicomTestFileGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DicomProcessingLogServiceTest {

    @Mock
    private DicomProcessingLogRepository dicomProcessingLogRepository;

    @InjectMocks
    private DicomProcessingLogService dicomProcessingLogService;


    @BeforeEach
    void setUp() {
        dicomProcessingLogService = spy(new DicomProcessingLogService(dicomProcessingLogRepository));
    }

    @Test
    void shouldVerifyFileInDatabase() throws IOException {
        File file = DicomTestFileGenerator.generateValidDicomFile("1.2.826.0.1.3680043.2.1125.1.1.20220125100010");
        when(dicomProcessingLogService.isFileAlreadyProcessed("1.2.826.0.1.3680043.2.1125.1.1.20220125100010")).thenReturn(false);
        MetaDataDicomFileDTO metaDataDicomFileDTO = dicomProcessingLogService.verifyFileInDatabase(new MetaDataDicomFileDTO("1.2.826.0.1.3680043.2.1125.1.1.20220125100010", file));
        Assertions.assertEquals(file, metaDataDicomFileDTO.file());
        verify(dicomProcessingLogService).isFileAlreadyProcessed("1.2.826.0.1.3680043.2.1125.1.1.20220125100010");
    }

    @Test
    void shouldVerifyFileInDatabaseWhenFileIsAlreadyProcessed() throws IOException {
        String fileName = "1.2.826.0.1.3680043.2.1125.1.1.20220125100010";
        File file = DicomTestFileGenerator.generateValidDicomFile(fileName);
        MetaDataDicomFileDTO metaData = new MetaDataDicomFileDTO(fileName, file);

        when(dicomProcessingLogService.isFileAlreadyProcessed(fileName)).thenReturn(true);

        Assertions.assertThrows(DicomFileAlreadyProcessedException.class,
                () -> dicomProcessingLogService.verifyFileInDatabase(metaData));

        verify(dicomProcessingLogService).isFileAlreadyProcessed(fileName);
    }


}