package oop.ex6.validator;

import oop.ex6.line.Line;
import oop.ex6.tables.MethodTable;
import oop.ex6.tables.VariableTable;

/**
 * StateMachine for Comment line; a comment line is always legal.
 * @author Michal and Alon
 *
 */
class CommentStateMachine implements StateMachine {

	/* (non-Javadoc)
	 * Comment line is always syntactically valids
	 * @see oop.ex6.validator.StateMachine#isLineSyntaxValid(oop.ex6.line.Line)
	 */
	@Override
	public void isLineSyntaxValid(Line line) throws SyntaxException {
		return;
	}

	/* (non-Javadoc)
	 * Comment line is always semantically valid.
	 * @see oop.ex6.validator.StateMachine#isLineSemanticsValid(oop.ex6.line.Line, 
	 * oop.ex6.tables.VariableTable, oop.ex6.tables.MethodTable)
	 */
	@Override
	public void isLineSemanticsValid(Line line, VariableTable varTable,
			MethodTable methodTable) throws SemanticsException {
		return;
		
	}

}
