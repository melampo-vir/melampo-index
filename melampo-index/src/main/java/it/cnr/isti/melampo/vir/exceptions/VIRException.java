package it.cnr.isti.melampo.vir.exceptions;

public class VIRException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -561011311568413781L;
	public static final String MESSAGE_WRONG_ID = "Cannot prepare the search by ID query! Wrong ID";
	public static final String MESSAGE_IO_PROBLEM = "Unexpected exception occured when querying the image index! IO Problem";
	public static final String MESSAGE_OPEN_INDICES = "Unexpected exception occured when opening the image indices! IO Problem";
	public static final String MESSAGE_FEATURE_COLLECTION = "Unexpected exception occured when querying the feature collection archive!";
	
	
	public VIRException(Exception e) {
		super(e);
	}
	
	public VIRException(String message) {
		super(message);
	}

	public VIRException(String message, Throwable th) {
		super(message, th);
	}
}
