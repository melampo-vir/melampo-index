package it.cnr.isti.melampo.vir.exceptions;

public class VIRException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -561011311568413781L;

	public VIRException(Exception e) {
		super(e);
	}
	
	public VIRException(String message) {
		super(message);
	}

}
