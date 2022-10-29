package org.matsu.pojo;

import java.io.File;

public record HopData(
    String country,
    String purpose,
    String alphaAcids,
    String betaAcids,
    String profile,
    File hopChartImage
){
    public static final HopData NOT_INITIALIZED = new HopData(null, null, null, null, null, null);

}
