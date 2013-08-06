package it.cnr.isti.melampo.vir.exceptions;

public class BoFException extends VIRException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -561011311568413781L;

	public BoFException(Exception e) {
		super(e);
	}
	
	public BoFException(String message) {
		super(message);
	}

}
