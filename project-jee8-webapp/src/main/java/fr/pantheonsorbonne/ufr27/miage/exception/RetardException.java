package fr.pantheonsorbonne.ufr27.miage.exception;

public class RetardException extends Exception{

	/**
	 * Cette exception se lance si:
	 * 
	 * un TER attend TGV
	 * 
	 * retarder un train si le nombre de passagers ayant reservés en rupture de correspondance est inférieur à 50
	 * 
	 * si au moins l'une des 2 conditions de desserte exceptionnelle n'est pas vérifée
	 */
	private static final long serialVersionUID = 165668898L;


	public String msgTERWaitTGV() {
		return "Un TER ne peut pas attendre un TGV";
	}
	
	public String msgNBPassagers() {
		return "Le nombre de passagers ayant réservés en rupture de correspondance doit etre > 50";
	}
	
	public String msgGareExceptionnelle() {
		return "Ce train ne peut pas desservir cette gare, vérifiez que les conditions soient vérifiées";
	}
}
