package kafkaconsumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    public int id;
    @JsonProperty("post_id")
    public String postId;
    public String name;
    public String email;
    public String body;
}
