package fr.pantheonsorbonne.ufr27.miage.model.jaxb;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

	private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public String marshal(LocalDateTime dateTime) {
		return dateTime.format(dateFormat);
	}

	@Override
	public LocalDateTime unmarshal(String dateTime) {
		return LocalDateTime.parse(dateTime, dateFormat);
	}

}