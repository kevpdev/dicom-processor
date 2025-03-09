package fr.kevpdev.dicom_processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude = { org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class })
@SpringBootApplication
public class DicomProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DicomProcessorApplication.class, args);
	}

}
