package flightassistant;

import structures.BinaryMinHeap;
import utils.Day;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class InfinityDijkstra {

	public static List<Airport> minPath(FlightAssistant fa, Airport origin, Airport dest, Weighter weighter, List<Day> days) {
		return minPath2(fa, origin, dest, weighter, null, days);
	}

	public static List<Airport> minPath2(FlightAssistant fa, Airport origin, Airport dest, Weighter weighter,
									   Weighter originWeighter, List<Day> days) {

		BinaryMinHeap<Airport> pq = queueAirports(fa);

		pq.decreasePriority(origin, 0);

		List<Airport> ans;
		if (originWeighter != null) {
			djistra(pq, dest, originWeighter, days, true);
		}

		ans = djistra(pq, dest, weighter, null, false);

		return ans;
	}

	// Cambiar nombre
	private static List<Airport> djistra(BinaryMinHeap<Airport> pq, Airport dest, Weighter weighter, List<Day> days, boolean isOrigin) {

		while (!pq.isEmpty()) {
			Double minWeight = pq.minWeight();

			if (Double.compare(minWeight, Double.POSITIVE_INFINITY) == 0)
				return new LinkedList<>(); //No existe el camino, no tiene sentido seguir

			Airport airport = pq.dequeue();
			airport.visit();

			if (airport.equals(dest)) {
				return buildList(dest);
			}

			Iterator<Airport> iter = airport.connectedAirportsIterator();
			while (iter.hasNext()) {
				Airport next = iter.next();
				if (!next.visited() && ((isOrigin && airport.flightExistsTo(next, days)) || (!isOrigin && airport.flightExistsTo(next)))) {
					WeightedTicket wTicket = weighter.minTicket(airport, next);

					double nextCurrWeight = pq.getPriority(next);
					double acumWeight = minWeight + wTicket.weight();

					if (acumWeight < nextCurrWeight) {
						next.setIncident(wTicket.ticket());
						pq.decreasePriority(next, acumWeight);
					}
				}
			}
			// Si está en el aeropuerto del origen tiene que ciclar una sola vez, y devuelve null.
			if (isOrigin) return null;
		}
		return new LinkedList<>(); // lista vacia
	}

	private static BinaryMinHeap<Airport> queueAirports(FlightAssistant fa) {
		int size = fa.airports.size();

		BinaryMinHeap<Airport> heap = new BinaryMinHeap<>(size);

		Iterator<Airport> iter = fa.airports.valueIterator();

		while(iter.hasNext()){
			heap.enqueue(iter.next(), Double.POSITIVE_INFINITY);
		}

		return heap;
	}

	// agregando aeropuertos.
	private static List<Airport> buildList(Airport last) {
		LinkedList<Airport> list = new LinkedList<>();

		Airport curr = last;
		Ticket t;
		while((t = curr.getIncident()) != null) {
			list.addFirst(curr);
			curr = t.getOrigin();
		}
		list.addFirst(curr);
		return list;
	}

	// Con tickets
//	private static List<Ticket> buildList(Airport last) {
//		LinkedList<Ticket> list = new LinkedList<>();
//
//		Airport curr = last;
//		Ticket t;
//		while((t = curr.getIncident()) != null) {
//			list.addFirst(t);
//			curr = t.getOrigin();
//		}
//		return list;
//	}

}
