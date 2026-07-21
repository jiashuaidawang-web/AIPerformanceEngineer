package com.aipe.resource.api.controller;

import com.aipe.resource.api.dto.ApiResponse;
import com.aipe.resource.api.dto.ResourceReport;
import com.aipe.resource.application.ResourceDiscoveryService;
import com.aipe.resource.domain.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源发现 REST Controller
 *
 * <p>Law-001：所有 Connector/Agent 上报的资源信息通过本 Controller 进入系统
 * <p>禁止 Connector/Agent 直接调用 ResourceLifecycleManager
 *
 * <p>API 列表：
 * <ul>
 *   <li>POST /api/v1/resources/discover              单条资源上报</li>
 *   <li>POST /api/v1/resources/discover/batch        批量资源上报</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/resources/discover")
public class ResourceReportController {

    private static final Logger log = LoggerFactory.getLogger(ResourceReportController.class);

    @Autowired
    private ResourceDiscoveryService discoveryService;

    /**
     * 单条资源上报
     * POST /api/v1/resources/discover
     *
     * <p>幂等：同一 resourceId 多次上报会更新而非重复创建
     */
    @PostMapping
    public ApiResponse<Map<String, String>> discover(@Valid @RequestBody ResourceReport report) {
        Resource resource = discoveryService.handleResourceReport(
                report.getResourceId(),
                report.getResourceName(),
                report.getResourceType(),
                report.getBusinessSystem(),
                report.getHost(),
                report.getPort(),
                report.getLabels()
        );
        Map<String, String> data = new HashMap<>();
        data.put("resourceId", resource.getId().getValue());
        data.put("status", "ok");
        return ApiResponse.success(data);
    }

    /**
     * 批量资源上报
     * POST /api/v1/resources/discover/batch
     */
    @PostMapping("/batch")
    public ApiResponse<Map<String, Object>> discoverBatch(@RequestBody List<Map<String, String>> reports) {
        int count = discoveryService.batchHandleResourceReport(reports);
        Map<String, Object> data = new HashMap<>();
        data.put("processed", count);
        data.put("total", reports != null ? reports.size() : 0);
        return ApiResponse.success(data);
    }
}
