package model;

public class MetricsOutput {
    public int app_id;
    public String country_code;
    public int impressions;
    public int clicks;
    public double revenue;

    public MetricsOutput(int app_id, String country_code, Metrics metrics) {
        this.app_id = app_id;
        this.country_code = country_code;
        this.impressions = metrics.impressions;
        this.clicks = metrics.clicks;
        this.revenue = metrics.revenue;
    }

    public int getApp_id() {
        return app_id;
    }

    public void setApp_id(int app_id) {
        this.app_id = app_id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public int getImpressions() {
        return impressions;
    }

    public void setImpressions(int impressions) {
        this.impressions = impressions;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}
