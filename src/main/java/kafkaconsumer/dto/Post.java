package kafkaconsumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

    public String id;
    @JsonProperty("user_id")
    public int userId;
    public String title;
    public String body;
}
