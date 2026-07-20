package com.aipe.observation.application;

import java.io.Serializable;

/**
 * 单条入库结果
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ObservationIncomingResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * observation_id（业务主键）
     */
    private final String observationId;

    /**
     * 是否成功
     */
    private final boolean success;

    /**
     * 失败原因（成功时为 null）
     */
    private final String errorMessage;

    private ObservationIncomingResult(String observationId, boolean success, String errorMessage) {
        this.observationId = observationId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static ObservationIncomingResult success(String observationId) {
        return new ObservationIncomingResult(observationId, true, null);
    }

    public static ObservationIncomingResult fail(String errorMessage) {
        return new ObservationIncomingResult(null, false, errorMessage);
    }

    public String getObservationId() {
        return observationId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
