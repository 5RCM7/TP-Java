package tp.dataflight;

public class Flight {
    private final Aeroport departure;
    private final Aeroport arrival;

    public Flight(Aeroport departure, Aeroport arrival) {
        this.departure = departure;
        this.arrival = arrival;
    }

    public Aeroport getDeparture() { return departure; }
    public Aeroport getArrival() { return arrival; }

    @Override
    public String toString() {
        return "Flight{" +
                "dep=" + (departure != null ? departure.getIata() : "null") +
                ", arr=" + (arrival != null ? arrival.getIata() : "null") +
                '}';
    }
}
