package org.matsu.pojo;

import java.io.File;

public record ScrapedHopData(
    String country,
    String purpose,
    String alphaAcids,
    String betaAcids,
    String profile,
    File hopChartImage
){

}
