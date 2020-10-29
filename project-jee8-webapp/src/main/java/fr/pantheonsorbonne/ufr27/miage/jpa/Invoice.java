package fr.pantheonsorbonne.ufr27.miage.jpa;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Invoice {
	public void setPayed(boolean isPayed) {
		this.isPayed = isPayed;
	}

	@Override
	public String toString() {
		return "Invoice [id=" + id + ", date=" + date + ", contract=" + contract + "]";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	@Temporal(TemporalType.TIMESTAMP)
	Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
	Contract contract;

	private boolean isPayed;

	public boolean isPayed() {
		return this.isPayed;
	}
}
