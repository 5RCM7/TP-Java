package tp.dataflight;

public class Aeroport {
    private final String name;
    private final String iata; // IATA code (3 letters) – may be empty in CSV
    private final double latitude;
    private final double longitude;

    public Aeroport(String name, String iata, double latitude, double longitude) {
        this.name = name;
        this.iata = iata == null ? "" : iata.trim();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public String getIata() { return iata; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    // Approximate equirectangular metric (no sqrt*R) per TP
    public double calculDistance(Aeroport other) {
        return World.distance(this.longitude, this.latitude, other.longitude, other.latitude);
    }

    @Override
    public String toString() {
        return "Aeroport{" +
                "name='" + name + '\'' +
                ", iata='" + iata + '\'' +
                ", lat=" + latitude +
                ", lon=" + longitude +
                '}';
    }
}
