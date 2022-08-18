package kafkaconsumer.service;


import kafkaconsumer.dto.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Arrays;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CommentAsyncService {

    private static Logger logger = LoggerFactory.getLogger(CommentAsyncService.class);

    private IntegrationService integrationService;

    @Autowired
    public CommentAsyncService(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @Async("asyncExecutor")
    public CompletableFuture<List<Comment>> getComments(String gorestUri, String postId){
        URI uri = URI.create(gorestUri + "/posts/" + postId + "/comments");
        logger.info("Calling to get comments for Post {}", postId);
        Comment[] comments = integrationService.callApi(Comment[].class, uri);
        return CompletableFuture.completedFuture(Arrays.asList(comments));
    }

}
