package kafkaconsumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import kafkaconsumer.dto.Comment;
import kafkaconsumer.dto.Post;
import kafkaconsumer.events.MigrateUserEnriched;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserEnrichServiceTest {

    @Mock
    IntegrationService integrationService;
    @InjectMocks
    UserEnrichService userEnrichService;
    private Post[] posts;
    private Comment[] comments;
    @Mock
    CommentAsyncService commentAsyncService;
    @Mock
    MigrateUserService migrateUserService;

    @BeforeEach
    void setup() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        posts = objectMapper.
                readValue(new File("src/test/resources/PostResponse.json"), Post[].class);
        comments = objectMapper.
                readValue(new File("src/test/resources/CommentsResponse.json"), Comment[].class);
    }

    @Test
    public void testEnrichedUser() {
        when(integrationService.callApi(any(),any())).thenReturn(posts);
        CompletableFuture<List<Comment>> list = CompletableFuture.completedFuture(Arrays.asList(comments));
        when(commentAsyncService.getComments(any(),any())).thenReturn(list);
        doNothing().when(migrateUserService).migrateUser(any());
        MigrateUserEnriched migrateUserEnriched = userEnrichService.enrichedUser("4049");
        assertEquals(migrateUserEnriched.getPosts().size(),3);
    }

    @Test
    public void testEnrichedUserWithEmptyPosts() {
        posts = new Post[0];
        when(integrationService.callApi(any(),any())).thenReturn(posts);
        CompletableFuture<List<Comment>> list = CompletableFuture.completedFuture(Arrays.asList(comments));
        when(commentAsyncService.getComments(any(),any())).thenReturn(list);
        doNothing().when(migrateUserService).migrateUser(any());
        MigrateUserEnriched migrateUserEnriched = userEnrichService.enrichedUser("4049");
        assertNull(migrateUserEnriched.getPosts());
    }

    @Test
    public void testEnrichedUserNullComments() {
        when(integrationService.callApi(any(),any())).thenReturn(posts);
        comments = new Comment[0];
        CompletableFuture<List<Comment>> list = CompletableFuture.completedFuture(Arrays.asList(comments));
        when(commentAsyncService.getComments(any(),any())).thenReturn(list);
        doNothing().when(migrateUserService).migrateUser(any());
        MigrateUserEnriched migrateUserEnriched = userEnrichService.enrichedUser("4049");
        assertEquals(migrateUserEnriched.getPosts().size(),3);
        assertEquals(migrateUserEnriched.getPosts().get(0).getComments(),null);
    }
}