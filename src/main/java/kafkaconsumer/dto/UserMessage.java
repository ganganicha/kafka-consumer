package kafkaconsumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import kafkaconsumer.events.MigrateUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserMessage extends KafkaMessage {

    public MigrateUser data;

    public UserMessage() {
        super();
    }
}

