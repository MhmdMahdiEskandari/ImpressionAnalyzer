package util;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/ad_metrics_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static List<MetricsOutput> getMetrics() {
        String sql = "SELECT app_id, country_code, COUNT(*) as impressions, " +
                "SUM(CASE WHEN clicks.impression_id IS NOT NULL THEN 1 ELSE 0 END) as clicks, " +
                "SUM(clicks.revenue) as revenue " +
                "FROM impressions " +
                "LEFT JOIN clicks ON impressions.id = clicks.impression_id " +
                "GROUP BY app_id, country_code";

        List<MetricsOutput> metricsList = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int appId = rs.getInt("app_id");
                String countryCode = rs.getString("country_code");
                int impressions = rs.getInt("impressions");
                int clicks = rs.getInt("clicks");
                double revenue = rs.getDouble("revenue");

                metricsList.add(new MetricsOutput(appId, countryCode, new Metrics(impressions, clicks, revenue)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return metricsList;
    }

    public static List<AdvertiserRecommendation> getRecommendations() {
        String sql = "SELECT i.app_id, i.country_code, " +
                "GROUP_CONCAT(DISTINCT i.advertiser_id ORDER BY total_revenue DESC) AS recommended_advertiser_ids " +
                "FROM impressions i " +
                "LEFT JOIN (SELECT impression_id, SUM(revenue) AS total_revenue FROM clicks GROUP BY impression_id ) c ON i.id = c.impression_id " +
                "GROUP BY i.app_id, i.country_code";

        List<AdvertiserRecommendation> recommendations = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int appId = rs.getInt("app_id");
                String countryCode = rs.getString("country_code");
                String[] advertiserIdsArray = rs.getString("recommended_advertiser_ids").split(",");
                List<Integer> advertiserIds = new ArrayList<>();

                for (String id : advertiserIdsArray) {
                    advertiserIds.add(Integer.parseInt(id));
                }

                recommendations.add(new AdvertiserRecommendation(appId, countryCode, advertiserIds));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recommendations;
    }

    public static void insertRecommendationsIntoDatabase(List<AdvertiserRecommendation> recommendations) {
        String sql = "INSERT INTO recommendations (app_id, country_code, advertiser_ids) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (AdvertiserRecommendation recommendation : recommendations) {
                String advertiserIds = recommendation.getRecommended_advertiser_ids().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));

                pstmt.setInt(1, recommendation.getApp_id());
                pstmt.setString(2, recommendation.getCountry_code());
                pstmt.setString(3, advertiserIds);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertMetricsIntoDatabase(Map<String, Metrics> metricsMap) {
        String sql = "INSERT INTO metrics (app_id, country_code, impressions, clicks, revenue) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Map.Entry<String, Metrics> entry : metricsMap.entrySet()) {
                String[] keys = entry.getKey().split("-");
                if (keys.length < 2 || keys[1].equals("null")) {
                    continue; // Skip entries without valid country codes
                }
                int appId = Integer.parseInt(keys[0]);
                String countryCode = keys[1];
                Metrics metrics = entry.getValue();

                pstmt.setInt(1, appId);
                pstmt.setString(2, countryCode);
                pstmt.setInt(3, metrics.impressions);
                pstmt.setInt(4, metrics.clicks);
                pstmt.setDouble(5, metrics.revenue);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertOrUpdateImpression(Impression impression) {
        String selectSql = "SELECT COUNT(*) FROM impressions WHERE id = ?";
        String insertSql = "INSERT INTO impressions (id, app_id, country_code, advertiser_id) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE impressions SET app_id = ?, country_code = ?, advertiser_id = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, impression.id);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0 && impression.id != null) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, impression.app_id);
                        updateStmt.setString(2, impression.country_code);
                        updateStmt.setInt(3, impression.advertiser_id);
                        updateStmt.setString(4, impression.id);
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, impression.id);
                        insertStmt.setInt(2, impression.app_id);
                        insertStmt.setString(3, impression.country_code);
                        insertStmt.setInt(4, impression.advertiser_id);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertClick(Click click) {
        String selectSql = "SELECT COUNT(*) FROM clicks WHERE impression_id = ? AND revenue = ?";
        String insertSql = "INSERT INTO clicks (impression_id, revenue) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, click.impression_id);
                selectStmt.setDouble(2, click.revenue);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, click.impression_id);
                        insertStmt.setDouble(2, click.revenue);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}