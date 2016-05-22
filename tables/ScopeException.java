package oop.ex6.tables;

/**
 * An exception thrown when there are unbalanced brackets.
 * @author alonav11
 *
 */
public class ScopeException extends Exception{
	private static final long serialVersionUID = 1L;	
	
	public ScopeException(String message){
		super(message);
	}

}
