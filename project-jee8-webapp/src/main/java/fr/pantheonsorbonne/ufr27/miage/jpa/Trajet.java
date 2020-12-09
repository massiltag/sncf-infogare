package fr.pantheonsorbonne.ufr27.miage.jpa;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Trajet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	String type;

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

	public DesserteReelle getDesserteReelleNo(int seq) {
		return this.getDesserteReelles().stream()
				.filter(gare -> gare.getSeq() == seq)
				.findFirst()
				.orElse(null);
	}

	public DesserteTheorique getDesserteTheoriqueNo(int seq) {
		return this.getDesserteTheoriques().stream()
				.filter(gare -> gare.getSeq() == seq)
				.findFirst()
				.orElse(null);
	}

	public void addDesserteReelles(List<DesserteReelle> desserteReelles) {
		for (DesserteReelle desserteReelle : desserteReelles)
			addDesserteReelle(desserteReelle);
	}

	public void addDesserteTheoriques(List<DesserteTheorique> desserteTheoriques) {
		for (DesserteTheorique desserteTheorique : desserteTheoriques)
			addDesserteTheorique(desserteTheorique);
	}

}
