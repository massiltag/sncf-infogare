package fr.pantheonsorbonne.ufr27.miage.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Address {
	@Override
	public String toString() {
		return "Address [id=" + id + ", streetName=" + streetName + ", streeNumber=" + streeNumber + ", zipCode="
				+ zipCode + ", country=" + country + "]";
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public int getStreeNumber() {
		return streeNumber;
	}
	public void setStreeNumber(int streeNumber) {
		this.streeNumber = streeNumber;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	String streetName;
	int streeNumber;
	String zipCode;
	String country;
}
