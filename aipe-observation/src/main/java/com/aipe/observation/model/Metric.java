package com.aipe.observation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Metric implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Double value;
    private String unit;
    private Map<String, String> tags;
}
