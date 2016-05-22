package oop.ex6.validator;

import oop.ex6.line.Line;
import oop.ex6.tables.InvalidTableAccessException;
import oop.ex6.tables.VariableTable;
import oop.ex6.tables.MethodTable;

/**
 * Interface for StateMachine, to be implemented separately for 
 * each line type.
 * @author Alon and Michal
 *
 */
interface StateMachine {

	/**
	 * Checks whether the line follows the legal syntax for the specific
	 * line type: Each state must be following by one of several options, 
	 * as delineated by each specific state machine.
	 * @param line
	 * @throws SyntaxException
	 */
	void isLineSyntaxValid(Line line) throws SyntaxException;

	/**
	 * Checks whether the semantics of each line are legal; if variables
	 * that are referred to exist in a legal scope; if they have values; if
	 * a line is called within a legal scope; whether a called method exists,
	 * and if it's called with the correct arguments, etc.
	 * @param line
	 * @param varTable
	 * @param methodTable
	 * @throws SemanticsException
	 * @throws SyntaxException
	 * @throws InvalidTableAccessException
	 */
	void isLineSemanticsValid(Line line, VariableTable varTable,
			MethodTable methodTable) throws SemanticsException, SyntaxException, 
			                                            InvalidTableAccessException;

}
