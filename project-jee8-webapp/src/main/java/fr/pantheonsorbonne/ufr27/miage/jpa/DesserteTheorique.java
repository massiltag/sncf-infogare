package fr.pantheonsorbonne.ufr27.miage.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Builder
@Entity
@AllArgsConstructor
@Getter
@Setter
public class DesserteTheorique {

	public DesserteTheorique() {}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
