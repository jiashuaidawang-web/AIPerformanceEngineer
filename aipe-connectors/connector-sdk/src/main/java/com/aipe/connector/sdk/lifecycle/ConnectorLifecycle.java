package com.aipe.connector.sdk.lifecycle;

import com.aipe.connector.sdk.exception.ConnectorException;

public interface ConnectorLifecycle {
    void onInit() throws ConnectorException;
    void onStart() throws ConnectorException;
    void onStop();
    void onDestroy();
    ConnectorState getState();
}
