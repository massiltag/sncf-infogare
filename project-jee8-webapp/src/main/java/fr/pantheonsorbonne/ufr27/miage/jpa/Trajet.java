package fr.pantheonsorbonne.ufr27.miage.jpa;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
@Setter
@Getter
public class Trajet {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "trajet")
	List<DesserteReelle> desserteReelles;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "trajet")
	List<DesserteTheorique> desserteTheoriques;

	public void addDesserteReelle(DesserteReelle desserteReelle) {
		desserteReelles.add(desserteReelle);
		desserteReelle.setTrajet(this);
	}

	public void addDesserteTheorique(DesserteTheorique desserteTheorique) {
		desserteTheoriques.add(desserteTheorique);
		desserteTheorique.setTrajet(this);
	}

}
