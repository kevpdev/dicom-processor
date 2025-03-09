package fr.kevpdev.dicom_processor.service;

import fr.kevpdev.dicom_processor.repository.DicomProcessingLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.spy;

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