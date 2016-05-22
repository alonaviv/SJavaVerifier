package oop.ex6.validator;

import java.util.ArrayList;
import java.util.List;

import oop.ex6.line.Line;
import oop.ex6.line.Tokenizer.Token;
import oop.ex6.line.TokenType;
import oop.ex6.tables.InvalidTableAccessException;
import oop.ex6.tables.MethodTable;
import oop.ex6.tables.TableAndScopeMaker;
import oop.ex6.tables.VariableTable;
import oop.ex6.tables.Variable.VarType;

/**
 * @author Michal and Alon
 *
 */
class MethodCallStateMachine implements StateMachine {

	private static final int BEGINNING_OF_ARG_LIST = 1;
	private static final int NUM_OF_TOKENS_AFTER_ARG_LIST = 2;

	/**
	 * Enum listing all the possible states in a valid final variable line.
	 * 
	 * @author Michal and Alon
	 *
	 */
	private static enum State {
		METHOD_NAME, OPEN_PARENTHESIS, VAR_NAME_OR_VAL, COMMA, CLOSE_PARENTHESIS, SEMICOLON;
	}

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
		State currentState = State.METHOD_NAME;
		for (int i = 1; i < tokenList.size(); ++i) {
			currentState = getNextState(currentState, line.getTokenList()
					.get(i));
		}
	}

	/*
	 * (non-Javadoc) Checks that semantics of line is legal: Method cannot be
	 * called from global scope; method must have been defined; varTypes of
	 * variables called with method must match method signature as defined at
	 * declaration, variables must exist in legal scope and must have values
	 * assigned before reference (or in global scope).
	 * 
	 * @see
	 * oop.ex6.validator.StateMachine#isLineSemanticsValid(oop.ex6.line.Line,
	 * oop.ex6.tables.VariableTable, oop.ex6.tables.MethodTable)
	 */
	@Override
	public void isLineSemanticsValid(Line line, VariableTable varTable,
			MethodTable methodTable) throws SemanticsException,
			InvalidTableAccessException, SyntaxException {
		// Checking legal scope:
		if (line.getScope().equals(TableAndScopeMaker.GLOBAL_SCOPE)) {
			throw new SemanticsException(
					"Attempt to call method from global scope");
		}
		// Checking that method exists and parameter list matches.
		Token method = line.getTokenList().get(0);
		List<VarType> paramList = new ArrayList<VarType>();
		for (int i = BEGINNING_OF_ARG_LIST; i < line.getTokenList().size()
				- NUM_OF_TOKENS_AFTER_ARG_LIST; ++i) {
			Token argument = line.getTokenList().get(i);
			if (!argument.getTokenType().equals(TokenType.COMMA)) {
				// getVarTypeFromToken already checks whether a variable has
				// been
				// initialized in a legal line!
				paramList.add(SemanticsValidator.getVarTypeFromToken(argument,
						varTable, line));
			}
		}

		methodTable.verifyMethod(method.getName(), paramList);

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
		case METHOD_NAME:
			if (token.getTokenType() == TokenType.VARNAME) {
				return State.VAR_NAME_OR_VAL;
			}
			if (TokenType.isVarValue(token.getTokenType())) {
				return State.VAR_NAME_OR_VAL;
			}

			if (token.getTokenType() == TokenType.CLOSINGPARENTHESIS) {
				return State.CLOSE_PARENTHESIS;
			}
			throw new SyntaxException("Expected variable name");

		case VAR_NAME_OR_VAL:
			if (token.getTokenType() == TokenType.COMMA) {
				return State.COMMA;
			}

			if (token.getTokenType() == TokenType.CLOSINGPARENTHESIS) {
				return State.CLOSE_PARENTHESIS;
			}
			throw new SyntaxException("Unexpected token");

		case COMMA:
			if (token.getTokenType() == TokenType.VARNAME) {
				return State.VAR_NAME_OR_VAL;
			}
			if (TokenType.isVarValue(token.getTokenType())) {
				return State.VAR_NAME_OR_VAL;
			}

			throw new SyntaxException("Expected variable name");

		case CLOSE_PARENTHESIS:
			if (token.getTokenType() == TokenType.SEMICOLON) {
				return State.SEMICOLON;
			}

			throw new SyntaxException("Expected semicolon");

		case SEMICOLON:
			throw new SyntaxException("Nothing should appear after semicolon");

		default:
			throw new SyntaxException("Unknown state");
		}
	}
}
