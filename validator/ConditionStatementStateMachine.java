package oop.ex6.validator;

import oop.ex6.line.Line;
import oop.ex6.line.Tokenizer.Token;
import oop.ex6.line.TokenType;
import oop.ex6.tables.InvalidTableAccessException;
import oop.ex6.tables.TableAndScopeMaker;
import oop.ex6.tables.Variable;
import oop.ex6.tables.VariableTable;
import oop.ex6.tables.Variable.VarType;
import oop.ex6.tables.MethodTable;

/**
 * State machine for if/while lines. Contains methods to check whether line is
 * syntactically and semantically valid. 
 * @author Michal and Alon
 *
 */
class ConditionStatementStateMachine implements StateMachine {

	/**
	 * Enum listing all the possible states in a valid condition line.
	 * @author Michal and Alon
	 *
	 */
	private static enum State {
		WHILE_OR_IF, VAR_NAME, ANDBOOL, ORBOOL, BOOLEAN, CLOSE_PARENTHESIS, OPEN_BRACKETS;
	}

	/* (non-Javadoc)
	 * Runs through all tokens in line's tokenList, and ensures that they
	 * match a legal state.
	 * @see oop.ex6.validator.StateMachine#isLineSyntaxValid(oop.ex6.line.Line)
	 */
	public void isLineSyntaxValid(Line line) throws SyntaxException{
		State currentState = State.WHILE_OR_IF;
		for (int i = 1; i < line.getTokenList().size(); ++i) {
			currentState = getNextState(currentState, line.getTokenList()
					.get(i));
		}
	}

	/* (non-Javadoc)
	 * Checks that all the values inside the predicate are booleans (whether
	 * literal boolean/int/double or a variable of those types that has been
	 * assigned a value).
	 * @see oop.ex6.validator.StateMachine#isLineSemanticsValid(oop.ex6.line.Line,
	 *  oop.ex6.tables.VariableTable, oop.ex6.tables.MethodTable)
	 */
	public void isLineSemanticsValid(Line line, VariableTable varTable,
			MethodTable methodTable) throws SemanticsException, SyntaxException, 
			                                              InvalidTableAccessException {
		if (line.getScope().equals(TableAndScopeMaker.GLOBAL_SCOPE)) {
			throw new SemanticsException("");
		}
		State currentState = State.WHILE_OR_IF;
		for (int i = 1; i < line.getTokenList().size(); ++i) {
			Token token = line.getTokenList().get(i);
			currentState = getNextState(currentState, token);
			if (currentState.equals(State.VAR_NAME)) {
				Variable var = varTable.getVariableInLegalScope(
						token.getName(), line.getScope());
				if (!(var.getType().equals(VarType.INT)
						|| var.getType().equals(VarType.BOOLEAN) || var
						.getType().equals(VarType.DOUBLE))) {
					throw new SemanticsException("Invalid Condition within if/while");
				}
				if (SemanticsValidator.wasVarInitializedInBadLine(var, line)) {
					throw new SemanticsException("Variable within if/while wasn't initialized");
				}
			}

		}
	}

	/**
	 * Receives a current state and the following token, and determines
	 * whether the token signifies a step a next legal state; returns new
	 * current state, or throws exception if token does not signify legal state.
	 * @param currentState
	 * @param token
	 * @return
	 * @throws SyntaxException
	 */
	private State getNextState(State currentState, Token token)
			throws SyntaxException {
		switch (currentState) {
		case WHILE_OR_IF:
			if (token.getTokenType().equals(TokenType.VARNAME)) {
				return State.VAR_NAME;
			}

			if (isLegalTypeForCondition(token.getTokenType())) {
				return State.BOOLEAN;
			}

			throw new SyntaxException("Expected variable name");

		case VAR_NAME:
		case BOOLEAN:
			if (token.getTokenType().equals(TokenType.ANDBOOL)) {
				return State.ANDBOOL;
			}

			if (token.getTokenType().equals(TokenType.ORBOOL)) {
				return State.ORBOOL;
			}

			if (token.getTokenType().equals(TokenType.CLOSINGPARENTHESIS)) {
				return State.CLOSE_PARENTHESIS;
			}
			throw new SyntaxException("Unexpected token");

		case ANDBOOL:
			if (token.getTokenType().equals(TokenType.VARNAME)) {
				return State.VAR_NAME;
			}

			if (isLegalTypeForCondition(token.getTokenType())) {
				return State.BOOLEAN;
			}
			
			throw new SyntaxException("Expected variable name");

		case ORBOOL:
			if (token.getTokenType().equals(TokenType.VARNAME)) {
				return State.VAR_NAME;
			}

			if (isLegalTypeForCondition(token.getTokenType())) {
				return State.BOOLEAN;
			}
			
			throw new SyntaxException("Expected variable name");

		case CLOSE_PARENTHESIS:
			if (token.getTokenType().equals(TokenType.OPENINGBRACKET)) {
				return State.OPEN_BRACKETS;
			}

			throw new SyntaxException("Expected semicolon");

		case OPEN_BRACKETS:
			throw new SyntaxException(
					"Nothing should appear after semicolon");

		default:
			throw new SyntaxException("Unknown state");
		}
	}

	private boolean isLegalTypeForCondition(TokenType tokenType) {
		return tokenType.equals(TokenType.BOOLEANVAL)
				|| tokenType.equals(TokenType.INTVAL)
				|| tokenType.equals(TokenType.DOUBLEVAL);
	}
}
