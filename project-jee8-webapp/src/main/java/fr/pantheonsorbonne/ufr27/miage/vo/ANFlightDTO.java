package fr.pantheonsorbonne.ufr27.miage.vo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

public class ANFlightDTO {

	private final static Random rand = new Random();

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Collection<TarifsDTO> getTarifs() {
		return tarifs;
	}

	public void setTarifs(Collection<TarifsDTO> tarifs) {
		this.tarifs = tarifs;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public Date getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public Duration getFlightDuration() {
		return flightDuration;
	}

	public void setFlightDuration(Duration flightDuration) {
		this.flightDuration = flightDuration;
	}

	String company;
	String id;
	Collection<TarifsDTO> tarifs = new ArrayList<>();
	String departure;
	String arrival;
	Date departureTime;
	Date arrivalTime;
	Duration flightDuration;

	private static String getRandomString(int size) {

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			builder.append((char) ('A' + rand.nextInt(25)));
		}

		return builder.toString();

	}

	public static ANFlightDTO getInstance() {
		ANFlightDTO dto = new ANFlightDTO();

		dto.company = getRandomString(2);
		dto.id = "" + rand.nextInt(999);
		for (int i = 0; i < rand.nextInt(4); i++) {
			dto.tarifs.add(TarifsDTO.getInstance());
		}
		dto.departure = getRandomString(3);
		dto.arrival = getRandomString(3);
		dto.departureTime = new Date(System.currentTimeMillis() + 1000 * 3600 * 24 * 30);
		dto.arrivalTime = new Date(System.currentTimeMillis() + 1000 * 3600 * 24 * 30 + 1000 * 3600 * 2);
		dto.flightDuration = Duration.ofHours(2);

		return dto;

	}

}
