package tp.dataflight;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonFlightFiller {
    private final ArrayList<Flight> list = new ArrayList<>();

    public JsonFlightFiller(String jsonString, World w) {
        try {
            InputStream is = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            JsonReader rdr = Json.createReader(is);
            JsonObject obj = rdr.readObject();
            JsonArray results = obj.getJsonArray("data");
            if (results == null) return;

            for (JsonObject result : results.getValuesAs(JsonObject.class)) {
                try {
                    JsonObject dep = result.getJsonObject("departure");
                    JsonObject arr = result.getJsonObject("arrival");
                    String depIata = dep != null ? dep.getString("iata", "") : "";
                    String arrIata = arr != null ? arr.getString("iata", "") : "";

                    Aeroport depA = w.findByCode(depIata);
                    Aeroport arrA = w.findByCode(arrIata);
                    if (arrA != null || depA != null) {
                        list.add(new Flight(depA, arrA));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Flight> getFlights() {
        return list;
    }

    public static void main(String[] args) throws Exception {
        World w = new World("/data/airport-codes_no_comma.csv");
        String test = new String(JsonFlightFiller.class.getResourceAsStream("/data/test.txt").readAllBytes(), StandardCharsets.UTF_8);
        JsonFlightFiller filler = new JsonFlightFiller(test, w);
        System.out.println("Flights: " + filler.getFlights().size());
        for (Flight f : filler.getFlights()) System.out.println(f);
    }
}
