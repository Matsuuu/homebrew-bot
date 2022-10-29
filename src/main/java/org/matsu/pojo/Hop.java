package org.matsu.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Hop(@JsonProperty("value") String name, @JsonProperty("data") String dataName, HopData hopData) {

    public static final String HOP_SITE_BASE = "https://beermaverick.com";
    public static final String HOP_SITE_URL = HOP_SITE_BASE + "/hops/";
    public static final String HOP_DATA_API_URL = HOP_SITE_BASE + "/api/js/?hop=";

    public static final Hop NOT_FOUND = new Hop("", "", null);

    public Hop(String name, String dataName) {
        this(name, dataName, HopData.NOT_INITIALIZED);
    }

    public String url() {
        if (dataName.isEmpty()) return "";
        return "/hop/" + dataName;
    }

    public String fullUrl() {
        if (dataName.isEmpty()) return "";
        return HOP_SITE_BASE + url();
    }

    public String imageName() {
        return dataName + ".png";
    }

}
