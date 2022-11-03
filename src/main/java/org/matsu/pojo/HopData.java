package org.matsu.pojo;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record HopData(
    String country,
    String purpose,
    String alphaAcids,
    String betaAcids,
    String profile,
    File hopChartImage,
    List<Hop> pairings,
    Map<String, String> shopListings
){
    public static final HopData NOT_INITIALIZED = new HopData(null, null, null, null, null, null, Collections.emptyList(), Collections.emptyMap());

    public String pairingsFormatted() {
        if (pairings.isEmpty()) return "No pairings found";

        return pairings.stream()
            .map(hop -> String.format("[%s](%s)", hop.name(), hop.fullUrl()))
            .collect(Collectors.joining(", "));
    }

    public String shopListingsFormatted() {
        if (shopListings.isEmpty()) return "";

        return shopListings.entrySet().stream()
            .map(entry -> String.format("[%s](%s)", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(", "));
    }
}
