package fr.kevpdev.dicom_processor.dto;

import java.io.File;


public record MetaDataDicomFileDTO(String sopInstanceUID, File file) {
}
