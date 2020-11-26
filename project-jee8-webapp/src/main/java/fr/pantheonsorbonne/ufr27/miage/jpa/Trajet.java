package fr.pantheonsorbonne.ufr27.miage.jpa;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity
public class Trajet {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	@JoinTable
	@OneToMany(cascade = CascadeType.ALL)
	List<Desserte> desserteReelles;
	@JoinTable
	@OneToMany(cascade = CascadeType.ALL)
	List<Desserte> desserteTheoriques;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Desserte> getDesserteReelles() {
		return desserteReelles;
	}

	public void setDesserteReelles(List<Desserte> desserteReelles) {
		this.desserteReelles = desserteReelles;
	}

	public List<Desserte> getDesserteTheoriques() {
		return desserteTheoriques;
	}

	public void setDesserteTheoriques(List<Desserte> desserteTheoriques) {
		this.desserteTheoriques = desserteTheoriques;
	}

	@Override
	public String toString() {
		return "Trajet [id=" + id + ", desserteReelles=" + desserteReelles + ", desserteTheoriques="
				+ desserteTheoriques + ", toString()=" + super.toString() + "]";
	}

}
