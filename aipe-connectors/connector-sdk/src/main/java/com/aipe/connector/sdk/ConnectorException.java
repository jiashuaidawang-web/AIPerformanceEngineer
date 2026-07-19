package com.aipe.connector.sdk;

/**
 * Connector 异常
 *
 * <p>Connector 内部错误的统一封装。
 * Agent 捕获此异常后不应终止，仅做日志记录。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ConnectorException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String connectorId;

    public ConnectorException(String message) {
        super(message);
        this.connectorId = null;
    }

    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
        this.connectorId = null;
    }

    public ConnectorException(String connectorId, String message, Throwable cause) {
        super(message, cause);
        this.connectorId = connectorId;
    }

    public String getConnectorId() {
        return connectorId;
    }

    @Override
    public String toString() {
        if (connectorId != null) {
            return "ConnectorException{connectorId='" + connectorId + "', message='" + getMessage() + "'}";
        }
        return "ConnectorException{message='" + getMessage() + "'}";
    }
}
