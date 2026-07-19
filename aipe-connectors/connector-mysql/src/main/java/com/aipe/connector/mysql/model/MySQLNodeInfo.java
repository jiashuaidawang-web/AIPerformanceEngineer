package com.aipe.connector.mysql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class MySQLNodeInfo {
    private String host;
    private Integer port;
    private String version;
    private String serverId;
    private String hostname;
    private Boolean connected;
}
