package flightassistant;

/**
 * Clase que representa los atributos principales de un {@link Flight}
 *
 */
public class FlightId implements Comparable<FlightId>{


    private String airline;
    private int number;

    public FlightId (String airline, int number) {
        if (airline == null)
            throw new IllegalArgumentException("null airline");
        if (number < 0)
            throw new IllegalArgumentException("Invalid flight number");
        this.airline = airline;
        this.number = number;
    }

    /**
     * Devuelve el nombre de la aerolinea de un vuelo
     * @return nombre de la aerolinea
     */
    public String getAirline () {
        return airline;
    }

    /**
     * Decuelve el numero del vuelo
     * @return numero de vuelo
     */
    public int getNumber () {
        return number;
    }

    @Override public boolean equals (Object other) {
        if (other == this)
            return true;
        if (other == null)
            return false;
        if (!other.getClass().equals(getClass()))
            return false;
        FlightId o = (FlightId) other;
        return airline.equals(o.airline) && number == o.number;
    }

    @Override public int hashCode () {
        int result = airline != null ? airline.hashCode() : 0;
        result = 31 * result + number;
        return result;
    }

    public String toString () {
        return airline + " " + number;
    }

    @Override public int compareTo (FlightId o) {
        int strComp = getAirline().compareTo(o.getAirline());
        return strComp == 0 ? Integer.compare(getNumber(), o.getNumber()) : strComp;
    }
}
