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
 * StateMachine for variable declaration line. Includes methods to check whether
 * line is syntactically and semantically valid.
 * 
 * @author Michal and Alon
 *
 */
class VarDeclarationStateMachine implements StateMachine {

	/**
	 * Enum listing all the possible states in a valid variable declaration
	 * line.
	 * 
	 * @author Michal and Alon
	 *
	 */
	private static enum State {
		VAR_TYPE, VAR_VAL, EQUALS, VAR_NAME, COMMA, SEMICOLON;
	}

	private static final int FINAL_TYPE_DECLARATION_INDEX = 1;
	private static final int NON_FINAL_TYPE_DEC_INDEX = 0;

	/*
	 * (non-Javadoc) Runs through all tokens in line's tokenList, and ensures
	 * that they match a legal state. Also checks that line ends in semicolon.
	 * 
	 * @see oop.ex6.validator.StateMachine#isLineSyntaxValid(oop.ex6.line.Line)
	 */
	public void isLineSyntaxValid(Line line) throws SyntaxException {
		List<Token> tokenList = line.getTokenList();
		if (tokenList.get(tokenList.size() - 1).getTokenType() != TokenType.SEMICOLON) {
			throw new SyntaxException("Doesn't end in semicolon");
		}

		State currentState = State.VAR_TYPE;
		for (int i = 1; i < tokenList.size(); ++i) {
			currentState = getNextState(currentState, line.getTokenList()
					.get(i));
		}
	}

	/*
	 * (non-Javadoc) Checks valid syntax for final or regular variable
	 * declaration. Checks that the assignment of a variable is legal (whether
	 * literal or by reference to another variable). Updates assigned variable's
	 * lineInitialized field, if assignment was legal.
	 * 
	 * @see oop.ex6.validator.StateMachine#isLineSemanticsValid
	 * (oop.ex6.line.Line, oop.ex6.tables.VariableTable,
	 * oop.ex6.tables.MethodTable)
	 */
	@Override
	public void isLineSemanticsValid(Line line, VariableTable varTable,
			MethodTable methodTable) throws SemanticsException,
			SyntaxException, InvalidTableAccessException {
		List<Token> tokenList = line.getTokenList();
		State currentState = State.VAR_TYPE;
		int typeDecIndex;
		// Checks whether line is a final or regular declaration line:
		if (line.getLineType().equals(Line.LineType.FINAL_VAR_DECLARATION)) {
			typeDecIndex = FINAL_TYPE_DECLARATION_INDEX;
		} else {
			typeDecIndex = NON_FINAL_TYPE_DEC_INDEX;
		}
		// Saves the token that indicates the type of the new var being
		// declared:
		Token varTypeToken = tokenList.get(typeDecIndex);
		VarType newVarType = SemanticsValidator
				.getVarTypeFromToken(varTypeToken, varTable, line);
		// Iterates over all tokens following the varType token,
		// If token is a value being assigned, checks that assignment is legal.
		for (int i = typeDecIndex + 1; i < tokenList.size(); ++i) {
			Token token = line.getTokenList().get(i);
			currentState = getNextState(currentState, token);
			if (currentState.equals(State.VAR_VAL)) {
				if (TokenType.isVarValue(token.getTokenType())) {

					VarType valType = SemanticsValidator.getVarTypeFromToken(
							token, varTable, line);
					if (!VarType.doVarTypesMatch(newVarType, valType)) {
						throw new SemanticsException(
								"Value doesn't match variable type");
					}
				} else {
					Variable rightVar = varTable.getVariableInLegalScope(
							token.getName(), line.getScope());
					if (!VarType.doVarTypesMatch(newVarType,
							rightVar.getType())) {
						throw new SemanticsException(
								"New Variable type doesn't match referenced "
										+ "variable type");
					}
					if (SemanticsValidator.wasVarInitializedInBadLine(rightVar,
							line)) {
						throw new SemanticsException(
								"Variable wasn't initialized");
					}
				}
			}
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
		case VAR_TYPE:
			if (token.getTokenType() == TokenType.VARNAME) {
				return State.VAR_NAME;
			}
			throw new SyntaxException("No var name after type");

		case VAR_NAME:
			if (token.getTokenType() == TokenType.EQUALS) {
				return State.EQUALS;
			}

			if (token.getTokenType() == TokenType.COMMA) {
				return State.COMMA;
			}

			if (token.getTokenType() == TokenType.SEMICOLON) {
				return State.SEMICOLON;
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
			throw new SyntaxException("Expected variable value");

		case VAR_VAL:
			if (token.getTokenType() == TokenType.COMMA) {
				return State.COMMA;
			}
			if (token.getTokenType() == TokenType.SEMICOLON) {
				return State.SEMICOLON;
			}
			throw new SyntaxException("Unexpected token");

		case COMMA:
			if (token.getTokenType() == TokenType.VARNAME) {
				return State.VAR_NAME;
			}

			throw new SyntaxException("Expected variable name");

		case SEMICOLON:
			throw new SyntaxException("Nothing should appear after semicolon");
		default:
			throw new SyntaxException("Unknown state");
		}
	}

}
