package fr.pantheonsorbonne.ufr27.miage.jpa;

import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Trajet {

	@Id
	int id;

	/**
	 * idParcours : set à déterminer si deux trains ont le même parcours.
	 */
	int parcoursId;

	String name;

	String type;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "trajet")
	List<DesserteReelle> desserteReelles;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "trajet")
	List<DesserteTheorique> desserteTheoriques;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "trajet")
	List<Passager> passagers;

	public Trajet() {

	}

	public void setPassagers(List<Passager> passagers) {
		this.passagers = passagers;
		for (Passager passager : passagers) {
			passager.setTrajet(this);
		}
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

	public Date getArriveeTheoriqueToGare(int gareId) {
		return this.getDesserteTheoriqueOfGare(gareId).getArrivee();
	}

	public Date getArriveeReelleToGare(int gareId) {
		return this.getDesserteReelleOfGare(gareId).getArrivee();
	}

	public DesserteTheorique getDesserteTheoriqueOfGare(int gareId) {
		return this.getDesserteTheoriques().stream()
				.filter(d -> d.getGare().getId() == gareId)
				.findFirst()
				.orElse(null);
	}

	public DesserteReelle getDesserteReelleOfGare(int gareId) {
		return this.getDesserteReelles().stream()
				.filter(d -> d.getGare().getId() == gareId)
				.findFirst()
				.orElse(null);
	}

	public DesserteReelle getDesserteReelleNo(int seq) {
		return this.getDesserteReelles().stream()
				.filter(gare -> gare.getSeq() == seq)
				.findFirst()
				.orElse(null);
	}

	public DesserteReelle getNextDesservieAfter(int seq) {
		return this.getDesserteReelles().stream()
				.filter(gare -> gare.getSeq() > seq)
				.filter(gare -> gare.desservi)
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
