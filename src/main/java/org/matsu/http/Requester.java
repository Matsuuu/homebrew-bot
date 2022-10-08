package org.matsu.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Requester {

    static Logger logger = LoggerFactory.getLogger(Requester.class);

    private HttpClient httpClient;

    public Requester() {
        httpClient = HttpClient.newHttpClient();
    }

    public String get(String url) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
            .build();

        String responseString = "";
        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            responseString = response.body();
        } catch (IOException | InterruptedException e) {
            logger.error("Could not request url " + url);
            logger.error(e.getMessage());
        }

        return responseString;
    }
}
