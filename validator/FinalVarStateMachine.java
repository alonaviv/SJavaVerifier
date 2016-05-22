package oop.ex6.validator;

import java.util.List;

import oop.ex6.line.Line;
import oop.ex6.line.Tokenizer.Token;
import oop.ex6.line.TokenType;
import oop.ex6.tables.InvalidTableAccessException;
import oop.ex6.tables.MethodTable;
import oop.ex6.tables.VariableTable;

/**
 * StateMachine for final variable declaration line. Does not include
 * implementation for semantics checker, as the semantics of a final line
 * are checked by the semantic checker in regular variable declaration line.
 * @author Michal and Alon
 *
 */
class FinalVarStateMachine implements StateMachine {

	/**
	 * Enum listing all the possible states in a valid final variable line.
	 * @author Michal and Alon
	 *
	 */
	private static enum State {
		VAR_TYPE, VAR_VAL, EQUALS, FINAL_KEYWORD, VAR_NAME, COMMA, SEMICOLON;
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
		State currentState = State.FINAL_KEYWORD;
		for (int i = 1; i < tokenList.size(); ++i) {
			currentState = getNextState(currentState, line.getTokenList()
					.get(i));
		}
	}

	/* (non-Javadoc)
	 * Is not implemented; semanticsValidation for final var line done from
	 * regular variable declaration line.
	 * @see oop.ex6.validator.StateMachine#isLineSemanticsValid(oop.ex6.line.Line,
	 *  oop.ex6.tables.VariableTable, oop.ex6.tables.MethodTable)
	 */
	@Override
	public void isLineSemanticsValid(Line line, VariableTable varTable,
			MethodTable methodTable) throws SemanticsException, SyntaxException,
			                                                 InvalidTableAccessException {

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
			throws SyntaxException{
		switch (currentState) {
		case FINAL_KEYWORD:
			if (TokenType.isVarType(token.getTokenType())) {
				return State.VAR_TYPE;
			}
			throw new SyntaxException("Phrase after final declaration must be a variable type");
		case VAR_TYPE:
			if (token.getTokenType() == TokenType.VARNAME) {
				return State.VAR_NAME;
			}
			throw new SyntaxException("No variable name after type");

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
			throw new SyntaxException("Expected varname");

		case SEMICOLON:
			throw new SyntaxException("Nothing should appear after semicolon");
		default:
			throw new SyntaxException("Unknown state");
		}
	}
}
