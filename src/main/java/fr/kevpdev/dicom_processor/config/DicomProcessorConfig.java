package fr.kevpdev.dicom_processor.config;

import fr.kevpdev.dicom_processor.service.DicomIOService;
import fr.kevpdev.dicom_processor.service.DicomMetaDataRulesService;
import fr.kevpdev.dicom_processor.service.DicomProcessingLogService;
import fr.kevpdev.dicom_processor.service.MetaDataExtractorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.handler.advice.ErrorMessageSendingRecoverer;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

@Configuration
public class DicomProcessorConfig {

    private static final Logger logger = LogManager.getLogger();

    @Value("${dicom.watcher.input.path}")
    private  String dicomWatcherInputPath;

    @Bean
    public IntegrationFlow fileWatcherFlow(MetaDataExtractorService metaDataExtractorService,
                                           DicomMetaDataRulesService dicomMetaDataRulesService,
                                           DicomIOService dicomIOService,
                                           DicomProcessingLogService dicomProcessingLogService) {
        return IntegrationFlow.from(fileReadingMessageSource(), c -> c.poller(
                        Pollers.fixedDelay(3000).errorChannel("errorChannel")
                ))
                .split()
                .channel(MessageChannels.executor(Executors.newFixedThreadPool(4)))
                .handle("metaDataExtractorService", "prepareMetaData", e -> e.advice(retryAdvice()))
                .handle("dicomProcessingLogService", "verifyFileInDatabase", e -> e.advice(retryAdvice()))
                .handle("dicomIOService", "readAndUpdateDicomFile", e -> e.advice(retryAdvice()))
                .handle("dicomIOService", "moveDicomFile", e -> e.advice(retryAdvice()))
                .get();
    }

    @Bean
    public FileReadingMessageSource fileReadingMessageSource() {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(dicomWatcherInputPath));
        logger.debug("fileReadingMessageSource() source.getComponentType()={}", source.getComponentType());

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
    public MessageChannel errorChannel() {
        return new DirectChannel();
    }

    //create bean RequestHandlerAdvice to redirect error to errorChannel without retry
    @Bean
    public RequestHandlerRetryAdvice retryAdvice() {
        RequestHandlerRetryAdvice retryAdvice = new RequestHandlerRetryAdvice();
        retryAdvice.setRecoveryCallback(new ErrorMessageSendingRecoverer(errorChannel()));
        return retryAdvice;
    }




}
