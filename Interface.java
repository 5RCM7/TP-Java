package tp.dataflight;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Interface extends Application {
    private Earth earth;
    private World world;
    private PerspectiveCamera camera;
    private double lastY = -1;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DataFlight — ENSEA");

        earth = new Earth();
        world = new World("/data/airport-codes_no_comma.csv");

        Pane pane = new Pane();
        Group root = new Group(earth);
        pane.getChildren().add(root);

        Scene ihm = new Scene(pane, 1000, 700, true);

        camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        camera.setNearClip(0.1);
        camera.setFarClip(5000.0);
        camera.setFieldOfView(35);
        ihm.setCamera(camera);

        // Basic zoom with left drag
        ihm.addEventHandler(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.getButton() == MouseButton.PRIMARY) {
                lastY = event.getSceneY();
            }
            if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && event.getButton() == MouseButton.PRIMARY) {
                double dy = event.getSceneY() - lastY;
                camera.setTranslateZ(camera.getTranslateZ() + dy);
                lastY = event.getSceneY();
            }

            if (event.getButton() == MouseButton.SECONDARY && event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                PickResult pr = event.getPickResult();
                if (pr.getIntersectedNode() != null) {
                    Point2D uv = pr.getIntersectedTexCoord();
                    if (uv != null) {
                        double[] latlon = Earth.uvToLatLon(uv);
                        double lat = latlon[0], lon = latlon[1];
                        Aeroport nearest = world.findNearestAirport(lon, lat);
                        System.out.println("Pick lat=" + lat + " lon=" + lon + " -> nearest: " + nearest);
                        earth.displayRedSphere(nearest);

                        // Fetch flights for arrival airport and display yellow spheres for departures
                        new Thread(() -> fetchAndDisplayDepartures(nearest)).start();
                    }
                }
            }
        });

        primaryStage.setScene(ihm);
        primaryStage.show();
    }

    private void fetchAndDisplayDepartures(Aeroport arrival) {
        if (arrival == null || arrival.getIata().isEmpty()) return;
        try {
            String apiKey = System.getenv("AVIATIONSTACK_KEY");
            String payload;
            if (apiKey != null && !apiKey.isEmpty()) {
                String url = "http://api.aviationstack.com/v1/flights?access_key=" + apiKey + "&arr_iata=" + arrival.getIata();
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
                HttpResponse<byte[]> resp = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
                payload = new String(resp.body(), StandardCharsets.UTF_8);
            } else {
                // Fallback to bundled test
                payload = new String(getClass().getResourceAsStream("/data/test.txt").readAllBytes(), StandardCharsets.UTF_8);
            }
            JsonFlightFiller filler = new JsonFlightFiller(payload, world);
            List<Flight> flights = filler.getFlights();
            System.out.println("Flights found: " + flights.size());
            // UI update on FX thread
            javafx.application.Platform.runLater(() -> {
                for (Flight f : flights) {
                    if (f.getDeparture() != null) earth.displayYellowSphere(f.getDeparture());
                }
            });
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
