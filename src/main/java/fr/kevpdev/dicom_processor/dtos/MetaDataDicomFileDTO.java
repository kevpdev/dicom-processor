package fr.kevpdev.dicom_processor.dtos;

import java.io.File;


public record MetaDataDicomFileDTO(String sopInstanceUID, File file) {
}
