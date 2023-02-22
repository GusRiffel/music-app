package com.gusriffel.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AlbumDto {
    private String title;
    private String coverSmall;
    private String coverMedium;
    private String coverBig;
    private String coverXL;
    private String trackListUrl;
    private List<TrackDto> tracks;
}
