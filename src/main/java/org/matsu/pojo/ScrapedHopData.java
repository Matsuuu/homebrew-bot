package org.matsu.pojo;

public record ScrapedHopData(
    String country,
    String purpose,
    String alphaAcids,
    String betaAcids,
    String profile,
    File hopChartImage
){

}
