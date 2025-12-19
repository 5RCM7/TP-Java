public class Flight {
    private final Aeroport origin;
    private final Aeroport destination;
    private final String airline;
    private final String number;

    public Flight(Aeroport origin, Aeroport destination, String airline, String number) {
        this.origin = origin;
        this.destination = destination;
        this.airline = airline;
        this.number = number;
    }

    public Aeroport getOrigin() { return origin; }
    public Aeroport getDestination() { return destination; }
    public String getAirline() { return airline; }
    public String getNumber() { return number; }

    @Override
    public String toString() {
        return airline + " " + number + " : " + origin.getIata() + " -> " + destination.getIata();
    }
}
