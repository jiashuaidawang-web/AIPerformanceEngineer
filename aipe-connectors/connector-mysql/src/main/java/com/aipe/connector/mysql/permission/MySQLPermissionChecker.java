package com.aipe.connector.mysql.permission;

import com.aipe.connector.mysql.client.MySQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class MySQLPermissionChecker {
    private static final Logger log = LoggerFactory.getLogger(MySQLPermissionChecker.class);

    public boolean checkSelectPermission(MySQLConnection connection) {
        final boolean[] result = {false};
        connection.query("SELECT 1", rs -> {
            try { result[0] = rs.next(); } catch (Exception e) {}
        });
        return result[0];
    }

    public boolean checkShowViewPermission(MySQLConnection connection) {
        final boolean[] result = {false};
        connection.query("SHOW PROCESSLIST", rs -> {
            try { result[0] = true; } catch (Exception e) {}
        });
        return result[0];
    }

    public boolean checkPerformanceSchemaAccess(MySQLConnection connection) {
        final boolean[] result = {false};
        connection.query("SELECT 1 FROM performance_schema.data_lock_waits LIMIT 1", rs -> {
            try { result[0] = true; } catch (Exception e) {}
        });
        return result[0];
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
