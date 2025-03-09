package fr.kevpdev.dicom_processor.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PropertyConfig {

    @Value("${dicom.watcher.input.path}")
    private  String dicomWatcherInputPath;

    @Value("${dicom.watcher.processed.path}")
    private String dicomProcessedPath;

    @Value("${dicom.watcher.logo.path}")
    private String dicomLogoPath;

    @Value("${dicom.watcher.failed.path}")
    private String dicomFailedPath;



}
