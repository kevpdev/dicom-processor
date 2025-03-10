package fr.kevpdev.dicom_processor.service;

import fr.kevpdev.dicom_processor.entity.EViewType;
import fr.kevpdev.dicom_processor.service.rules.DicomMetaDataRulesService;
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
    void shouldGetFullView(String viewPosition) {
        Attributes dataset = new Attributes();
        dataset.setString(Tag.ViewPosition, VR.CS, viewPosition);
        Assertions.assertEquals(EViewType.FULL, dicomMetaDataRulesService.getViewType(dataset));
    }

    @ParameterizedTest
    @ValueSource(strings = {"SPOT", "MAG"})
    void shouldGetPartialView(String viewPosition) {
        Attributes dataset = new Attributes();
        dataset.setString(Tag.ViewPosition, VR.CS, viewPosition);
        Assertions.assertEquals(EViewType.PARTIAL, dicomMetaDataRulesService.getViewType(dataset));
    }

    @ParameterizedTest
    @ValueSource(strings = {"MLL", "MLA", "UNKNOWN"})
    void shouldGetUnknownWhenUnknownOrInvalid(String viewPosition) {
        Attributes dataset = new Attributes();
        dataset.setString(Tag.ViewPosition, VR.CS, viewPosition);
        Assertions.assertEquals(EViewType.UNKNOWN, dicomMetaDataRulesService.getViewType(dataset));
    }


}