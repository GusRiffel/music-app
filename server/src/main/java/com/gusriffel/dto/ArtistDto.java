package com.gusriffel.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonDeserialize(using = ArtistDtoJsonDeserializer.class)
public class ArtistDto {
    private String name;
    private String pictureSmall;
    private String pictureMedium;
    private String pictureBig;
    private String pictureXL;
    private List<AlbumDto> albums;
}
