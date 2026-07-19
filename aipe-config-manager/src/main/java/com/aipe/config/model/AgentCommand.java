package com.aipe.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor
public class AgentCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String START_CONNECTOR = "START_CONNECTOR";
    public static final String STOP_CONNECTOR = "STOP_CONNECTOR";
    public static final String RELOAD_CONFIG = "RELOAD_CONFIG";

    private String type;
    private Map<String, String> params;
}
