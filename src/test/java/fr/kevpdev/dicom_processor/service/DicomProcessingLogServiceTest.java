package fr.kevpdev.dicom_processor.service;

import fr.kevpdev.dicom_processor.repository.DicomProcessingLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DicomProcessingLogServiceTest {

    @Mock
    private DicomProcessingLogRepository dicomProcessingLogRepository;

    @Mock
    private DicomProcessingLogService self;

    @InjectMocks
    private DicomProcessingLogService dicomProcessingLogService;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(self, "self", self);
    }

//    @Test
//    void shouldVerifyFileInDatabaseWhenNoEntryExists() throws IOException {
//        String sopInstanceUID = "1.2.826.0.1.3680043.2.1125.1.1.20220125100010";
//        File file = DicomTestFileGenerator.generateValidDicomFile(sopInstanceUID);
//
//
//        when(dicomProcessingLogRepository.findBySopInstanceUIDAndLimitOne(sopInstanceUID)).thenReturn(Optional.empty());
//        //TODO
//
//
//
//        verify(dicomProcessingLogRepository).findBySopInstanceUIDAndLimitOne(sopInstanceUID);
//    }







}