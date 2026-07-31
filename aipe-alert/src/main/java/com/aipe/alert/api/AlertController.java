package com.aipe.alert.api;

import com.aipe.alert.domain.AlertRule;
import com.aipe.alert.domain.AlertRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 告警规则 REST Controller
 *
 * <p>API:
 * <ul>
 *   <li>POST /api/v1/alerts/rules - 创建规则</li>
 *   <li>GET /api/v1/alerts/rules - 规则列表</li>
 *   <li>PUT /api/v1/alerts/rules/{id} - 更新规则</li>
 *   <li>DELETE /api/v1/alerts/rules/{id} - 删除规则</li>
 *   <li>GET /api/v1/alerts/records - 告警记录</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    // 临时内存存储 (后续替换为 MySQL)
    private static final Map<String, AlertRule> rules = new HashMap<>();
    private static final List<AlertRecord> records = new ArrayList<>();

    /**
     * 创建告警规则
     */
    @PostMapping("/rules")
    public Map<String, Object> createRule(@RequestBody AlertRule rule) {
        if (rule.getRuleId() == null) {
            rule.setRuleId("rule-" + UUID.randomUUID().toString().substring(0, 8));
        }
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        if (rule.getEnabled() == null) rule.setEnabled(true);
        rules.put(rule.getRuleId(), rule);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("message", "success");
        result.put("data", rule);
        return result;
    }

    /**
     * 获取规则列表
     */
    @GetMapping("/rules")
    public Map<String, Object> listRules() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("message", "success");
        result.put("data", new ArrayList<>(rules.values()));
        return result;
    }

    /**
     * 更新规则
     */
    @PutMapping("/rules/{id}")
    public Map<String, Object> updateRule(@PathVariable("id") String id, @RequestBody AlertRule rule) {
        rule.setRuleId(id);
        rule.setUpdatedAt(LocalDateTime.now());
        rules.put(id, rule);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("message", "success");
        result.put("data", rule);
        return result;
    }

    /**
     * 删除规则
     */
    @DeleteMapping("/rules/{id}")
    public Map<String, Object> deleteRule(@PathVariable("id") String id) {
        rules.remove(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("message", "success");
        return result;
    }

    /**
     * 获取告警记录
     */
    @GetMapping("/records")
    public Map<String, Object> listRecords(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "severity", required = false) String severity) {
        List<AlertRecord> filtered = new ArrayList<>();
        for (AlertRecord r : records) {
            if (status != null && !status.equals(r.getStatus())) continue;
            if (severity != null && !severity.equals(r.getSeverity())) continue;
            filtered.add(r);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("message", "success");
        result.put("data", filtered);
        return result;
    }

    /**
     * 模拟触发告警 (测试用)
     */
    @PostMapping("/test/trigger")
    public Map<String, Object> testTrigger(@RequestBody Map<String, Object> params) {
        AlertRecord record = new AlertRecord();
        record.setAlertId("alert-" + UUID.randomUUID().toString().substring(0, 8));
        record.setResourceId((String) params.get("resourceId"));
        record.setMetricName((String) params.get("metricName"));
        record.setTriggerValue(Double.parseDouble(params.get("value").toString()));
        record.setThreshold(Double.parseDouble(params.get("threshold").toString()));
        record.setSeverity((String) params.get("severity"));
        record.setMessage(String.format("%s 指标 %.2f 超过阈值 %.2f",
                record.getMetricName(), record.getTriggerValue(), record.getThreshold()));
        record.setStatus("FIRING");
        record.setTriggeredAt(LocalDateTime.now());
        records.add(record);

        // 发送通知
        sendNotification(record);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("message", "success");
        result.put("data", record);
        return result;
    }

    /**
     * 发送告警通知
     */
    private void sendNotification(AlertRecord record) {
        // 查找匹配的规则
        for (AlertRule rule : rules.values()) {
            if (!rule.getEnabled()) continue;
            if (rule.getResourceId() != null && !rule.getResourceId().equals(record.getResourceId())) continue;
            if (rule.getMetricName() != null && !rule.getMetricName().equals(record.getMetricName())) continue;

            // 发送通知
            String notifyType = rule.getNotifyType();
            String notifyTarget = rule.getNotifyTarget();

            if ("webhook".equals(notifyType)) {
                sendWebhook(notifyTarget, record);
            } else if ("dingtalk".equals(notifyType)) {
                sendDingtalk(notifyTarget, record);
            } else if ("wecom".equals(notifyType)) {
                sendWecom(notifyTarget, record);
            }
        }
    }

    private void sendWebhook(String url, AlertRecord record) {
        // 实际项目使用 RestTemplate 或 HttpClient 发送 POST 请求
        System.out.println("[Webhook] Sending alert to: " + url);
        System.out.println("[Webhook] Alert: " + record.getMessage());
    }

    private void sendDingtalk(String webhookUrl, AlertRecord record) {
        System.out.println("[DingTalk] Sending alert to: " + webhookUrl);
        System.out.println("[DingTalk] Alert: " + record.getMessage());
    }

    private void sendWecom(String webhookUrl, AlertRecord record) {
        System.out.println("[WeCom] Sending alert to: " + webhookUrl);
        System.out.println("[WeCom] Alert: " + record.getMessage());
    }
}
