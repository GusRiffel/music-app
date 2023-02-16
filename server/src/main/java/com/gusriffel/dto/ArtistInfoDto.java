package com.gusriffel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ArtistInfoDto {

    @JsonProperty("title")
    private String trackTitle;

    @JsonProperty("artistName")
    private String artistName;

    @JsonProperty("albumTitle")
    private String albumTitle;

    @JsonProperty("artist")
    public void setArtist(Map<String, String> artistData) {
        this.artistName = artistData.get("name");
    }

    @JsonProperty("album")
    public void setAlbum(Map<String, String> artistData) {
        this.albumTitle = artistData.get("title");
    }

}
