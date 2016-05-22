package oop.ex6.validator;


/**
 * An exception thrown when the file's syntax is invalid
 * @author Alon and Michal
 *
 */
public class SyntaxException extends Exception{
	private static final long serialVersionUID = 1L;	
	
	public SyntaxException(String message){
		super(message);
	}

}
