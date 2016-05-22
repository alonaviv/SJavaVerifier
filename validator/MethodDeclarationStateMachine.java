package oop.ex6.validator;

import java.util.List;

import oop.ex6.line.Line;
import oop.ex6.line.Tokenizer.Token;
import oop.ex6.line.TokenType;
import oop.ex6.tables.MethodTable;
import oop.ex6.tables.TableAndScopeMaker;
import oop.ex6.tables.VariableTable;

/**
 * StateMachine for method declaration line. Includes methods to check 
 * whether line is syntactically and semantically valid.
 * @author Michal and Alon
 *
 */
class MethodDeclarationStateMachine implements StateMachine {

	/**
	 * Enum listing all the possible states in a valid method declaration line.
	 * @author Michal and Alon
	 *
	 */
	
	private static enum State {
		VOID, METHOD_NAME, FINAL, VAR_TYPE, VAR_NAME, COMMA,
		CLOSE_PARENTHESIS, OPEN_BRACKETS;
	}
	
	/* (non-Javadoc)
	 * Runs through all tokens in line's tokenList, and ensures that they
	 * match a legal state. Also checks that line ends in opening bracket.
	 * @see oop.ex6.validator.StateMachine#isLineSyntaxValid(oop.ex6.line.Line)
	 */
	public void isLineSyntaxValid(Line line) throws SyntaxException {
		List<Token> tokenList = line.getTokenList();
		if (tokenList.get(tokenList.size() - 1).getTokenType()
				!= TokenType.OPENINGBRACKET) {
			throw new SyntaxException("Doesn't end in opening bracket");
		}
		
		State currentState = State.VOID;
		for (int i = 1; i < tokenList.size(); ++i) {
			currentState = getNextState(currentState, line.getTokenList()
					.get(i));
		}
	}
	
		/* (non-Javadoc)
		 * Method must be declared in global scope; any other problem in method
		 * declaration line will be caught by syntaxValidtor.
		 * @see oop.ex6.validator.StateMachine#isLineSemanticsValid(oop.ex6.line.Line, 
		 * oop.ex6.tables.VariableTable, oop.ex6.tables.MethodTable)
		 */
		@Override
	public void isLineSemanticsValid(Line line, VariableTable varTable,
			MethodTable methodTable) throws SemanticsException {
		if (!line.getScope().equals(TableAndScopeMaker.GLOBAL_SCOPE)) {
			throw new SemanticsException("Cannot declare method within method");
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
		case VOID:
			if (token.getTokenType().equals(TokenType.METHODNAME)) {
				return State.METHOD_NAME;
			}
			throw new SyntaxException("Expected legal method name");
			
		case METHOD_NAME:
			if (token.getTokenType().equals(TokenType.FINAL)) {
				return State.FINAL;
			}
			if (TokenType.isVarType(token.getTokenType())) {
				return State.VAR_TYPE;
			}
			
			if (token.getTokenType().equals(TokenType.CLOSINGPARENTHESIS)) {
				return State.CLOSE_PARENTHESIS;
			}
			throw new SyntaxException("Expected variable type");
			
		case FINAL:
			if (TokenType.isVarType(token.getTokenType())) {
				return State.VAR_TYPE;
			}
			throw new SyntaxException("Expected variable type");
			
		case VAR_TYPE: 
			if (token.getTokenType().equals(TokenType.VARNAME)) {
				return State.VAR_NAME;
			}
			throw new SyntaxException("Expected variable name");
			
		case VAR_NAME: 
			if (token.getTokenType().equals(TokenType.COMMA)) {
				return State.COMMA;
			}
			
			if (token.getTokenType().equals(TokenType.CLOSINGPARENTHESIS)) {
				return State.CLOSE_PARENTHESIS;
			}
			throw new SyntaxException("Unexpected token");
			
		case COMMA:
			if (token.getTokenType().equals(TokenType.FINAL)) {
				return State.FINAL;
			}
			
			if (TokenType.isVarType(token.getTokenType())) {
				return State.VAR_TYPE;
			}
			throw new SyntaxException("Expected variable type");
			
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



}
