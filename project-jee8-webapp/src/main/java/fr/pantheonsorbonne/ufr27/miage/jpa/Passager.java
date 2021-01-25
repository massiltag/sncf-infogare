package fr.pantheonsorbonne.ufr27.miage.jpa;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Passager {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @ManyToOne
    Trajet trajet;

    @OneToOne(cascade = CascadeType.ALL)
    Correspondance correspondance;

    public void setCorrespondance(Correspondance correspondance) {
        this.correspondance = correspondance;
        correspondance.setPassager(this);
    }
}
