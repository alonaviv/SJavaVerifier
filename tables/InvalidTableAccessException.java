package oop.ex6.tables;

/**
 * An exception thrown when there is an invalid access to a table in the table package
 * 
 * @author alonav11
 *
 */
public class InvalidTableAccessException extends Exception{
	private static final long serialVersionUID = 1L;	
	
	public InvalidTableAccessException(String message){
		super(message);
	}

}
