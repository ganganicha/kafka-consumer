package kafkaconsumer.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import kafkaconsumer.events.MigrateUserEnriched;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.UUID;


@Service
public class MigrateUserService {

    private static Logger logger = LoggerFactory.getLogger(MigrateUserService.class);

    private final static ObjectMapper mapper = new ObjectMapper();
    private final static String EVENT_SOURCE = "http://localhost";

    @Value("${spring.kafka.topic}")
    public String topic;

    private KafkaTemplate<String, String> producer;


    @Autowired
    public MigrateUserService(KafkaTemplate<String, String> producer){
        this.producer = producer;
    }

    private CloudEvent createCloudEvent(MigrateUserEnriched user) {
        return CloudEventBuilder.v1()
                .withType(user.getClass().getCanonicalName())
                .withSubject(user.getClass().getSimpleName())
                .withSource(URI.create(MigrateUserService.EVENT_SOURCE))
                .withId(UUID.randomUUID().toString())
                .withDataContentType(MediaType.APPLICATION_JSON.toString())
                .withData(PojoCloudEventData.wrap(user, mapper::writeValueAsBytes))
                .build();
    }
    public void migrateUser(MigrateUserEnriched migrateUser) {

        CloudEvent event = createCloudEvent(migrateUser);
        String cloudEvent = new String(EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE).serialize(event));
        logger.info("CloudEvent: {}", cloudEvent);
        producer.send(topic, cloudEvent);
    }


}
