package model;

import java.util.List;

public class AdvertiserRecommendation {
    public int app_id;
    public String country_code;
    public List<Integer> recommended_advertiser_ids;

    public AdvertiserRecommendation(int app_id, String country_code, List<Integer> recommended_advertiser_ids) {
        this.app_id = app_id;
        this.country_code = country_code;
        this.recommended_advertiser_ids = recommended_advertiser_ids;
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

    public List<Integer> getRecommended_advertiser_ids() {
        return recommended_advertiser_ids;
    }

    public void setRecommended_advertiser_ids(List<Integer> recommended_advertiser_ids) {
        this.recommended_advertiser_ids = recommended_advertiser_ids;
    }
}
