package com.db.dataplatform.techtest.server.component;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

public interface HadoopClient {
    @Async
    @Retryable(value = {RestClientException.class, HttpClientErrorException.class}, maxAttempts = 10, backoff = @Backoff(3000))
    Boolean pushData(String data);
}
