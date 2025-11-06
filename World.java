package tp.dataflight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class World {
    private final ArrayList<Aeroport> list = new ArrayList<>();

    public World(String resourceCsvPath) {
        // Load CSV from classpath (resources) so it's portable
        try (InputStream is = getClass().getResourceAsStream(resourceCsvPath)) {
            if (is == null) {
                System.out.println("Maybe the file isn't there ? (" + resourceCsvPath + ")");
                return;
            }
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String s = buf.readLine();
            while (s != null) {
                s = s.replace("\"","");
                String[] fields = s.split(",");
                try {
                    if (fields.length > 11 && "large_airport".equals(fields[1])) {
                        String name = safe(fields, 2);
                        String iata = safe(fields, 9);
                        String coord = safe(fields, 11); // "lon, lat"
                        if (coord.contains(",")) {
                            String[] ll = coord.split("\\s*;?\\s*|\\s*,\\s*");
                            // Expect [lon, lat]
                            double lon = Double.parseDouble(ll[0]);
                            double lat = Double.parseDouble(ll[1]);
                            list.add(new Aeroport(name, iata, lat, lon));
                        }
                    }
                } catch (Exception ignore) {
                    // Skip malformed rows silently
                }
                s = buf.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String safe(String[] a, int i) {
        return (i >= 0 && i < a.length && a[i] != null) ? a[i].trim() : "";
    }

    public List<Aeroport> getList() { return list; }

    public Aeroport findByCode(String code) {
        if (code == null) return null;
        String up = code.trim().toUpperCase();
        for (Aeroport a : list) {
            if (Objects.equals(a.getIata().toUpperCase(), up)) return a;
        }
        return null;
    }

    public Aeroport findNearestAirport(double lon, double lat) {
        Aeroport best = null;
        double bestNorm = Double.POSITIVE_INFINITY;
        for (Aeroport a : list) {
            double n = distance(lon, lat, a.getLongitude(), a.getLatitude());
            if (n < bestNorm) {
                bestNorm = n;
                best = a;
            }
        }
        return best;
    }

    public static double distance(double lon1, double lat1, double lon2, double lat2) {
        // Equirectangular approximation metric (no sqrt*R), angles in degrees
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double lam1 = Math.toRadians(lon1);
        double lam2 = Math.toRadians(lon2);
        double x = (lam2 - lam1) * Math.cos((phi2 + phi1) / 2.0);
        double y = (phi2 - phi1);
        return x * x + y * y;
    }

    public static void main(String[] args) {
        World w = new World("/data/airport-codes_no_comma.csv");
        System.out.println("Found " + w.getList().size() + " airports.");
        Aeroport parisNearest = w.findNearestAirport(2.316,48.866);
        Aeroport ory = w.findByCode("ORY");
        System.out.println("Nearest to Paris: " + parisNearest);
        System.out.println("ORY: " + ory);
        double d = distance(2.316,48.866, parisNearest.getLongitude(), parisNearest.getLatitude());
        System.out.println("Norm to nearest: " + d);
    }
}
