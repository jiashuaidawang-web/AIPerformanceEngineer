package com.aipe.connector.mysql.permission;

import com.aipe.connector.mysql.client.MySQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class MySQLPermissionChecker {
    private static final Logger log = LoggerFactory.getLogger(MySQLPermissionChecker.class);

    public boolean checkSelectPermission(MySQLConnection connection) {
        try {
            ResultSet rs = connection.executeQuery("SELECT 1");
            rs.close();
            return true;
        } catch (Exception e) {
            log.warn("SELECT permission check failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean checkShowViewPermission(MySQLConnection connection) {
        try {
            ResultSet rs = connection.executeQuery("SHOW PROCESSLIST");
            rs.close();
            return true;
        } catch (Exception e) {
            log.warn("SHOW VIEW permission check failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean checkPerformanceSchemaAccess(MySQLConnection connection) {
        try {
            ResultSet rs = connection.executeQuery("SELECT 1 FROM performance_schema.data_lock_waits LIMIT 1");
            rs.close();
            return true;
        } catch (Exception e) {
            log.debug("performance_schema access not available: {}", e.getMessage());
            return false;
        }
    }

    public PermissionReport checkAll(MySQLConnection connection) {
        PermissionReport report = new PermissionReport();
        report.setCanSelect(checkSelectPermission(connection));
        report.setCanShowView(checkShowViewPermission(connection));
        report.setCanAccessPerformanceSchema(checkPerformanceSchemaAccess(connection));
        return report;
    }

    public static class PermissionReport {
        private boolean canSelect;
        private boolean canShowView;
        private boolean canAccessPerformanceSchema;

        public boolean isCanSelect() { return canSelect; }
        public void setCanSelect(boolean canSelect) { this.canSelect = canSelect; }
        public boolean isCanShowView() { return canShowView; }
        public void setCanShowView(boolean canShowView) { this.canShowView = canShowView; }
        public boolean isCanAccessPerformanceSchema() { return canAccessPerformanceSchema; }
        public void setCanAccessPerformanceSchema(boolean canAccessPerformanceSchema) { this.canAccessPerformanceSchema = canAccessPerformanceSchema; }
    }
}
