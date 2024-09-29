package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.AdvertiserRecommendation;
import model.Click;
import model.Impression;
import model.MetricsOutput;
import util.DatabaseUtil;

import java.io.File;
import java.util.List;

public class AdMetrics {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Impression[] impressions = mapper.readValue(new File("src/main/resources/impressions.json"), Impression[].class);
        Click[] clicks = mapper.readValue(new File("src/main/resources/clicks.json"), Click[].class);

        DatabaseUtil dbUtil = new DatabaseUtil();

        // Insert impressions into the database
        for (Impression impression : impressions) {
            dbUtil.insertOrUpdateImpression(impression);
        }

        // Insert clicks into the database
        for (Click click : clicks) {
            dbUtil.insertClick(click);
        }

        List<MetricsOutput> metricsOutputList = DatabaseUtil.getMetrics();
        List<AdvertiserRecommendation> recommendations = DatabaseUtil.getRecommendations();

        mapper.writeValue(new File("src/main/resources/metrics_output.json"), metricsOutputList);
        mapper.writeValue(new File("src/main/resources/recommendations.json"), recommendations);
    }
}
