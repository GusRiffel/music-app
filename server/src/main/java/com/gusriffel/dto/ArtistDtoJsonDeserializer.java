package com.gusriffel.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArtistDtoJsonDeserializer extends JsonDeserializer<ArtistDto> {
    @Override
    public ArtistDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        Root root = jsonParser.readValueAs(Root.class);
        return ArtistDto.builder()
                .name(root.artist.name)
                .pictureSmall(root.artist.picture_small)
                .pictureMedium(root.artist.picture_medium)
                .pictureBig(root.artist.picture_big)
                .pictureXL(root.artist.picture_xl)
                .albums(List.of(
                        AlbumDto.builder()
                                .id(root.album.id)
                                .title(root.album.title)
                                .coverSmall(root.album.cover_small)
                                .coverMedium(root.album.cover_medium)
                                .coverBig(root.album.cover_big)
                                .coverXL(root.album.cover_xl)
                                .trackListUrl(root.album.tracklist)
                                .tracks(new ArrayList<>()
                                )
                                .build()
                )).build();
    }

    @Getter
    @Setter
    private static class Root {
        private AlbumJson album;
        private ArtistJson artist;
    }

    @Getter
    @Setter
    private static class AlbumJson {
        private int id;
        private String title;
        private String cover_small;
        private String cover_medium;
        private String cover_big;
        private String cover_xl;
        private String tracklist;
    }

    @Getter
    @Setter
    private static class ArtistJson {
        private String name;
        private String picture_small;
        private String picture_medium;
        private String picture_big;
        private String picture_xl;
    }

}
