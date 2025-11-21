package com.example.social_media.shared_libs.services;

import com.example.social_media.shared_libs.utils.RestClientUtility;

import java.util.Map;


public abstract class BaseService {

    protected final RestClientUtility restClient;

    protected BaseService(RestClientUtility restClient) {
        this.restClient = restClient;
    }

    protected <T> T get(
            String url,
            Class<T> responseType,
            Object... uriVars
    ) {
        return restClient.get(url, null, null, responseType, uriVars).getBody();
    }

    protected <T> T get(
            String url,
            Object queryParams,
            Class<T> responseType,
            Object... uriVars
    ) {
        return restClient.get(url, null, (Map<String, Object>) queryParams, responseType, uriVars).getBody();
    }

    protected <T> T post(
            String url,
            Object body,
            Class<T> responseType,
            Object... uriVars
    ) {
        return restClient.post(url, null, body, responseType, uriVars).getBody();
    }

    protected <T> T put(
            String url,
            Object body,
            Class<T> responseType,
            Object... uriVars
    ) {
        return restClient.put(url, null, body, responseType, uriVars).getBody();
    }

    protected <T> T delete(
            String url,
            Class<T> responseType,
            Object... uriVars
    ) {
        return restClient.delete(url, null, responseType, uriVars).getBody();
    }
}
