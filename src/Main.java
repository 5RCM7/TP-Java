public class Main {
    public static void main(String[] args) {
        World w = new World("./data/airport-codes_no_comma.csv");
        System.out.println("Found " + w.getList().size() + " airports.");

        Aeroport parisNearest = w.findNearestAirport(2.316, 48.866);
        System.out.println("Nearest to Paris = " + parisNearest);

        Aeroport cdg = w.findByCode("CDG");
        double dParisNearest = w.distance(2.316, 48.866, parisNearest.getLongitude(), parisNearest.getLatitude());
        double dParisCDG     = w.distance(2.316, 48.866, cdg.getLongitude(), cdg.getLatitude());
        System.out.println("Norme(Paris->Nearest) = " + dParisNearest);
        System.out.println("CDG = " + cdg);
        System.out.println("Norme(Paris->CDG) = " + dParisCDG);
    }
}


