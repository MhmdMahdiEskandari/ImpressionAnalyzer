package model;

public class Metrics {
    public int impressions = 0;
    public int clicks = 0;
    public double revenue = 0.0;

    public Metrics(int impressions, int clicks, double revenue) {
        this.impressions = impressions;
        this.clicks = clicks;
        this.revenue = revenue;
    }

    public Metrics() {
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