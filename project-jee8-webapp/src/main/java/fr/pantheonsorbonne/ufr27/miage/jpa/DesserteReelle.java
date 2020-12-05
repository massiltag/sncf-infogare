package fr.pantheonsorbonne.ufr27.miage.jpa;

import fr.pantheonsorbonne.ufr27.miage.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
@Builder
@AllArgsConstructor
@Entity
@Getter
@Setter
public class DesserteReelle {

    public DesserteReelle() {}

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

    public void addDuration(Duration duration) {
        LocalDateTime ldt = DateUtil.dateToLocalDateTime(this.arrivee).plus(duration);
        this.setArrivee(DateUtil.localDateTimeToDate(ldt));
    }

    public void substractDuration(Duration duration) {
        LocalDateTime ldt = DateUtil.dateToLocalDateTime(this.arrivee).minus(duration);
        this.setArrivee(DateUtil.localDateTimeToDate(ldt));
    }
}
