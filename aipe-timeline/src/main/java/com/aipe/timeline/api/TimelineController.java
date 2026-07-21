package com.aipe.timeline.api;

import com.aipe.timeline.api.dto.TimelineRequest;
import com.aipe.timeline.api.dto.TimelineResponse;
import com.aipe.timeline.api.dto.TimelineBatchRequest;
import com.aipe.timeline.api.exception.TimelineExceptionHandler;
import com.aipe.timeline.application.TimelineService;
import com.aipe.timeline.domain.Timeline;
import com.aipe.timeline.domain.TimelinePoint;
import com.aipe.timeline.domain.TimelineQuery;
import com.aipe.timeline.domain.TimelineStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Timeline REST Controller
 *
 * <p>IM-006 / WP014 Blueprint §7
 * <p>Architecture Law-007 + Law-004：Timeline 永不存储，每次查询实时构建
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/timelines")
@Validated
public class TimelineController {

    private static final Logger log = LoggerFactory.getLogger(TimelineController.class);

    @Autowired
    private TimelineService timelineService;

    /**
     * GET /api/v1/timelines?resource_id=xxx&metric_name=xxx&start_time=xxx&end_time=xxx
     * Single Timeline
     */
    @GetMapping
    public ApiResponse<TimelineResponse> getTimeline(@Valid TimelineRequest request) {
        TimelineQuery query = new TimelineQuery(
                request.getResourceId(), request.getMetricName(),
                request.getStartTime(), request.getEndTime(),
                request.getLimit() != null ? request.getLimit() : 10000);
        Timeline timeline = timelineService.buildTimeline(query);
        return ApiResponse.success(toResponse(timeline));
    }

    /**
     * GET /api/v1/timelines/batch?resource_id=xxx&metric_names=cpu,memory&start_time=xxx&end_time=xxx
     * Multi Timeline
     */
    @GetMapping("/batch")
    public ApiResponse<List<TimelineResponse>> getTimelines(@Valid TimelineBatchRequest request) {
        List<String> metricNames = request.getMetricNames() != null ? request.getMetricNames()
                : java.util.Collections.emptyList();

        TimelineQuery baseQuery = new TimelineQuery(
                request.getResourceId(), null,
                request.getStartTime(), request.getEndTime(),
                request.getLimit() != null ? request.getLimit() : 10000);
        List<Timeline> timelines = timelineService.buildTimelines(baseQuery, metricNames);
        List<TimelineResponse> responses = timelines.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    /**
     * GET /api/v1/timelines/all?resource_id=xxx&start_time=xxx&end_time=xxx
     * 全指标 Timeline（按 Resource 所有 distinct metricName 聚合）
     */
    @GetMapping("/all")
    public ApiResponse<List<TimelineResponse>> getAllMetricsTimelines(
            @RequestParam("resource_id") String resourceId,
            @RequestParam("start_time") Long startTime,
            @RequestParam("end_time") Long endTime,
            @RequestParam(value = "limit", defaultValue = "10000") Integer limit) {
        List<Timeline> timelines = timelineService.buildAllMetricsTimelines(resourceId, startTime, endTime);
        List<TimelineResponse> responses = new ArrayList<>();        for (Timeline t : timelines) {
            responses.add(toResponse(t));
        }
        return ApiResponse.success(responses);
    }

    /**
     * GET /api/v1/timelines/enhanced?resource_id=xxx&metric_name=xxx&start_time=xxx&end_time=xxx
     * 增强 Timeline（含趋势方向 + 变化率）
     */
    @GetMapping("/enhanced")
    public ApiResponse<TimelineResponse> getEnhancedTimeline(@Valid TimelineRequest request) {
        TimelineQuery query = new TimelineQuery(
                request.getResourceId(), request.getMetricName(),
                request.getStartTime(), request.getEndTime(),
                request.getLimit() != null ? request.getLimit() : 10000);
        Timeline timeline = timelineService.buildEnhancedTimeline(query);
        return ApiResponse.success(toResponse(timeline));
    }

    /**
     * Timeline → TimelineResponse
     */
    private TimelineResponse toResponse(Timeline timeline) {
        TimelineResponse response = new TimelineResponse();
        response.setTimelineId(timeline.getTimelineId());
        response.setResourceId(timeline.getResourceId());
        response.setMetricName(timeline.getMetricName());
        response.setStartTime(timeline.getStartTime());
        response.setEndTime(timeline.getEndTime());
        response.setPointCount(timeline.getPoints().size());

        List<TimelineResponse.TimelinePointDto> pointDtos = new ArrayList<>();
        for (TimelinePoint p : timeline.getPoints()) {
            TimelineResponse.TimelinePointDto dto = new TimelineResponse.TimelinePointDto();
            dto.setTimestamp(p.getTimestamp());
            dto.setValue(p.getValue());
            dto.setUnit(p.getUnit());
            pointDtos.add(dto);
        }
        response.setPoints(pointDtos);

        TimelineStats s = timeline.getStats();
        TimelineResponse.TimelineStatsDto statsDto = new TimelineResponse.TimelineStatsDto();
        statsDto.setMin(s.getMin());
        statsDto.setMax(s.getMax());
        statsDto.setAvg(s.getAvg());
        statsDto.setStdDev(s.getStdDev());
        statsDto.setCount(s.getCount());
        response.setStats(statsDto);
        return response;
    }
}
