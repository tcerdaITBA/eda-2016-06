package flightassistant;

import utils.Moment;
import utils.Time;

import java.io.Serializable;

public class Flight implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private FlightId id;
	private double price;

	//private Schedule[] schedules;
	private Moment departure;
	private Time duration;

	private Airport origin;
	private Airport destination;

//	private static class Schedule{
//		private Moment departure;
//		private Moment arrival;
//
//		public Schedule(Moment departure, Moment arrival){
//			this.arrival = arrival;
//			this.departure = departure;
//		}
//	}

	public Flight(String airline, int number, double price, Moment departure, Time duration,
			Airport origin, Airport destination) {
		this.id = new FlightId(airline, number);
		this.price = price;
		this.duration = duration;

//		schedules = new Schedule[departures.length];
//		for(int i = 0; i < departures.length ; i++){
//			Moment m = departures[i];
//			schedules[i] = new Schedule(m, m.addTime(duration));
//		}

		this.departure = departure;
		this.origin = origin;
		this.destination = destination;
	}

	public Airport getOrigin() {
		return origin;
	}

	public Time getDepartureTime() {
		return departure.getTime();
	}

	public Moment getDeparture() { return departure; }
//	public Moment[] getDepartureMoments() {
//		Moment[] departures = new Moment[schedules.length];
//		for(int i = 0; i < departures.length; i++)
//			departures[i] = schedules[i].departure;
//		return departures;
//	}

	public Airport getDestination() {
		return destination;
	}

	public boolean isCheaperThan(Flight other) {
		return price < other.price;
	}

	public boolean isQuickerThan(Flight other) {
		return duration.compareTo(other.duration) < 0;
	}

	public Time getDuration() {
		return duration;
	}

	public FlightId getId() {
		return id;
	}

	public double getPrice() {
		return price;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public String toString(){
		return "Flight: " + id.toString() + " Time: " + getDuration().getMinutes();
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;

		Flight other = (Flight) o;
		return id.equals(other.id);
	}

}
