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

    // Prochain trajet du passager
    @ManyToOne
    Trajet trajet;

    // Passager concerné par cette correspondance
    @OneToOne
    Passager passager;

    @ManyToOne
    Gare gare;

    boolean rupture;

    Date newDate;

}
