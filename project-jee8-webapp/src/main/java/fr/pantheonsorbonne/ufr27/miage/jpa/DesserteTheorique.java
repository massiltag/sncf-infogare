package fr.pantheonsorbonne.ufr27.miage.jpa;

import lombok.*;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Builder
@Entity
@AllArgsConstructor
@Getter
@Setter
public class DesserteTheorique {

	public DesserteTheorique() {}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	int seq;

	boolean desservi;

	@Temporal(TemporalType.TIMESTAMP)
	Date arrivee;

	@ManyToOne(cascade = CascadeType.PERSIST)
	Gare gare;

	@ManyToOne
	Trajet trajet;

}
