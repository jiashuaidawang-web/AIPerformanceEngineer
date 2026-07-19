package com.aipe.config.audit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class AuditService {
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final List<AuditEntry> entries = new ArrayList<>();
    public void record(String operation, String target) { AuditEntry e = new AuditEntry(); e.timestamp = LocalDateTime.now(); e.operator = "admin"; e.operation = operation; e.target = target; entries.add(e); log.debug("Audit: {} on {}", operation, target); }
    public List<AuditEntry> list() { return new ArrayList<>(entries); }
    public static class AuditEntry { public LocalDateTime timestamp; public String operator; public String operation; public String target; }
}
