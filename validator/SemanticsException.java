package oop.ex6.validator;

/**
 * An exception thrown when the file's semantics is invalid
 * @author Alon and Michal
 *
 */
public class SemanticsException extends Exception{
	private static final long serialVersionUID = 1L;	
	
	public SemanticsException(String message){
		super(message);
	}

}
