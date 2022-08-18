package kafkaconsumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@Service
@Slf4j
public class IntegrationService {


    private final RestTemplate restTemplate;

    @Autowired
    public IntegrationService( RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T callApi(Class<T> responseType, URI uri) {

        long startTime = System.currentTimeMillis();
        log.info("calling to {}", uri);
        T response =  getResponse(uri, responseType);
        log.info("Time taken for {} response  {} ms", uri.toString(), System.currentTimeMillis() - startTime);
        log.info("response for : {} : {}",uri, response);
        return response;
    }


    private <T> T getResponse(URI uri, final Class<T> responseType) {
            RequestEntity<Void> requestEntity = RequestEntity
                    .get(uri)
                    .build();

            ResponseEntity<T> response = restTemplate.exchange(requestEntity, responseType);
            return response.getBody();
    }
}
