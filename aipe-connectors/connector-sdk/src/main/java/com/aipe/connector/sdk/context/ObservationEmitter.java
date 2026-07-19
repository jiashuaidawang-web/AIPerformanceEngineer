package com.aipe.connector.sdk.context;

import com.aipe.common.domain.ObservationData;
import java.util.List;

/**
 * 观测数据发送器接口
 *
 * <p>Connector 通过此接口将采集数据发送至 Agent。
 */
public interface ObservationEmitter {
    void send(List<ObservationData> observations);
    void send(ObservationData observation);
}
