package tn.esprit.clinique.facturation.client;

public record CoverageLookup(int tauxPriseEnCharge, boolean available) {

    public static CoverageLookup available(int rate) {
        return new CoverageLookup(rate, true);
    }

    public static CoverageLookup unavailable() {
        return new CoverageLookup(0, false);
    }
}
