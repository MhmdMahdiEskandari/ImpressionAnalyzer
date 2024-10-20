package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import helper.Advertiser;
import model.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class AdMetrics {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Impression[] impressions = mapper.readValue(new File("src/main/resources/impressions.json"), Impression[].class);
        Click[] clicks = mapper.readValue(new File("src/main/resources/clicks.json"), Click[].class);

        Map<String, Metrics> metricsMap = new HashMap<>();
        Map<String, Map<Integer, List<Double>>> advertiserRevenueMap = new HashMap<>();

        for (Impression impression : impressions) {
            String key = impression.app_id + "-" + impression.country_code;
            metricsMap.putIfAbsent(key, new Metrics());
            metricsMap.get(key).impressions++;

            advertiserRevenueMap.putIfAbsent(key, new HashMap<>());
            advertiserRevenueMap.get(key).putIfAbsent(impression.advertiser_id, new ArrayList<>());
            advertiserRevenueMap.get(key).get(impression.advertiser_id).add(0.0); // No revenue yet
        }

        for (Click click : clicks) {
            for (Impression impression : impressions) {
                if (click.impression_id.equals(impression.id)) {
                    String key = impression.app_id + "-" + impression.country_code;
                    metricsMap.get(key).clicks++;
                    metricsMap.get(key).revenue += click.revenue;

                    // Update advertiser revenue
                    advertiserRevenueMap.get(key).get(impression.advertiser_id).set(0,
                            advertiserRevenueMap.get(key).get(impression.advertiser_id).get(0) + click.revenue);
                    break;
                }
            }
        }

        List<MetricsOutput> metricsOutputList = new ArrayList<>();
        for (Map.Entry<String, Metrics> entry : metricsMap.entrySet()) {
            String[] keys = entry.getKey().split("-");
            if (keys.length < 2 || keys[1].equals("null")) {
                continue;
            }
            String countryCode = keys[1];
            MetricsOutput output = new MetricsOutput(Integer.parseInt(keys[0]), countryCode, entry.getValue());
            metricsOutputList.add(output);
        }
        mapper.writeValue(new File("src/main/resources/metrics_output.json"), metricsOutputList);

        List<AdvertiserRecommendation> recommendations = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, List<Double>>> entry : advertiserRevenueMap.entrySet()) {
            String[] keys = entry.getKey().split("-");
            int appId = Integer.parseInt(keys[0]);
            if (keys.length < 2 || keys[1].equals("null")) {
                continue;
            }
            String countryCode = keys[1];
            List<Advertiser> advertisers = new ArrayList<>();
            for (Map.Entry<Integer, List<Double>> advEntry : entry.getValue().entrySet()) {
                int advId = advEntry.getKey();
                double revenue = advEntry.getValue().get(0);
                double impressionsValue = metricsMap.get(entry.getKey()).impressions;
                double revenuePerImpression = (impressionsValue > 0) ? revenue / impressionsValue : 0.0;
                advertisers.add(new Advertiser(advId, revenuePerImpression));
            }
            advertisers.sort(Comparator.comparingDouble(Advertiser::getRevenuePerImpression).reversed());
            List<Integer> topAdvertisers = advertisers.stream().limit(5).map(Advertiser::getId).collect(Collectors.toList());
            recommendations.add(new AdvertiserRecommendation(appId, countryCode, topAdvertisers));
        }
        mapper.writeValue(new File("src/main/resources/recommendations.json"), recommendations);
    }
}