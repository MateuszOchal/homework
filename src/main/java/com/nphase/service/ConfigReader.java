package com.nphase.service;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class ConfigReader {
//    private static final Properties properties = new Properties();
//
//    static {
//        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
//            properties.load(inputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    public int getNumberOfItemsNeededForDiscount() {
//        return Integer.parseInt(properties.getProperty("numberOfItemsNeededForDiscount", "3"));
//    }
//
//    public double getDiscountSizeInPercent() {
//        return Double.parseDouble(properties.getProperty("discountSizeInPercent", "0.10"));
//    }
private Map<String, Object> config;

    public ConfigReader(String configFile) {
        Yaml yaml = new Yaml();

        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(configFile)) {
            config = yaml.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            config = null;
        }
    }

    public double getDiscountSizeInPercent() {
        if (config != null && config.containsKey("discount") && config.get("discount") instanceof Map) {
            Map<String, Object> discountConfig = (Map<String, Object>) config.get("discount");
            if (discountConfig.containsKey("size") && discountConfig.get("size") instanceof Double) {
                return (Double) discountConfig.get("size");
            }
        }
        return 0.0;
    }

    public int getNumberOfItemsNeededForDiscount() {
        if (config != null && config.containsKey("discount") && config.get("discount") instanceof Map) {
            Map<String, Object> discountConfig = (Map<String, Object>) config.get("discount");
            if (discountConfig.containsKey("threshold") && discountConfig.get("threshold") instanceof Integer) {
                return (Integer) discountConfig.get("threshold");
            }
        }
        return 0;
    }
}
