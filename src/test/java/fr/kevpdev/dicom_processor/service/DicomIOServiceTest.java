package fr.kevpdev.dicom_processor.service;

import fr.kevpdev.dicom_processor.factory.DicomInputStreamFactory;
import fr.kevpdev.dicom_processor.factory.DicomOutputStreamFactory;
import fr.kevpdev.dicom_processor.service.image.DicomImageService;
import fr.kevpdev.dicom_processor.service.io.DicomIOService;
import fr.kevpdev.dicom_processor.service.rules.DicomMetaDataRulesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DicomIOServiceTest {

    @Mock
    private DicomImageService dicomImageService;

    @Mock
    private DicomInputStreamFactory dicomInputStreamFactory;

    @Mock
    private DicomOutputStreamFactory dicomOutputStreamFactory;

    @Mock
    private DicomMetaDataRulesService dicomMetaDataRulesService;

    @InjectMocks
    private DicomIOService dicomIOService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldAddProcessedSuffixToFileName() {
        String fileName = "1.2.826.0.1.3680043.2.1125.1.1.20220125100010.dcm";
        String suffix = "processed";
        String newFileName = dicomIOService.addSuffixToFileName(fileName, suffix);
        String expectedFileName = "1.2.826.0.1.3680043.2.1125.1.1.20220125100010_processed.dcm";
        Assertions.assertEquals(expectedFileName, newFileName);
    }

    @Test
    void shouldAddFailedSuffixToFileName() {
        String fileName = "1.2.826.0.1.3680043.2.1125.1.1.20220125100010.dcm";
        String suffix = "failed";
        String newFileName = dicomIOService.addSuffixToFileName(fileName, suffix);
        String expectedFileName = "1.2.826.0.1.3680043.2.1125.1.1.20220125100010_failed.dcm";
        Assertions.assertEquals(expectedFileName, newFileName);
    }
}