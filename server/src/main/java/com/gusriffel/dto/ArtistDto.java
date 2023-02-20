package com.gusriffel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ArtistDto {
    private Map<String, String> artist = new HashMap<>();
    private Map<String, String> album = new HashMap<>();
    private Map<String, String> track = new HashMap<>();

    @JsonProperty("artist")
    public void setArtist(Map<String, String> artistData) {
        artist.put("name", artistData.get("name"));
        artist.put("pictureSmall", artistData.get("picture_small"));
        artist.put("pictureMedium", artistData.get("picture_medium"));
        artist.put("pictureBig", artistData.get("picture_big"));
        artist.put("pictureXL", artistData.get("picture_xl"));
    }

    @JsonProperty("album")
    public void setAlbum(Map<String, String> albumData) {
        album.put("title", albumData.get("title"));
        album.put("coverSmall", albumData.get("cover_small"));
        album.put("coverMedium", albumData.get("cover_medium"));
        album.put("coverBig", albumData.get("cover_big"));
        album.put("coverXL", albumData.get("cover_xl"));
        album.put("trackListUrl", albumData.get("tracklist"));
    }

    @JsonProperty("title")
    public void setTrackTitle(String title) {
        track.put("title", title);
    }

    @JsonProperty("preview")
    public void setTrackPreview(String preview) {
        track.put("preview", preview);
    }
}
