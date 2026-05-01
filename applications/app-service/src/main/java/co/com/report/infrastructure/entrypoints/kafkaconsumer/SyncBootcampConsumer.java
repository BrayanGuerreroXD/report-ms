package co.com.report.infrastructure.entrypoints.kafkaconsumer;

import co.com.report.model.bootcamp.Bootcamp;
import co.com.report.usecase.bootcamp.SyncBootcampService;
import co.com.report.infrastructure.entrypoints.reactiveweb.BootcampDTOMapper;
import co.com.report.infrastructure.entrypoints.reactiveweb.BootcampEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncBootcampConsumer {

    private final SyncBootcampService syncBootcampService;
    private final BootcampDTOMapper bootcampMapper;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.bootcamp-report-sync}")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            BootcampEvent event = objectMapper.readValue(record.value(), BootcampEvent.class);
            Bootcamp bootcamp = bootcampMapper.toModel(event);
            syncBootcampService.sync(bootcamp)
                .subscribe(null, error -> log.error("Error syncing bootcamp {}: {}", event.getExternalId(), error.getMessage()));
        } catch (Exception e) {
            log.error("Error parsing bootcamp.report.sync message: {}", e.getMessage());
        }
    }
}