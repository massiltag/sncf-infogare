package fr.pantheonsorbonne.ufr27.miage.exception;

public class UserHasDebtException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2390474645106504444L;
	double debt;
	int userId;

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public double getDebt() {
		return debt;
	}

	public void setDebt(double debt) {
		this.debt = debt;
	}

	public UserHasDebtException(double amount,int userId) {
		super("you owe us " + amount);
		this.debt = amount;
		this.userId=userId;
	}

	public int getUserId() {
		return userId;
	}

}
