package com.gusriffel.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Track {
    private String title;
    private String preview;
}
