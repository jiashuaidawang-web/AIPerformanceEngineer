package com.aipe.observation.api;

import com.aipe.observation.api.dto.ApiResponse;
import com.aipe.observation.api.dto.ObservationRequest;
import com.aipe.observation.api.dto.ObservationResponse;
import com.aipe.observation.api.dto.TrendResponse;
import com.aipe.observation.application.BatchIncomingResult;
import com.aipe.observation.application.ObservationApplicationService;
import com.aipe.observation.application.ObservationIncomingResult;
import com.aipe.observation.domain.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Observation REST Controller
 *
 * <p>对齐 IM-006 REST API Mapping / WP012 Blueprint §7
 * <p>Architecture Law-007：Controller Is A Protocol Translator（只做协议转换，无业务逻辑）
 *
 * <p>API 设计（含 M1 旧协议兼容 - Blueprint §7.2）：
 * <ul>
 *   <li>POST /api/v1/observations              新协议：单条入库</li>
 *   <li>POST /api/v1/observations/batch         兼容 M1 旧协议：批量入库 List&lt;Map&gt;</li>
 *   <li>POST /api/v1/observations/batch/new     新协议：批量入库 List&lt;ObservationRequest&gt;</li>
 *   <li>GET  /api/v1/observations               查询 Observation</li>
 *   <li>GET  /api/v1/observations/trend         趋势查询（1m / 5m / 1h / 1d）</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/observations")
public class ObservationController {

    private static final Logger log = LoggerFactory.getLogger(ObservationController.class);

    @Autowired
    private ObservationApplicationService applicationService;

    @Autowired
    private ObservationDtoMapper dtoMapper;

    /**
     * 新协议：单条入库
     * POST /api/v1/observations
     */
    @PostMapping
    public ApiResponse<ObservationResponse> create(@Valid @RequestBody ObservationRequest request) {
        Observation observation = dtoMapper.toDomain(request);
        ObservationIncomingResult result = applicationService.processIncoming(observation);
        if (!result.isSuccess()) {
            return ApiResponse.error(400, result.getErrorMessage());
        }
        ObservationResponse response = dtoMapper.toResponse(observation);
        return ApiResponse.success(response);
    }

    /**
     * 兼容 M1 旧协议：Agent 批量上报 Observation
     * POST /api/v1/observations/batch
     *
     * <p>旧协议格式：{agentId, connectorType, observations: [{"resource_id":"x","metric_name":"x","metric_value":1.0,"timestamp":123}]}
     * <p>新 ObservationApplicationService 先兼容旧 ObservationBatchRequest 协议，然后逐步切换 Agent 到新协议。
     * 旧 Agent 代码无需修改即可对接新后端。
     */
    @PostMapping("/batch")
    public ApiResponse<Map<String, Object>> batchCreate(@RequestBody Map<String, Object> request) {
        if (request == null) {
            return ApiResponse.error(400, "Request body is required");
        }
        Object observationsObj = request.get("observations");
        if (!(observationsObj instanceof List)) {
            return ApiResponse.error(400, "`observations` must be a list");
        }
        List<Map<String, Object>> legacyList = (List<Map<String, Object>>) observationsObj;

        int success = 0;
        int failed = 0;
        for (Map<String, Object> legacy : legacyList) {
            try {
                Observation observation = dtoMapper.fromLegacyMap(legacy);
                ObservationIncomingResult result = applicationService.processIncoming(observation);
                if (result.isSuccess()) {
                    success++;
                } else {
                    failed++;
                }
            } catch (Exception e) {
                failed++;
                log.warn("Failed to process legacy observation: {}", e.getMessage());
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("total", legacyList.size());
        data.put("success", success);
        data.put("failed", failed);
        return ApiResponse.success(data);
    }

    /**     * 新协议：批量入库     * POST /api/v1/observations/batch/new
     */    @PostMapping("/batch/new")
    public ApiResponse<BatchIncomingResult> batchCreateNew(@Valid @RequestBody List<ObservationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return ApiResponse.error(400, "Observations list is required");
        }
        List<Observation> observations = requests.stream()
                .map(dtoMapper::toDomain)
                .collect(java.util.stream.Collectors.toList());
        BatchIncomingResult result = applicationService.batchProcessIncoming(observations);
        return ApiResponse.success(result);
    }

    /**     * 查询 Observation     * GET /api/v1/observations?resource_id=xxx&metric_name=xxx&start_time=xxx&end_time=xxx&limit=xxx
     */
    @GetMapping
    public ApiResponse<List<ObservationResponse>> query(
            @RequestParam("resource_id") String resourceId,
            @RequestParam(value = "metric_name", required = false) String metricName,            @RequestParam(value = "start_time", required = false) Long startTime,
            @RequestParam(value = "end_time", required = false) Long endTime,
            @RequestParam(value = "limit", defaultValue = "100") Integer limit) {

        List<Observation> observations;
        if (metricName != null && startTime != null && endTime != null) {
            observations = applicationService.queryByMetric(resourceId, metricName, startTime, endTime, limit);
        } else {            observations = applicationService.queryByResource(resourceId, limit);
        }
        return ApiResponse.success(dtoMapper.toResponseList(observations));
    }

    /**
     * 查询最新 Observation     * GET /api/v1/observations/latest?resource_id=xxx&limit=100
     */    @GetMapping("/latest")
    public ApiResponse<List<ObservationResponse>> latest(
            @RequestParam("resource_id") String resourceId,            @RequestParam(value = "limit", defaultValue = "100") Integer limit) {
        List<Observation> observations = applicationService.queryByResource(resourceId, limit);
        return ApiResponse.success(dtoMapper.toResponseList(observations));
    }

    /**
     * 时间桶趋势查询
     * GET /api/v1/observations/trend?resource_id=xxx&metric_name=xxx&interval=1m&start_time=xxx&end_time=xxx
     */
    @GetMapping("/trend")
    public ApiResponse<TrendResponse> trend(
            @RequestParam("resource_id") String resourceId,
            @RequestParam("metric_name") String metricName,
            @RequestParam(value = "interval", defaultValue = "1m") String interval,
            @RequestParam(value = "start_time", required = false) Long startTime,
            @RequestParam(value = "end_time", required = false) Long endTime) {

        long now = System.currentTimeMillis();
        long effectiveStart = startTime != null ? startTime : now - 3600_000L; // 默认 1 小时
        long effectiveEnd = endTime != null ? endTime : now;

        List<com.aipe.observation.application.TrendAggregator.TrendPoint> trendPoints =
                applicationService.queryTrend(resourceId, metricName, effectiveStart, effectiveEnd, interval);

        TrendResponse response = dtoMapper.toTrendResponse(trendPoints, resourceId, metricName, interval);
        return ApiResponse.success(response);
    }
}
