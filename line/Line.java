package oop.ex6.line;

import java.text.ParseException;
import java.util.List;

import oop.ex6.line.Tokenizer.Token;

/**
 * This class represents a line of text within the Sjava file. The line
 * hold a list of recognizable tokens - words/symbols that exist in the Sjava language.
 * The line type is defined by the first of these tokens (for instance a line starting with "void"
 * will be a method declaration line).
 * @author Alon and Michal
 *
 */
public class Line {

	
	public static enum LineType {
		FINAL_VAR_DECLARATION, VAR_DECLARATION, VAR_ASSIGNMENT, 
		METHOD_DECLARATION, METHOD_CALL, COMMENT, RETURN, IF_STATEMENT, 
		WHILE_STATEMENT, CLOSING_BRACKET;
	}

	private final int lineNumber;
	private String scope;
	private final List<Token> tokenList;
	LineType lineType;

	/**
	 * Constructor
	 * @param lineNumber Line number within the file
	 * @param tokenList List of tokens that represent the line
	 * @throws ParseException 
	 */
	public Line(int lineNumber, List<Token> tokenList) throws ParseException {
		this.lineNumber = lineNumber;
		this.tokenList = tokenList;
		determineLineType();
		

	}

	/**
	 * @param scope The scope that the line is in
	 */
	public void setScope(String scope) {
		this.scope = scope;

	}


	/**
	 * @return The scope of the line is in
	 * @throws ParseException 
	 */
	public String getScope(){
		return scope;
	}

	/**
	 * @return The type of the line, determined by the first token.
	 * @throws ParseException
	 */
	public LineType getLineType(){
		return lineType;
	}

	/**
	 * @return Line number within the code file
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @return List of tokens representing the line
	 */
	public List<Token> getTokenList() {
		return tokenList;
	}
	
	/**
	 * Determines the type of line according to the first token.
	 * @return Line Type
	 * @throws ParseException
	 */
	private void determineLineType() throws ParseException {
		if(tokenList.size() == 0){
			throw new ParseException("Line: No legal tokens in line", lineNumber);
		}
		TokenType firstToken = tokenList.get(0).getTokenType();
		if (TokenType.isVarType(firstToken)) {
			lineType = LineType.VAR_DECLARATION;
		}else{
			switch (firstToken) {
			case FINAL:
				lineType =  LineType.FINAL_VAR_DECLARATION;
				break;
			case VARNAME:
				lineType = LineType.VAR_ASSIGNMENT;
				break;
			case VOID:
				lineType = LineType.METHOD_DECLARATION;
				break;
			case METHODNAME:
				lineType =LineType.METHOD_CALL;
				break;
			case RETURN:
				lineType = LineType.RETURN;
				break;
			case CLOSINGBRACKET:
				lineType = LineType.CLOSING_BRACKET;
				break;
			case IF:
				lineType = LineType.IF_STATEMENT;
				break;
			case WHILE:
				lineType = LineType.WHILE_STATEMENT;
				break;
			case COMMENTDECLARE:
				lineType = LineType.COMMENT;
				break;
			default:
				throw new ParseException("Line: Line doesn't start"
					               	+" with a valid keyword",lineNumber);
			}
		}
	}
	
}
