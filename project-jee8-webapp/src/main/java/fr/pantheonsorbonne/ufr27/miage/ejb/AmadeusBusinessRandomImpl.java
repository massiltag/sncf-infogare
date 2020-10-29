package fr.pantheonsorbonne.ufr27.miage.ejb;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

import fr.pantheonsorbonne.ufr27.miage.vo.ANFlightDTO;


public class AmadeusBusinessRandomImpl implements AmadeusBusiness {

	Random ran = new Random();

	@Override
	public Collection<ANFlightDTO> findFlights(String departure, String arrival, Date date) {

		Collection<ANFlightDTO> res = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			res.add(ANFlightDTO.getInstance());
		}

		return res;

	}

	@Override
	public BigDecimal priceSegment(String flightNum, Date date, String klass, int passCount) {

		return new BigDecimal(100 + (ran.nextDouble() * 1000) % 500).setScale(2, RoundingMode.CEILING);

	}

	@Override
	public boolean available(String departure, String arrival, Date date, String klass, int nbSeat) {
		return ran.nextDouble()<0.90;
	}

}
