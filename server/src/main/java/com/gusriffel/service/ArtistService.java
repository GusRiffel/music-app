package com.gusriffel.service;

import com.gusriffel.dto.APIResponseDto;
import com.gusriffel.dto.ArtistDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArtistService {
    @Value("${apiKey}")
    private String apiKey;
    @Value("${apiHost}")
    private String apiHost;
    private final WebClient webClient;
    private String baseUrl = "https://deezerdevs-deezer.p.rapidapi.com/search?q=";

    public ArtistService() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Object getArtist(String artist) {
        List<ArtistDto> artistDtos = artistRequest(artist);
        Object o = formatRequest(artistDtos);
        return o;

    }

    private List<ArtistDto> artistRequest(String artist) {
        String composedUrl = baseUrl + artist;
        Mono<APIResponseDto> apiResponseDtoMono = request(composedUrl);
        Mono<String> nextMono = apiResponseDtoMono.map(APIResponseDto::getNext);
        String nextPage = nextMono.block();
        assert nextPage != null;
        String startURL = nextPage.substring(0, nextPage.length() - 2) + 0;

        return getAllRequestPages(startURL)
                .map(APIResponseDto::getData)
                .toStream()
                .flatMap(Collection::stream)
                .filter(artistDto -> artistDto.getArtist().get("name").equalsIgnoreCase(artist))
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

    private Flux<APIResponseDto> getAllRequestPages(String url) {
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

    private Object formatRequest(List<ArtistDto> artistDto) {
        return artistDto.stream()
                .collect(Collectors.groupingBy(
                        ArtistDto::getArtist,
                        Collectors.groupingBy(
                                ArtistDto::getAlbum,
                                Collectors.mapping(ArtistDto::getTrack, Collectors.toList())
                        )
                ))
                .entrySet().stream()
                .map(entry -> Map.of(
                        "Artist", entry.getKey(),
                        "Albums", entry.getValue().entrySet().stream()
                                .map(albumEntry -> Map.of(
                                        "AlbumInfo", albumEntry.getKey(),
                                        "Tracks", albumEntry.getValue()
                                ))
                                .toList()
                ))
                .toList();
    }
}
