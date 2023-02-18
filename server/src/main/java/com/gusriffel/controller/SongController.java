package com.gusriffel.controller;

import com.gusriffel.dto.APIResponseDto;
import com.gusriffel.dto.ArtistInfoDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@RestController
public class SongController {

    @Value("${apiKey}")
    private String apiKey;
    @Value("${apiHost}")
    private String apiHost;
    private final WebClient webClient;
    private String baseUrl = "https://deezerdevs-deezer.p.rapidapi.com/search?q=";

    public SongController() {
        this.webClient = WebClient.builder()
//                .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/search")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @GetMapping(path = "/test/{artist}")
    public Object test(@PathVariable String artist) {
        String composedUrl = baseUrl + artist;
        Mono<APIResponseDto> apiResponseDtoMono = request(composedUrl);
        Mono<String> nextMono = apiResponseDtoMono.map(APIResponseDto::getNext);
        String nextPage = nextMono.block();
        assert nextPage != null;
        String startURL = nextPage.substring(0, nextPage.length() - 2)+ 0;
        List<ArtistInfoDto> artistInfoDto =
                getAllPages(startURL).map(APIResponseDto::getData).toStream().flatMap(Collection::stream).toList();

        return artistInfoDto.stream()
                .filter(track -> track.getArtistName().equalsIgnoreCase(artist))
                .collect(Collectors.groupingBy(
                        ArtistInfoDto::getArtistName,
                        Collectors.groupingBy(
                                ArtistInfoDto::getAlbumTitle,
                                Collectors.mapping(ArtistInfoDto::getTrackTitle, Collectors.toList())
                        )
                ))
                .entrySet().stream()
                .map(entry -> Map.of(
                        "artistName", entry.getKey(),
                        "albums", entry.getValue().entrySet().stream()
                                .map(albumEntry -> Map.of(
                                        "albumTitle", albumEntry.getKey(),
                                        "tracks", albumEntry.getValue()
                                ))
                                .toList()
                ))
                .toList();
    }

    private Mono<APIResponseDto> request(String url) {
        return webClient.get()
                .uri(url)
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", apiHost)
                .retrieve()
                .bodyToMono(APIResponseDto.class);
    }

    private Flux<APIResponseDto> getAllPages(String url) {
        return request(url)
                .expand(data -> {
                    String getNextPage = data.getNext();
                    if (getNextPage == null) {
                        return Mono.empty();
                    } else {
                        return request(data.getNext());
                    }
                });
    }

}

