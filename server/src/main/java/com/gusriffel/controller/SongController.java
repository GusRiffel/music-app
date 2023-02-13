package com.gusriffel.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class SongController {

    @Value("${apiKey}")
    private String apiKey;
    @Value("${apiHost}")
    private String apiHost;
    private final WebClient webClient;

    public SongController() {
        this.webClient = WebClient.builder()
                .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/search")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @GetMapping(path = "/test/{artist}")
    public Object test(@PathVariable String artist) {
        return webClient.get()
                .uri("?q=" + artist)
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", apiHost)
                .retrieve()
                .bodyToMono(Object.class);
    }
}
