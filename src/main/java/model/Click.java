package model;

public class Click {
    public String impression_id;
    public double revenue;

    public String getImpression_id() {
        return impression_id;
    }

    public void setImpression_id(String impression_id) {
        this.impression_id = impression_id;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}