package com.gusriffel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gusriffel.domain.Artist;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class APIResponseDto {
    @JsonProperty("data")
    List<ArtistInfoDto> data;

//    @JsonProperty("data")
//    List<Object> data;
    @JsonProperty("total")
    int total;
    @JsonProperty("next")
    String next;
}
