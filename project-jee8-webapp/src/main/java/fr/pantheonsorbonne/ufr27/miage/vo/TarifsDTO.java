package fr.pantheonsorbonne.ufr27.miage.vo;

import java.util.Random;

public class TarifsDTO {
	String klass;

	public String getKlass() {
		return klass;
	}

	public void setKlass(String klass) {
		this.klass = klass;
	}

	public enum Klasses {
		J, Y, M
	}

	private final static Random rand = new Random();

	public int getAvailability() {
		return availability;
	}

	public void setAvailability(int availability) {
		this.availability = availability;
	}

	int availability;

	public static TarifsDTO getInstance() {
		TarifsDTO t = new TarifsDTO();
		t.availability = rand.nextInt(50);
		int rnd = rand.nextInt(Klasses.values().length);
		t.klass = Klasses.values()[rnd].name();
		return t;

	}

}
