package com.gusriffel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class APISearchResponseDto {
    @JsonProperty("data")
    List<ArtistDto> data;

    @JsonProperty("total")
    int total;

    @JsonProperty("next")
    String next;
}
