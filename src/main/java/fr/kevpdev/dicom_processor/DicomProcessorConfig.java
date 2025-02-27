package fr.kevpdev.dicom_processor;

import fr.kevpdev.dicom_processor.services.DicomProcessorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ErrorMessage;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

@Configuration
public class DicomProcessorConfig {

    private static final Logger logger = LogManager.getLogger();

    @Value("${dicom.watcher.input.path}")
    private  String dicomWatcherInputPath;

    @Bean
    public IntegrationFlow fileWatcherFlow(DicomProcessorService dicomProcessorService) {
        return IntegrationFlow.from(fileReadingMessageSource(), c -> c.poller(
                        Pollers.fixedDelay(3000).errorHandler(e -> logger.error("Erreur poller : {}", e.getMessage()))
                ))
                .split()
                .channel(MessageChannels.executor(Executors.newFixedThreadPool(4)))
                .transform(dicomProcessorService::extractMetaData)
                .handle("dicomProcessorService", "verifyFileInDatabase")
                .handle("dicomProcessorService", "process")
                .get();
    }

    @Bean
    public FileReadingMessageSource fileReadingMessageSource() {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(dicomWatcherInputPath));
        logger.info("fileReadingMessageSource() source.getComponentType()={}", source.getComponentType());

        List<FileListFilter<File>> filters = List.of(
                new SimplePatternFileListFilter("*dcm"),
                new AcceptOnceFileListFilter<>()
        );
        //filters
        ChainFileListFilter<File> filter = new ChainFileListFilter<>(filters);
        source.setFilter(filter);

        return source;
    }

    @Bean
    public IntegrationFlow errorFlow() {
        return IntegrationFlow.from("errorChannel")
                .handle(message -> {
                    Throwable exception = (Throwable) message.getPayload();
                    Message<?> failedMessage = ((ErrorMessage) message).getOriginalMessage();
                    logger.error("Erreur capturée : {}", exception.getMessage());
                    if (failedMessage != null) {
                        logger.error("Message ayant échoué : {}", failedMessage);
                    }
                    // log in database with error message and status
                    // dicomFileService.save....
                })
                .get();
    }



}
