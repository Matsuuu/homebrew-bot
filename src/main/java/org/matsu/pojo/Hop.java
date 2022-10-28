package org.matsu.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Hop(@JsonProperty("value") String name, @JsonProperty("data") String dataName, HopData hopData) {
    public static Hop NOT_FOUND = new Hop("", "", null);

    public Hop(String name, String dataName) {
        this(name, dataName, null);
    }

    public String url() {
        if (name.isEmpty()) return "";
        return "/hop/" + dataName;
    }

    public String imageName() {
        return dataName + ".png";
    }

}
