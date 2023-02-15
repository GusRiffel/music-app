package com.gusriffel.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Artist {

    @JsonProperty("CachorroPicles")
    private Object artistDetails;

    @JsonProperty
    public void data(List<Map<String, Object>> res) {
//        Map<String, Object> data = res;
        this.artistDetails = res;
    }
}
