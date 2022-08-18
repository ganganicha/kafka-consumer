package kafkaconsumer.events;

import kafkaconsumer.dto.Comment;
import lombok.Data;

import java.util.List;

@Data
public class MigrateUserEnriched {

    String userId;
    List<UserPost> posts;

    @Data
    public static class UserPost {
        String id;
        String title;
        String body;
        List<Comment> comments;
    }
}
