package com.aipe.backend.controller;

import com.aipe.backend.dto.ObservationBatchRequest;
import com.aipe.backend.dto.ObservationQueryResponse;
import com.aipe.backend.service.ObservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Observation 接口
 *
 * <p>Agent 上报数据、外部查询数据的入口。
 */
@RestController
@RequestMapping("/api/v1/observations")
public class ObservationController {
  private static final Logger log = LoggerFactory.getLogger(ObservationController.class);

  @Autowired
  private ObservationService observationService;

  /**
   * Agent 批量上报 Observation
   * POST /api/v1/observations/batch
   */
  @PostMapping("/batch")
  public Map<String, Object> batchSave(@RequestBody ObservationBatchRequest request) {
    if (request != null && request.getObservations() != null) {
      observationService.saveObservations(request.getObservations());
    }
    Map<String, Object> map = new HashMap<>();
    map.put("status", "ok");
    return map;
  }

  /**
   * 查询 Observation
   * GET /api/v1/observations?resource_id=xxx&metric_name=xxx&start_time=xxx&end_time=xxx&limit=100
   */
  @GetMapping
  public ObservationQueryResponse query(
    @RequestParam("resource_id") String resourceId,
    @RequestParam("metric_name") String metricName,
    @RequestParam("start_time") Long startTime,
    @RequestParam("end_time") Long endTime,
    @RequestParam(defaultValue = "100") Integer limit) {

    List<Map<String, Object>> data = observationService.query(resourceId, metricName, startTime, endTime, limit);
    return new ObservationQueryResponse(resourceId, metricName, data);
  }

  /**
   * 查询最新 Observation
   * GET /api/v1/observations/latest?resource_id=xxx&limit=100
   */
  @GetMapping("/latest")
  public ObservationQueryResponse latest(
    @RequestParam("resource_id") String resourceId,
    @RequestParam(defaultValue = "100") Integer limit) {

    List<Map<String, Object>> data = observationService.queryLatest(resourceId, limit);
    return new ObservationQueryResponse(resourceId, null, data);
  }
}
