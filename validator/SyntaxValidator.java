package oop.ex6.validator;

import oop.ex6.line.Line;
import oop.ex6.line.Line.LineType;

/**
 * SyntaxValidator receives a single line and checks whether it is 
 * syntactically valid: if there exists a context in which the line
 * is legal, it will validate the line. The only exception to this
 * is in assigning a literal value to a variable: even if it is of the 
 * wrong type, this will be considered a semantic error and not syntactic.
 * @author Alon and Michal
 *
 */
public class SyntaxValidator {

	/**
	 * The main function of the validator, runs over each line of the file and
	 * checks whether it is syntactically valid. The rule of thumb is if there
	 * exists a file in which this line would be legal. Will throw exception if
	 * the stateMachine validation doesn't pass.
	 * @throws SyntaxException 
	 * 
	 */
	public void validate(Line line) throws SyntaxException{
		LineType type = line.getLineType();
		StateMachine stateMachine;
		switch (type) {
		case FINAL_VAR_DECLARATION:
			stateMachine = new FinalVarStateMachine();
			break;
		case VAR_DECLARATION:
			stateMachine = new VarDeclarationStateMachine();
			break;
		case VAR_ASSIGNMENT:
			stateMachine = new VarAssignmentStateMachine();
			break;
		case METHOD_DECLARATION:
			stateMachine = new MethodDeclarationStateMachine();
			break;
		case METHOD_CALL:
			stateMachine = new MethodCallStateMachine();
			break;
		case IF_STATEMENT:
		case WHILE_STATEMENT:
			stateMachine = new ConditionStatementStateMachine();
			break;
		case RETURN:
			stateMachine = new ReturnStateMachine();
			break;
		case CLOSING_BRACKET:
			stateMachine = new ClosingBracketStateMachine(null);
			break;
		case COMMENT:
			stateMachine = new CommentStateMachine();
			break;
		default:
			throw new IllegalArgumentException();
		}

		stateMachine.isLineSyntaxValid(line);
		return;
	}

}
