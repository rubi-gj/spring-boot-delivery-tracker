package deliverytracker.util;

import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Raw JDBC service for reporting and CRUD operations.
 * Covers R6 - JDBC (Create, Read, Update, Delete)
 */
@Service
public class JdbcReportService {

    private final DataSource dataSource;

    public JdbcReportService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ============ CREATE ============
    /**
     * R6 - JDBC CREATE: Insert a package activity log
     */
    public void insertPackageLog(String trackingCode,
                                 String action,
                                 String performedBy) {
        String sql = "INSERT INTO package_logs " +
                "(tracking_code, action, performed_by, " +
                "created_at) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trackingCode);
            ps.setString(2, action);
            ps.setString(3, performedBy);
            ps.setTimestamp(4,
                    Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();

            System.out.println("[JDBC CREATE] Log inserted: "
                    + trackingCode + " - " + action);

        } catch (SQLException e) {
            System.err.println("JDBC CREATE Error: "
                    + e.getMessage());
        }
    }

    // ============ READ ============
    /**
     * R6 - JDBC READ: Get package count by status
     */
    public Map<String, Integer> getPackageCountByStatus() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT status, COUNT(*) as total " +
                "FROM packages " +
                "GROUP BY status " +
                "ORDER BY total DESC";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.put(
                        rs.getString("status"),
                        rs.getInt("total")
                );
            }

        } catch (SQLException e) {
            System.err.println("JDBC READ Error: "
                    + e.getMessage());
        }

        return result;
    }

    /**
     * R6 - JDBC READ: Get recent packages
     */
    public List<Map<String, Object>> getRecentPackages() {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT tracking_code, sender_name, " +
                "destination, status, current_location " +
                "FROM packages " +
                "ORDER BY created_at DESC LIMIT 10";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("trackingCode",
                        rs.getString("tracking_code"));
                row.put("senderName",
                        rs.getString("sender_name"));
                row.put("destination",
                        rs.getString("destination"));
                row.put("status",
                        rs.getString("status"));
                row.put("currentLocation",
                        rs.getString("current_location"));
                result.add(row);
            }

        } catch (SQLException e) {
            System.err.println("JDBC READ Error: "
                    + e.getMessage());
        }

        return result;
    }

    /**
     * R6 - JDBC READ: Get total packages count
     */
    public int getTotalPackages() {
        String sql = "SELECT COUNT(*) FROM packages";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("JDBC READ Error: "
                    + e.getMessage());
        }

        return 0;
    }

    /**
     * R6 - JDBC READ: Get package logs
     */
    public List<Map<String, Object>> getPackageLogs(
            String trackingCode) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT * FROM package_logs " +
                "WHERE tracking_code = ? " +
                "ORDER BY created_at DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, trackingCode);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("trackingCode",
                        rs.getString("tracking_code"));
                row.put("action",
                        rs.getString("action"));
                row.put("performedBy",
                        rs.getString("performed_by"));
                row.put("createdAt",
                        rs.getTimestamp("created_at").toString());
                result.add(row);
            }

        } catch (SQLException e) {
            System.err.println("JDBC READ Error: "
                    + e.getMessage());
        }

        return result;
    }

    // ============ UPDATE ============
    /**
     * R6 - JDBC UPDATE: Update package location directly
     */
    public boolean updatePackageLocationJdbc(
            String trackingCode,
            String newLocation,
            String newStatus) {
        String sql = "UPDATE packages " +
                "SET current_location = ?, " +
                "status = ?, " +
                "updated_at = ? " +
                "WHERE tracking_code = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setString(1, newLocation);
            ps.setString(2, newStatus);
            ps.setTimestamp(3,
                    Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, trackingCode);

            int rows = ps.executeUpdate();
            System.out.println("[JDBC UPDATE] Updated "
                    + rows + " row(s) for " + trackingCode);
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("JDBC UPDATE Error: "
                    + e.getMessage());
            return false;
        }
    }

    // ============ DELETE ============
    /**
     * R6 - JDBC DELETE: Delete logs older than 30 days
     */
    public int deleteOldLogs() {
        String sql = "DELETE FROM package_logs " +
                "WHERE created_at < ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            Timestamp thirtyDaysAgo = Timestamp.valueOf(
                    LocalDateTime.now().minusDays(30));
            ps.setTimestamp(1, thirtyDaysAgo);

            int deleted = ps.executeUpdate();
            System.out.println("[JDBC DELETE] Deleted "
                    + deleted + " old log(s)");
            return deleted;

        } catch (SQLException e) {
            System.err.println("JDBC DELETE Error: "
                    + e.getMessage());
            return 0;
        }
    }

    /**
     * Create package_logs table if not exists
     */
    public void initializeLogsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS package_logs (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "tracking_code VARCHAR(255), " +
                "action VARCHAR(255), " +
                "performed_by VARCHAR(255), " +
                "created_at DATETIME)";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println(
                    "[JDBC] package_logs table ready!");

        } catch (SQLException e) {
            System.err.println("JDBC INIT Error: "
                    + e.getMessage());
        }
    }
}