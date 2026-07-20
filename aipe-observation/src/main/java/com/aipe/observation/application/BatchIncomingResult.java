package com.aipe.observation.application;

import java.io.Serializable;

/**
 * 批量入库结果
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class BatchIncomingResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int totalCount;
    private final int successCount;
    private final int failedCount;

    public BatchIncomingResult(int totalCount, int successCount, int failedCount) {
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failedCount = failedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public boolean isAllSuccess() {
        return failedCount == 0;
    }
}
