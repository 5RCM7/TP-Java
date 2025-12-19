import javax.json.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class JsonFlightFiller {
    private final World world;

    public JsonFlightFiller(World world) {
        this.world = world;
    }

    /** Toutes les origines (distinctes) des vols qui ARRIVENT à iataArrival. */
    public Set<Aeroport> distinctOriginsArrivingTo(String iataArrival) {
        List<Flight> flights = flightsArrivingTo(iataArrival);
        Set<Aeroport> origins = new HashSet<>();
        for (Flight f : flights) origins.add(f.getOrigin());
        return origins;
    }

    /** Liste des vols arrivant à iataArrival (origine -> destination). */
    public List<Flight> flightsArrivingTo(String iataArrival) {
        String json = readResourceAsString("/data/Interrogation_Orly.json");
        if (json == null || json.isBlank()) return List.of();

        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject root = reader.readObject();
        JsonArray data = root.getJsonArray("data");
        if (data == null) return List.of();

        List<Flight> flights = new ArrayList<>();
        for (JsonValue v : data) {
            if (!(v instanceof JsonObject)) continue;
            JsonObject obj = (JsonObject) v;

            JsonObject dep = obj.getJsonObject("departure");
            JsonObject arr = obj.getJsonObject("arrival");
            JsonObject airline = obj.getJsonObject("airline");
            JsonObject flight = obj.getJsonObject("flight");

            String depIata = stringOf(dep, "iata");
            String arrIata = stringOf(arr, "iata");
            if (depIata == null || arrIata == null) continue;
            if (!arrIata.equalsIgnoreCase(iataArrival)) continue;

            String airlineName = stringOf(airline, "name");
            String flightNumber = stringOf(flight, "number");

            Aeroport origin = world.findByCode(depIata);
            Aeroport dest   = world.findByCode(arrIata);

            if (origin != null && dest != null) {
                flights.add(new Flight(origin, dest,
                        airlineName != null ? airlineName : "",
                        flightNumber != null ? flightNumber : ""));
            }
        }
        return flights;
    }

    /* Utils */

    private static String stringOf(JsonObject o, String key) {
        if (o == null || !o.containsKey(key) || o.isNull(key)) return null;
        JsonValue val = o.get(key);
        return (val.getValueType() == JsonValue.ValueType.STRING) ? ((JsonString) val).getString() : null;
    }

    private static String readResourceAsString(String resourcePath) {
        try (InputStream is = JsonFlightFiller.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("JSON resource not found: " + resourcePath);
                return null;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append('\n');
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
