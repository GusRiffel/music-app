package com.gusriffel.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Artist {
    private String name;
    private String pictureSmall;
    private String pictureMedium;
    private String pictureBig;
    private String pictureXL;
    private List<Album> albums;
}
