package oop.ex6.validator;

import java.util.List;

import oop.ex6.line.Line;
import oop.ex6.line.Tokenizer.Token;
import oop.ex6.line.TokenType;
import oop.ex6.tables.InvalidTableAccessException;
import oop.ex6.tables.MethodTable;
import oop.ex6.tables.Variable;
import oop.ex6.tables.VariableTable;
import oop.ex6.tables.Variable.VarType;

/**
 * StateMachine for variable assignment line. Includes methods to check 
 * whether line is syntactically and semantically valid.
 * @author Michal and Alon
 *
 */
class VarAssignmentStateMachine implements StateMachine {

	private static final int INDEX_OF_LEFT_VAR = 0;
	private static final int INDEX_OF_RIGHT_VAR = 2;
	
	/**
	 * Enum listing all the possible states in a valid var assignment line.
	 * @author Michal and Alon
	 *
	 */
	private static enum State {
		VAR_VAL, EQUALS, VAR_NAME, SEMICOLON;
	}
	
	/* (non-Javadoc)
	 * Runs through all tokens in line's tokenList, and ensures that they
	 * match a legal state. Also checks that line ends in semicolon.
	 * @see oop.ex6.validator.StateMachine#isLineSyntaxValid(oop.ex6.line.Line)
	 */
	public void isLineSyntaxValid(Line line) throws SyntaxException {
		List<Token> tokenList = line.getTokenList();
		if (tokenList.get(tokenList.size() - 1).getTokenType() != TokenType.SEMICOLON) {
			throw new SyntaxException("Doesn't end in semicolon");
		}
		State currentState = State.VAR_NAME;
		for (int i = 1; i < tokenList.size(); ++i) {
			currentState = getNextState(currentState, line.getTokenList()
					.get(i));
		}
	}

	/* (non-Javadoc)
	 * Checks valid syntax for variable assignment.
	 * Checks that the assignment of a variable is legal (whether literal or by
	 * reference to another variable). Updates assigned variable's lineInitialized
	 * field, if assignment was legal and has previously been -1.
	 * @see oop.ex6.validator.StateMachine#isLineSemanticsValid(oop.ex6.line.Line,
	 *  oop.ex6.tables.VariableTable, oop.ex6.tables.MethodTable)
	 */
	@Override
	public void isLineSemanticsValid(Line line, VariableTable varTable,
			MethodTable methodTable) throws SemanticsException,
			InvalidTableAccessException, SyntaxException {
		List<Token> tokenList = line.getTokenList();
		// leftVar is the variable name that appears left of equals sign:
		Variable leftVar = varTable.getVariableInLegalScope(
				tokenList.get(INDEX_OF_LEFT_VAR).getName(), line.getScope());
		if (leftVar.isFinal()) {
			throw new SemanticsException("trying to assign value to final var");
		}
		VarType leftVarType = leftVar.getType();
		Token rightVarToken = tokenList.get(INDEX_OF_RIGHT_VAR);
		if (TokenType.isVarValue(rightVarToken.getTokenType())) {
			VarType valType = SemanticsValidator.getVarTypeFromToken(
					rightVarToken, varTable, line);
			if (!VarType.doVarTypesMatch(leftVarType, valType)) {
				throw new SemanticsException(
						"Value doesn't match variable type");
			}
		} else {
		Variable rightVar = varTable.getVariableInLegalScope(
				rightVarToken.getName(), line.getScope());
		if (!SemanticsValidator.wasVarInitializedInBadLine(rightVar,
				line)) {
			throw new SemanticsException("Variable has not been initialized");
		}
		}
		VarType rightVarType = SemanticsValidator.getVarTypeFromToken(
				rightVarToken, varTable, line);
		if (!VarType.doVarTypesMatch(leftVarType, rightVarType)) {
			throw new SemanticsException("Variable types do not match");
		}

	}
	
	/**
	 * Receives a current state and the following token, and determines whether
	 * the token signifies a step a next legal state; returns new current state,
	 * or throws exception if token does not signify legal state.
	 * 
	 * @param currentState
	 * @param token
	 * @return
	 * @throws SyntaxException
	 */

	private State getNextState(State currentState, Token token)
			throws SyntaxException {
		switch (currentState) {
		case VAR_NAME:
			if (token.getTokenType() == TokenType.EQUALS) {
				return State.EQUALS;
			}
			throw new SyntaxException("Expected equals sign");

		case EQUALS:
			if (TokenType.isVarValue(token.getTokenType())
					|| token.getTokenType() == TokenType.VARNAME) {
				return State.VAR_VAL;
			}

			if (token.getTokenType() == TokenType.VARNAME) {
				return State.VAR_VAL;
			}
			throw new SyntaxException("Expected var val");

		case VAR_VAL:

			if (token.getTokenType() == TokenType.SEMICOLON) {
				return State.SEMICOLON;
			}
			throw new SyntaxException("Syntax: unexpected token");

		case SEMICOLON:
			throw new SyntaxException("Nothing should appear after semicolon");
		default:
			throw new SyntaxException("Unknown state");
		}
	}
}
