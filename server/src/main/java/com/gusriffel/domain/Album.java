package com.gusriffel.domain;

import lombok.Data;

import java.util.List;

@Data
public class Album {
    private String title;
    private String coverSmall;
    private String coverMedium;
    private String coverBig;
    private String coverXL;
    private String trackListUrl;
    private List<Track> tracks;
}
