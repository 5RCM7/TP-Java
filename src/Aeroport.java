public class Aeroport {
    private final String name;
    private final String iata;
    private final double latitude;   // Θ (degrés)
    private final double longitude;  // Φ (degrés)

    public Aeroport(String name, String iata, double latitude, double longitude) {
        this.name = name;
        this.iata = iata;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public String getIata() { return iata; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public String toString() {
        return "Aeroport{" +
                "name='" + name + '\'' +
                ", iata='" + iata + '\'' +
                ", lat=" + latitude +
                ", lon=" + longitude +
                '}';
    }

    /** Norme demandée dans le PDF (Θ,Φ en radians). */
    public double calculDistance(Aeroport other) {
        return calculDistance(other.longitude, other.latitude);
    }

    public double calculDistance(double lonDeg, double latDeg) {
        double th1 = Math.toRadians(this.latitude);
        double th2 = Math.toRadians(latDeg);
        double ph1 = Math.toRadians(this.longitude);
        double ph2 = Math.toRadians(lonDeg);

        double dTh = th2 - th1;
        double dPh = ph2 - ph1;
        double mean = (th1 + th2) / 2.0;

        return dTh * dTh + (dPh * Math.cos(mean)) * (dPh * Math.cos(mean));
    }
}

