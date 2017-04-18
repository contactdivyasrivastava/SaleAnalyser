package com.saleanalyser.exception;

/**
 * @author Divya
 *
 */
public class InvalidMessageException extends RuntimeException {

	
	/**
	 * 
	 */
	public InvalidMessageException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param msg
	 */
	public InvalidMessageException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public InvalidMessageException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public InvalidMessageException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}



	@Override
    public String toString() {
          return "Exception Occured :: " + super.toString() + "::"
		+ getMessage();

    }

    @Override
    public String getMessage() {
        return "Exception Occured :: " +super.getMessage();
    }
}
