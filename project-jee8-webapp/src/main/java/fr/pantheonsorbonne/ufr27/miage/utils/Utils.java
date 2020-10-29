package fr.pantheonsorbonne.ufr27.miage.utils;

import java.text.DateFormatSymbols;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.OptionalInt;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import com.google.common.io.BaseEncoding;

import fr.pantheonsorbonne.ufr27.miage.exception.DateParseException;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ObjectFactory;



public abstract class Utils {
	private static final Random random = new Random(); // or SecureRandom
	
	static {
		try {
			DATATYPE_FACTORY = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			// we are doomed, we cannot create a datatype factory
			throw new RuntimeException(e);
		}
	}

	public static String getRandomString() {
		final byte[] buffer = new byte[5];
		random.nextBytes(buffer);
		return BaseEncoding.base64Url().omitPadding().encode(buffer); // or
																		// base32()
	}

	public static Date parseDate(String strDate, String time) throws DateParseException {
		Date theDate = Utils.parseDateOnly(strDate);
		if (time != null) {
	
			Pattern pattern = Pattern.compile("(\\d{2})(\\d{2})");
			Matcher matcher = pattern.matcher(time);
			if (matcher.matches()) {
				theDate.setHours(Integer.valueOf(matcher.group(1)));
				theDate.setMinutes(Integer.valueOf(matcher.group(1)));
	
			} else {
				throw new DateParseException();
			}
	
		}
	
		return theDate;
	
	}

	public static Date parseDateOnly(String strDate) throws DateParseException {
		try {
	
			Pattern pattern = Pattern.compile("([0-9]{1,2})([A-Z]{3})");
			Matcher matcher = pattern.matcher(strDate);
			if (matcher.matches()) {
	
				String dom = matcher.group(1);
				String moy = matcher.group(2);
				OptionalInt monthIndex = IntStream.range(0, Utils.shortMonthes.length)
						.filter(i -> Utils.shortMonthes[i].substring(0, 3).toLowerCase().equals(moy.toLowerCase())).findFirst();
				if (!monthIndex.isPresent()) {
					throw new DateParseException();
				}
				int day = Integer.valueOf(dom);
	
				Calendar cal = Calendar.getInstance();
				cal.set(cal.get(Calendar.YEAR), monthIndex.getAsInt(), day);
				return cal.getTime();
			}
			throw new DateParseException();
		} catch (Exception e) {
			throw new DateParseException();
		}
	
	}

	public static Duration getDuration(Date arrivalTime, Date departureTime) {
		return Utils.DATATYPE_FACTORY.newDuration(arrivalTime.getTime() - departureTime.getTime());
	}

	public static XMLGregorianCalendar Date2XMLGregorianCalendar(Date arrivalTime) {
		Instant instant = Instant.ofEpochMilli(arrivalTime.getTime());
		ZonedDateTime dateTime = instant.atZone(Utils.ZONE);
		GregorianCalendar c = GregorianCalendar.from(dateTime);
		XMLGregorianCalendar cal = Utils.DATATYPE_FACTORY.newXMLGregorianCalendar(c);
		return cal;
	}

	public static final ObjectFactory FACTORY = new ObjectFactory();
	public static final DatatypeFactory DATATYPE_FACTORY;
	public static final ZoneId ZONE = ZoneId.systemDefault();
	public static final String[] shortMonthes = new DateFormatSymbols().getShortMonths();

}
