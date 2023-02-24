package com.gusriffel.service;

import com.gusriffel.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
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

    public ArtistDto getArtist(String artist) {
        List<ArtistDto> artistDto = artistRequest(artist);
        return formatRequest(artistDto);
    }

    private List<ArtistDto> artistRequest(String artist) {
        String composedUrl = baseUrl + artist;
        Mono<APISearchResponseDto> apiResponseDtoMono = request(composedUrl);
        Mono<String> nextMono = apiResponseDtoMono.map(APISearchResponseDto::getNext);
        String nextPage = nextMono.block();
        assert nextPage != null;
        String startURL = nextPage.substring(0, nextPage.length() - 2) + 0;

        return getAllRequestPages(startURL)
                .map(APISearchResponseDto::getData)
                .toStream()
                .flatMap(Collection::stream)
                .filter(artistDto -> artistDto.getName().equalsIgnoreCase(artist))
                .toList();
    }

    private Mono<APISearchResponseDto> request(String url) {
        return webClient.get()
                .uri(url)
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", apiHost)
                .retrieve()
                .bodyToMono(APISearchResponseDto.class);
    }

    private Mono<APITrackListResponseDto> getTrackList(String url) {
        return webClient.get()
                .uri(url)
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", apiHost)
                .retrieve()
                .bodyToMono(APITrackListResponseDto.class);
    }

    private Flux<APISearchResponseDto> getAllRequestPages(String url) {
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

    private ArtistDto formatRequest(List<ArtistDto> artistDto) {
        List<AlbumDto> albumsDto = artistDto.stream().map(ArtistDto::getAlbums).flatMap(Collection::stream).toList();
        List<AlbumDto> filteredAlbums = new ArrayList<>(albumsDto.stream()
                .distinct()
                .collect(Collectors.toMap(AlbumDto::getTitle, Function.identity(), (album1, album2) -> album1))
                .values());

        return ArtistDto.builder()
                .name(artistDto.get(0).getName())
                .pictureSmall(artistDto.get(0).getPictureSmall())
                .pictureMedium(artistDto.get(0).getPictureMedium())
                .pictureBig(artistDto.get(0).getPictureBig())
                .pictureXL(artistDto.get(0).getPictureXL())
                .albums(populateAlbumTrackList(filteredAlbums).toFuture().join())
                .build();
    }

    private Mono<List<AlbumDto>> populateAlbumTrackList(List<AlbumDto> albumList) {
        return Flux.fromIterable(albumList)
                .flatMapSequential(album -> getTrackList(album.getTrackListUrl())
                        .map(trackList -> {
                            album.setTracks(trackList.getData());
                            return album;
                        }))
                .collectList();
    }
}
