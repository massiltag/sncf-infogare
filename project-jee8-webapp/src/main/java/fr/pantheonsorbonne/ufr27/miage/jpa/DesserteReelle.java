package fr.pantheonsorbonne.ufr27.miage.jpa;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
public class DesserteReelle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    boolean desservi;

    @Temporal(TemporalType.TIMESTAMP)
    Date depart;

    @Temporal(TemporalType.TIMESTAMP)
    Date arrivee;

    @ManyToOne(cascade = CascadeType.PERSIST)
    Gare gare;

    @ManyToOne
    Trajet trajet;

}
