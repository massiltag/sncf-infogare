package fr.pantheonsorbonne.ufr27.miage.jpa;

import lombok.*;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Builder
@Entity
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Trajet {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "trajet")
	List<DesserteReelle> desserteReelles;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "trajet")
	List<DesserteTheorique> desserteTheoriques;

	public Trajet() {

	}


	public void addDesserteReelle(DesserteReelle desserteReelle) {
		int seq = 0;
		if (desserteReelles.isEmpty()) seq = 1;
		else {
			seq = desserteReelles.get(desserteReelles.size() - 1).getSeq() + 1;
		}
		desserteReelle.setSeq(seq);
		desserteReelles.add(desserteReelle);
		desserteReelle.setTrajet(this);
	}

	public void addDesserteTheorique(DesserteTheorique desserteTheorique) {
		int seq = 0;
		if (desserteTheoriques.isEmpty()) seq = 1;
		else {
			seq = desserteTheoriques.get(desserteTheoriques.size() - 1).getSeq() + 1;
		}
		desserteTheorique.setSeq(seq);
		desserteTheoriques.add(desserteTheorique);
		desserteTheorique.setTrajet(this);
	}


}
