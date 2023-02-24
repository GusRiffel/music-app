package com.gusriffel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackDto {
    @JsonProperty("title")
    private String title;
    @JsonProperty("preview")
    private String preview;
}
