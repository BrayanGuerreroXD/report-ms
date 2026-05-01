package co.com.report.infrastructure.entrypoints.kafkaconsumer;

import co.com.report.model.bootcamp.Bootcamp;
import co.com.report.usecase.bootcamp.SyncBootcampService;
import co.com.report.infrastructure.entrypoints.reactiveweb.BootcampDTOMapper;
import co.com.report.infrastructure.entrypoints.reactiveweb.BootcampEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncBootcampConsumerTest {

    @Mock
    private SyncBootcampService syncBootcampService;

    @Mock
    private BootcampDTOMapper bootcampMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SyncBootcampConsumer consumer;

    @Test
    void consume_whenEventIsValid_shouldCallSync() throws Exception {
        String json = "{\"externalId\":1,\"name\":\"Test\"}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("bootcamp.report.sync", 0, 0, null, json);
        BootcampEvent event = BootcampEvent.builder().externalId(1L).name("Test").build();
        Bootcamp bootcamp = Bootcamp.builder().externalId(1L).name("Test").build();

        when(objectMapper.readValue(json, BootcampEvent.class)).thenReturn(event);
        when(bootcampMapper.toModel(event)).thenReturn(bootcamp);
        when(syncBootcampService.sync(bootcamp)).thenReturn(Mono.just(bootcamp));

        consumer.consume(record);

        verify(syncBootcampService).sync(bootcamp);
    }
}