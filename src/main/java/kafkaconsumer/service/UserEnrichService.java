package kafkaconsumer.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kafkaconsumer.dto.Comment;
import kafkaconsumer.events.MigrateUserEnriched;
import kafkaconsumer.dto.UserMessage;
import kafkaconsumer.dto.Post;
import kafkaconsumer.util.ForkJoinPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class UserEnrichService {


    @Value("${gorest.com.main.uri}")
    public String gorestUri;

    @Value("${spring.kafka.topic}")
    public String topic;

    private static Logger logger = LoggerFactory.getLogger(UserEnrichService.class);

    private IntegrationService integrationService;

    private CommentAsyncService commentAsyncService;

    private MigrateUserService migrateUserService;

    @Autowired
    public UserEnrichService(IntegrationService integrationService, CommentAsyncService commentAsyncService
            , MigrateUserService migrateUserService) {
        this.integrationService = integrationService;
        this.commentAsyncService = commentAsyncService;
        this.migrateUserService = migrateUserService;

    }

    @KafkaListener(topics = "com.company.vertical.migrate-user",
            groupId = "kafka-consumer")
    public void consume(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserMessage userMessage
                = objectMapper.readValue(message, UserMessage.class);
        logger.info(String.format("Message received -> %s", userMessage.getData().toString()));
        if (userMessage.getSubject().equals("MigrateUser")) {
            enrichedUser(String.valueOf(userMessage.getData().getUserId()));
        }

    }

    public Post[] getUserPosts(String userId) {
        URI uri = URI.create(gorestUri + "/users/" + userId + "/posts");
        return integrationService.callApi(Post[].class, uri);
    }

    public MigrateUserEnriched enrichedUser(String uerId) {
        MigrateUserEnriched migrateUserEnriched = new MigrateUserEnriched();
        migrateUserEnriched.setUserId(uerId);
        Post[] posts = getUserPosts(uerId);
        if (posts != null && posts.length > 0) {
            HashMap<String, List<Comment>> map = getCommentsForPosts(posts);
            List<MigrateUserEnriched.UserPost> userPosts = new ArrayList<>();
            for (Post post : posts) {
                MigrateUserEnriched.UserPost userPost = new MigrateUserEnriched.UserPost();
                BeanUtils.copyProperties(post, userPost);
                if (map.containsKey(post.getId())) {
                    userPost.setComments(map.get(post.getId()));
                }
                userPosts.add(userPost);
            }
            migrateUserEnriched.setPosts(userPosts);
            migrateUserService.migrateUser(migrateUserEnriched);
        }
        return migrateUserEnriched;
    }

    public HashMap<String, List<Comment>> getCommentsForPosts(Post[] posts) {
        long startTime = System.currentTimeMillis();

        List<CompletableFuture<List<Comment>>> allComments = new ArrayList<>();
        for (Post post : posts) {
            allComments.add(commentAsyncService.getComments(gorestUri, post.getId()));
        }
        List<Comment> comments = allComments.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream).collect(Collectors.toList());

        HashMap<String, List<Comment>> map = new HashMap<>();
        comments.forEach(c -> {
            if (map.containsKey(c.getPostId())) {
                map.get(c.getPostId()).add(c);
            } else {
                List<Comment> commentList = new ArrayList<>();
                commentList.add(c);
                map.put(c.getPostId(), commentList);
            }
        });
        logger.info("Time taken to get all comments  {} ms", System.currentTimeMillis() - startTime);
        return map;
    }


}
