package fr.kevpdev.dicom_processor.service;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DicomMetaDataRulesServiceTest {

    private DicomMetaDataRulesService dicomMetaDataRulesService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        dicomMetaDataRulesService = new DicomMetaDataRulesService();
    }

    @ParameterizedTest
    @ValueSource(strings = {"CC", "MLO", "ML"})
    void shouldIsCompleteMammographyWhenViewPositionIs(String viewPosition) {
        Attributes dataset = new Attributes();
        dataset.setString(Tag.ViewPosition, VR.CS, viewPosition);
        Assertions.assertTrue(dicomMetaDataRulesService.isCompleteMammography(dataset));
    }

    @ParameterizedTest
    @ValueSource(strings = {"MLL", "MLA", ""})
    void shouldIsCompleteMammographyWhenViewPositionIsInvalid(String viewPosition) {
        Attributes dataset = new Attributes();
        dataset.setString(Tag.ViewPosition, VR.CS, viewPosition);
        Assertions.assertFalse(dicomMetaDataRulesService.isCompleteMammography(dataset));
    }

}