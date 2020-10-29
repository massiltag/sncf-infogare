package fr.pantheonsorbonne.ufr27.miage.jpa;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Contract {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	public int getId() {
		return id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEnDate() {
		return enDate;
	}

	public void setEnDate(Date enDate) {
		this.enDate = enDate;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Set<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(Set<Invoice> invoices) {
		this.invoices = invoices;
	}

	public Set<Card> getCards() {
		return cards;
	}

	public void setCards(Set<Card> cards) {
		this.cards = cards;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "contract")
	Set<Invoice> invoices;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "contract")
	Set<Card> cards;

	@ManyToOne(cascade = CascadeType.ALL)
	Customer customer;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	double monthlyFare;

	@Temporal(TemporalType.TIMESTAMP)
	Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	Date enDate;

	public double getMonthlyFare() {
		return monthlyFare;
	}

	public void setMonthlyFare(double monthlyFare) {
		this.monthlyFare = monthlyFare;
	}

}
