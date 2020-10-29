package fr.pantheonsorbonne.ufr27.miage.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class HealthData {
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public double getFatRatio() {
		return fatRatio;
	}
	public void setFatRatio(double fatRatio) {
		this.fatRatio = fatRatio;
	}
	public double getMuscleRatio() {
		return muscleRatio;
	}
	public void setMuscleRatio(double muscleRatio) {
		this.muscleRatio = muscleRatio;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	double height;
	double weight;
	double fatRatio;
	double muscleRatio;

}
