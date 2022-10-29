package org.matsu.pojo;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record HopData(
    String country,
    String purpose,
    String alphaAcids,
    String betaAcids,
    String profile,
    File hopChartImage,
    List<Hop> pairings
){
    public static final HopData NOT_INITIALIZED = new HopData(null, null, null, null, null, null, Collections.emptyList());

    public String pairingsFormatted() {
        return pairings.stream()
            .map(hop -> "[" + hop.name() + "](" + hop.fullUrl() + ")")
            .collect(Collectors.joining(", "));
    }

}
