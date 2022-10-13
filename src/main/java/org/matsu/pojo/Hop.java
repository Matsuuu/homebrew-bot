package org.matsu.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Hop(@JsonProperty("value") String name, @JsonProperty("data") String dataName) {
    public String url() {
        if (name.isEmpty()) return "";
        return "/hop/" + dataName;
    }

    public static Hop NOT_FOUND = new Hop("", "");
}
