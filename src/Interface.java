import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.PointLight;
import javafx.stage.Stage;


 /* Test hello world
public class Interface extends Application {
    @Override

    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Hello world");
        Group root = new Group();
        Pane pane = new Pane(root);
        Scene theScene = new Scene(pane, 600, 400, true);
        primaryStage.setScene(theScene);
        primaryStage.show();

    }

 */
 /*  Test Earth
 public class Interface extends Application {
     private Earth earth;
     private World world;

     private double lastY;  // pour le zoom au drag

     @Override
     public void start(Stage primaryStage) {
         primaryStage.setTitle("Catch me if you can! – Partie 5");
         Group root = new Group();
         Scene scene = new Scene(root, 1200, 800, true);
         primaryStage.setScene(scene);

         // Caméra 3D (Perspective)
         PerspectiveCamera camera = new PerspectiveCamera(true);
         camera.setTranslateZ(-1000);   // éloignée au départ
         camera.setNearClip(0.1);
         camera.setFarClip(5000.0);
         camera.setFieldOfView(35);
         scene.setCamera(camera);

         // Terre
         earth = new Earth();
         root.getChildren().add(earth);

         // Lumière simple
         PointLight light = new PointLight(Color.WHITE);
         light.setTranslateZ(-500);
         root.getChildren().add(light);

         // Données : CSV sur le disque (Partie 3 déjà faite)
         world = new World("./data/airport-codes_no_comma.csv");

         // Interactions : zoom drag + clic droit
         scene.addEventHandler(MouseEvent.ANY, e -> {
             if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
                 lastY = e.getSceneY();
             }

             // Zoom au drag (bouton gauche)
             if (e.getEventType() == MouseEvent.MOUSE_DRAGGED && e.getButton() == MouseButton.PRIMARY) {
                 double dy = e.getSceneY() - lastY;
                 lastY = e.getSceneY();
                 camera.setTranslateZ(clamp(camera.getTranslateZ() + dy * 2.0, -2500, -400));
             }

             // Clic droit : pick -> texCoord -> (lon,lat) -> nearest -> sphère rouge
             if (e.getButton() == MouseButton.SECONDARY && e.getEventType() == MouseEvent.MOUSE_CLICKED) {
                 var pick = e.getPickResult();
                 if (pick.getIntersectedNode() != null) {
                     Point2D tex = pick.getIntersectedTexCoord();
                     if (tex != null) {
                         double[] lonlat = Earth.texToLonLat(tex);
                         Aeroport nearest = world.findNearestAirport(lonlat[0], lonlat[1]);
                         if (nearest != null) {
                             System.out.println("Click@ lon=" + lonlat[0] + ", lat=" + lonlat[1]
                                     + " -> nearest = " + nearest);
                             earth.displayRedSphere(nearest);
                         }
                     }
                 }
             }
         });

         primaryStage.show();
     }
     private static double clamp(double v, double min, double max) {
         return Math.max(min, Math.min(max, v));
     }

    public static void main(String[] args) {
        launch(args);
    }
}
*/
public class Interface extends Application {
    private Earth earth;
    private World world;
    private JsonFlightFiller filler;
    private double lastY;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Catch me if you can – Partie 6");
        Group root = new Group();
        Scene scene = new Scene(root, 1200, 800, true);
        stage.setScene(scene);

        // Caméra
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        camera.setNearClip(0.1);
        camera.setFarClip(5000.0);
        camera.setFieldOfView(35);
        scene.setCamera(camera);

        // Terre + lumière
        earth = new Earth();
        root.getChildren().add(earth);
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateZ(-500);
        root.getChildren().add(light);

        // Données
        world = new World("./data/airport-codes_no_comma.csv");
        filler = new JsonFlightFiller(world);

        // Interactions
        scene.addEventHandler(MouseEvent.ANY, e -> {
            if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
                lastY = e.getSceneY();
            }
            if (e.getEventType() == MouseEvent.MOUSE_DRAGGED && e.getButton() == MouseButton.PRIMARY) {
                double dy = e.getSceneY() - lastY;
                lastY = e.getSceneY();
                camera.setTranslateZ(clamp(camera.getTranslateZ() + dy * 2.0, -2500, -400));
            }
            if (e.getEventType() == MouseEvent.MOUSE_CLICKED && e.getButton() == MouseButton.SECONDARY) {
                var pick = e.getPickResult();
                if (pick != null && pick.getIntersectedNode() != null) {
                    Point2D tex = pick.getIntersectedTexCoord();
                    if (tex != null) {
                        double[] lonlat = Earth.texToLonLat(tex);
                        Aeroport nearest = world.findNearestAirport(lonlat[0], lonlat[1]);
                        if (nearest != null) {
                            System.out.println("Click lon=" + lonlat[0] + " lat=" + lonlat[1] + " -> " + nearest);
                            // Marqueur rouge (aéroport cliqué le plus proche)
                            earth.displayRedSphere(nearest);
                            // Origines en JAUNE via JSON
                            for (Aeroport origin : filler.distinctOriginsArrivingTo(nearest.getIata())) {
                                earth.displayYellowSphere(origin);
                            }
                        }
                    }
                }
            }
        });

        stage.show();
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public static void main(String[] args) { launch(args); }
}



