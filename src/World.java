import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class World {

    private final ArrayList<Aeroport> list = new ArrayList<>();

    /** Constructeur conforme au PDF : lit le CSV et ne garde que les "large_airport". */
    public World(String fileName) {
        try {
            BufferedReader buf = new BufferedReader(new FileReader(fileName));

            String s = buf.readLine(); // première ligne (header éventuel)
            while (s != null) {
                s = s.replaceAll("\"", "");
                // enlève les guillemets
                String[] fields = s.split(",");
                // découpe par virgules

                // On ne traite que les lignes valides ET de type "large_airport"
                if (fields.length > 12 && "large_airport".equals(fields[1])) {
                    String name   = fields[2];
                    String iata   = fields[9];
                    String lonStr = fields[11];
                    String latStr = fields[12];

                    if (iata != null && !iata.isBlank()
                            && lonStr != null && !lonStr.isBlank()
                            && latStr != null && !latStr.isBlank()) {
                        try {
                            double lon = Double.parseDouble(lonStr.trim());
                            double lat = Double.parseDouble(latStr.trim());
                            list.add(new Aeroport(name, iata.trim(), lat, lon));
                        } catch (NumberFormatException ignored) {
                            // ignore la ligne mal formée
                        }
                    }
                }

                s = buf.readLine();
            }
        } catch (Exception e) {
            System.out.println("Maybe the file isn't there ?");
            if (!list.isEmpty()) {
                System.out.println(list.get(list.size() - 1));
            }
            e.printStackTrace();
        }
    }

    /** Accès à la liste (le PDF l’emploie pour afficher la taille). */
    public List<Aeroport> getList() {
        return list;
    }

    /** Recherche exacte par code IATA (ex: "ORY", "CDG"). */
    public Aeroport findByCode(String iata) {
        if (iata == null) return null;
        String key = iata.trim();
        for (Aeroport a : list) {
            if (key.equalsIgnoreCase(a.getIata())) return a;
        }
        return null;
    }

    /** Norme de proximité (sans racine), conforme à la formule du PDF. */
    public double distance(double lon1, double lat1, double lon2, double lat2) {
        double th1 = Math.toRadians(lat1);
        double th2 = Math.toRadians(lat2);
        double ph1 = Math.toRadians(lon1);
        double ph2 = Math.toRadians(lon2);

        double dTh = th2 - th1;
        double dPh = ph2 - ph1;
        double mean = (th1 + th2) / 2.0;

        return dTh * dTh + (dPh * Math.cos(mean)) * (dPh * Math.cos(mean));
    }

    /** Renvoie l’aéroport dont la norme à (lon,lat) est minimale. */
    public Aeroport findNearestAirport(double lon, double lat) {
        Aeroport best = null;
        double bestNorm = Double.MAX_VALUE;
        for (Aeroport a : list) {
            double n = distance(lon, lat, a.getLongitude(), a.getLatitude());
            if (n < bestNorm) {
                bestNorm = n;
                best = a;
            }
        }
        return best;
    }
}
