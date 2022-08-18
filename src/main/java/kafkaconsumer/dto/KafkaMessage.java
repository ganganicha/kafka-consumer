package kafkaconsumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaMessage {
    public String specversion;
    public String id;
    public String source;
    public String type;
    public String datacontenttype;
    public String subject;

}
