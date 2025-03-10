package fr.kevpdev.dicom_processor.dto;

import fr.kevpdev.dicom_processor.entity.EStatus;
import fr.kevpdev.dicom_processor.entity.EViewType;
import lombok.Builder;
import lombok.Data;

import java.io.File;

@Data
@Builder
public class MetaDataDicomFileDTO {
    private String sopInstanceUID;
    private File file;
    private EStatus status;
    private String errorMessage;
    private EViewType viewType;


    public MetaDataDicomFileDTO(String sopInstanceUID, File file, EStatus status, String errorMessage, EViewType viewType) {
        this.sopInstanceUID = sopInstanceUID;
        this.file = file;
        this.status = status;
        this.errorMessage = errorMessage;
        this.viewType = viewType;
    }

    public MetaDataDicomFileDTO(String sopInstanceUID, File file, EStatus status, String errorMessage) {
        this(sopInstanceUID, file, status, errorMessage, null);

    }

    public MetaDataDicomFileDTO(String sopInstanceUID, File file) {
        this(sopInstanceUID, file, null, null);
    }

    public MetaDataDicomFileDTO(String sopInstanceUID, File file, EStatus status) {
        this(sopInstanceUID, file, status, null);
    }

}

