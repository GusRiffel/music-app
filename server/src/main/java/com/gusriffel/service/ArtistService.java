package com.gusriffel.service;

import com.gusriffel.dto.APIResponseDto;
import com.gusriffel.dto.AlbumDto;
import com.gusriffel.dto.ArtistDto;
import com.gusriffel.dto.TrackDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<AlbumDto> albumDtos = artistDtos.stream().map(ArtistDto::getAlbums).flatMap(Collection::stream).toList();
        List<AlbumDto> filteredAlbums = new ArrayList<>(albumDtos.stream()
                .distinct()
                .collect(Collectors.toMap(AlbumDto::getTitle, Function.identity(), (album1, album2) -> album1))
                .values());
        return filteredAlbums.size();
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
                .filter(artistDto -> artistDto.getName().equalsIgnoreCase(artist))
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

//    private ArtistDto formatRequest(List<ArtistDto> artistDto) {
//        List<AlbumDto> albumDtos = artistDto.stream().map(ArtistDto::getAlbums).flatMap(Collection::stream).toList();
////        ArtistDto.builder()
////                .name(artistDto.get(0).getName())
////                .pictureSmall(artistDto.get(0).getPictureSmall())
////                .pictureMedium(artistDto.get(0).getPictureMedium())
////                .pictureBig(artistDto.get(0).getPictureBig())
////                .pictureXL(artistDto.get(0).getPictureXL())
////                .albums(
////                ))
////        return artistDto.stream()
////                .collect(Collectors.groupingBy(
////                        ArtistDto::getName,
////                        Collectors.groupingBy(
////                                ArtistDto::getAlbums,
////                                Collectors.mapping(ArtistDto::getName, Collectors.toList())
////                        )
////                ))
////                .entrySet().stream()
////                .map(entry -> Map.of(
////                        "Artist", entry.getKey(),
////                        "Albums", entry.getValue().entrySet().stream()
////                                .map(albumEntry -> Map.of(
////                                        "AlbumInfo", albumEntry.getKey(),
////                                        "Tracks", albumEntry.getValue()
////                                ))
////                                .toList()
////                ))
////                .toList();
//    }
}
