package fr.pantheonsorbonne.ufr27.miage.jpa;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Correspondance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @ManyToOne
    Trajet trajet;

    @OneToOne
    Passager passager;

    @ManyToOne
    Gare gare;

    boolean rupture;

    Date newDate;

}
