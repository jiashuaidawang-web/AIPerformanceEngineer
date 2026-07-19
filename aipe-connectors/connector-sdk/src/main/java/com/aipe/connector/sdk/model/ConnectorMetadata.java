package com.aipe.connector.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConnectorMetadata implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String version;
    private String author;
    private String description;
    private String type;
    private List<String> supportedResources;
}
