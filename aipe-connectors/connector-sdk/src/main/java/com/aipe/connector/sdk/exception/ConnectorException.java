package com.aipe.connector.sdk.exception;

public class ConnectorException extends Exception {
    private static final long serialVersionUID = 1L;
    private final String connectorId;

    public ConnectorException(String message) { super(message); this.connectorId = null; }
    public ConnectorException(String message, Throwable cause) { super(message, cause); this.connectorId = null; }
    public ConnectorException(String connectorId, String message, Throwable cause) { super(message, cause); this.connectorId = connectorId; }

    public String getConnectorId() { return connectorId; }

    @Override
    public String toString() {
        return connectorId != null
            ? "ConnectorException{connectorId='" + connectorId + "', message='" + getMessage() + "'}"
            : "ConnectorException{message='" + getMessage() + "'}";
    }
}
