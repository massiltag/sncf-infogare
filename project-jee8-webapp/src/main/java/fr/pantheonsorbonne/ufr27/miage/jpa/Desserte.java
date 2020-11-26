package fr.pantheonsorbonne.ufr27.miage.jpa;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Desserte {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;
	boolean desservi;
	@Temporal(TemporalType.TIMESTAMP)
	Date depart;
	@Temporal(TemporalType.TIMESTAMP)
	Date arrivee;
	@ManyToOne
	Gare gare;
	@ManyToOne(cascade = CascadeType.ALL)
	Trajet trajet;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isDesservi() {
		return desservi;
	}
	public void setDesservi(boolean desservi) {
		this.desservi = desservi;
	}
	public Date getDepart() {
		return depart;
	}
	public void setDepart(Date depart) {
		this.depart = depart;
	}
	public Date getArrivee() {
		return arrivee;
	}
	public void setArrivee(Date arrivee) {
		this.arrivee = arrivee;
	}
	public Gare getGare() {
		return gare;
	}
	public void setGare(Gare gare) {
		this.gare = gare;
	}
	@Override
	public String toString() {
		return "Desserte [id=" + id + ", desservi=" + desservi + ", depart=" + depart + ", arrivee=" + arrivee
				+ ", gare=" + gare + ", toString()=" + super.toString() + "]";
	}
	

}
