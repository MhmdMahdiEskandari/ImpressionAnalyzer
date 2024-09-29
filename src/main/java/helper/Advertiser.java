package helper;

public class Advertiser {
    private int id;
    private double revenuePerImpression;

    public Advertiser(int id, double revenuePerImpression) {
        this.id = id;
        this.revenuePerImpression = revenuePerImpression;
    }

    public int getId() {
        return id;
    }

    public double getRevenuePerImpression() {
        return revenuePerImpression;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRevenuePerImpression(double revenuePerImpression) {
        this.revenuePerImpression = revenuePerImpression;
    }
}
