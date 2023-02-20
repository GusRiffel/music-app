package com.gusriffel.service;

import com.gusriffel.dto.APIResponseDto;
import com.gusriffel.dto.ArtistDto;
import com.gusriffel.dto.ArtistInfoDto;
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

    public List<ArtistDto> getArtist(String artist) {
        return initialRequest(artist);

    }

//    public Object artist(String artist) {
//        String composedUrl = baseUrl + artist;
//        Mono<APIResponseDto> apiResponseDtoMono = request(composedUrl);
//        Mono<String> nextMono = apiResponseDtoMono.map(APIResponseDto::getNext);
//        String nextPage = nextMono.block();
//        assert nextPage != null;
//        String startURL = nextPage.substring(0, nextPage.length() - 2)+ 0;
//        List<ArtistInfoDto> artistInfoDto =
//                getAllPages(startURL).map(APIResponseDto::getData).toStream().flatMap(Collection::stream).toList();
//
//        return artistInfoDto.stream()
//                .filter(track -> track.getArtistName().equalsIgnoreCase(artist))
//                .collect(Collectors.groupingBy(
//                        ArtistInfoDto::getArtistName,
//                        Collectors.groupingBy(
//                                ArtistInfoDto::getAlbumTitle,
//                                Collectors.mapping(ArtistInfoDto::getTrackTitle, Collectors.toList())
//                        )
//                ))
//                .entrySet().stream()
//                .map(entry -> Map.of(
//                        "artistName", entry.getKey(),
//                        "albums", entry.getValue().entrySet().stream()
//                                .map(albumEntry -> Map.of(
//                                        "albumTitle", albumEntry.getKey(),
//                                        "tracks", albumEntry.getValue()
//                                ))
//                                .toList()
//                ))
//                .toList();
//    }
    private List<ArtistDto> initialRequest(String artist) {
        String composedUrl = baseUrl + artist;
        Mono<APIResponseDto> apiResponseDtoMono = request(composedUrl);
        Mono<String> nextMono = apiResponseDtoMono.map(APIResponseDto::getNext);
        String nextPage = nextMono.block();
        assert nextPage != null;
        String startURL = nextPage.substring(0, nextPage.length() - 2)+ 0;

        return getAllPages(startURL)
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
