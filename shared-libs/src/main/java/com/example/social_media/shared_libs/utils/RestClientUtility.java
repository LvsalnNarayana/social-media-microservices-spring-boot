package com.example.social_media.shared_libs.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
public class RestClientUtility {

    private final RestTemplate restTemplate = new RestTemplate();

    public <T> ResponseEntity<T> exchange(
            String url,
            HttpMethod method,
            Map<String, String> headers,
            Map<String, Object> queryParams,
            Object body,
            Class<T> responseType,
            Object... uriVariables
    ) {
        try {
            // Build URI with params
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
            if (queryParams != null) {
                queryParams.forEach(builder::queryParam);
            }
            String finalUrl = builder.toUriString();

            // Set headers
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            if (headers != null) {
                headers.forEach(httpHeaders::add);
            }

            HttpEntity<?> entity = body != null
                    ? new HttpEntity<>(body, httpHeaders)
                    : new HttpEntity<>(httpHeaders);

            log.info("Calling external service: {} {}", method, finalUrl);
            log.debug("Headers: {}", headers);
            log.debug("Body: {}", body);

            return restTemplate.exchange(
                    finalUrl,
                    method,
                    entity,
                    responseType,
                    uriVariables
            );

        } catch (RestClientException ex) {
            log.error("REST call failed: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    // Convenience GET
    public <T> ResponseEntity<T> get(
            String url,
            Map<String, String> headers,
            Map<String, Object> queryParams,
            Class<T> responseType,
            Object... uriVariables
    ) {
        return exchange(url, HttpMethod.GET, headers, queryParams, null, responseType, uriVariables);
    }

    // Convenience POST
    public <T> ResponseEntity<T> post(
            String url,
            Map<String, String> headers,
            Object body,
            Class<T> responseType,
            Object... uriVariables
    ) {
        return exchange(url, HttpMethod.POST, headers, null, body, responseType, uriVariables);
    }

    // Convenience PUT
    public <T> ResponseEntity<T> put(
            String url,
            Map<String, String> headers,
            Object body,
            Class<T> responseType,
            Object... uriVariables
    ) {
        return exchange(url, HttpMethod.PUT, headers, null, body, responseType, uriVariables);
    }

    // Convenience DELETE
    public <T> ResponseEntity<T> delete(
            String url,
            Map<String, String> headers,
            Class<T> responseType,
            Object... uriVariables
    ) {
        return exchange(url, HttpMethod.DELETE, headers, null, null, responseType, uriVariables);
    }
}
