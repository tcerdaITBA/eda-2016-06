package flightassistant;

import structures.SimpleHashMap;
import structures.SimpleMap;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class Airport implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private double latitude;
	private double longitude;
	private SimpleMap<Airport, Route> routes;

	public Airport(String id, double latitude, double longitude) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;

		routes = new SimpleHashMap<>(new Comparator<Airport>() {
			@Override
			public int compare(Airport o1, Airport o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
	}

	public String getId() {
		return id;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void addFlight(Flight flight) {
		Airport destination = flight.getDestination();
		if (! routeExistsTo(destination)) { //Un acceso mas, pero queda mas claro
			throw new IllegalStateException();
		}

		Route r = routes.get(destination);
		r.addFlight(flight);
	}

	public boolean routeExistsTo(Airport airport) {
		return routes.containsKey(airport);
	}

	public void addRoute(Airport airport, Route r) {
		routes.put(airport, r);
	}

	public void removeFlight(Flight flight) {
		Route r = routes.get(flight.getDestination());
		if (r == null)
			throw new IllegalArgumentException("No existe ruta hacia el destino del vuelo a eliminar");
		r.removeFlight(flight);
	}

	public void removeRouteTo(Airport destination) {
		routes.remove(destination);
	}

	public Set<Airport> getDestinations() {
		return routes.keySet();
	}

	public Iterator<Airport> getDestinationsIterator() {
		return routes.keyIterator();
	}
	
	
	
	public String toString(){
		return "Airport: " + id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	
	/*
	 * Determina la igualdad de dos Aeropuertos según su nombre (id)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;
		Airport other = (Airport) o;
		return id == null ? other.id == null : id.equals(other.id);
	}


	public boolean flightExistTo(Airport next) {
		return routes.get(next).getCheapestFrom(this) != null;
	}
	
	public Flight getCheapestTo(Airport destination) {
		return routes.get(destination).getCheapestFrom(this);
	}
	
	public Flight getQuickestTo(Airport destination) {
		return routes.get(destination).getQuickestFrom(this);
	}

	
	//Cosas de Dijkstrksja
	
	private boolean visited;
	
	private double weight;
	
	public void visit(){
		visited = true;
	}
	
	public void unvisit(){
		visited = false;
	}
	
	public boolean visited() {
		return visited;
	}	
	
	
	private Flight incident;
	
	public Flight getIncident(){
		return incident;
	}
	
	public void setIncident(Flight f){
		incident = f;
	}

	public double weight(){
		return weight;
	}
	
	public void setWeight(double w){
		weight = w;
	}

}
