package fr.pantheonsorbonne.ufr27.miage.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Entity
@AllArgsConstructor
public class Gare {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	String nom;

	public Gare() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	@Override
	public String toString() {
		return "Gare [id=" + id + ", nom=" + nom + ", toString()=" + super.toString() + "]";
	}

}
