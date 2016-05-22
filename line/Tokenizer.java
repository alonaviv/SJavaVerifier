package oop.ex6.line;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class receives a String and parses it into "tokens" - each representing
 * an accepted section of code. The token list is then loaded into a newly created 
 * line object
 * @author Alon and Michal
 *
 */
public class Tokenizer {
	
	private static final int FIRST_OR_OPERATOR_LOCATION = 1;
	private static final int NO_OCCURENCE_IN_STRING_SYMBOL = -1;
	private static final char END_OF_NAME_FIRST_OPTION = ' ';
	private static final char END_OF_NAME_SECOND_OPTION = '(';
	
	
	/**
	 * TokenType enum. Each holds the regex pattern of a keyword in the code.
	 * @author alonav11
	 *
	 */

	
	private String tokenPatterns;
	
	/**
	 * Constructor. Creates a big regex string out of all the existing token type.
	 */
	public Tokenizer(){
		tokenPatterns = getPatternsString();
	}
	
	/**
	 * Represents a single keyword in a line. Holds the type
	 * of token, and in case of a name of a variable\method, holds the actual name as well.
	 * @author alonav11
	 *
	 */
	public class Token {
		private final TokenType tokenType;
		private String name = null;
		
		
		/**
		 * Constructor
		 * @param tokenType Type of token
		 */
		public Token(TokenType tokenType){
			this.tokenType = tokenType;
			
		}
		
		
		/**
		 * Constructor with token name
		 * @param type Type of token
		 * @param name Name of method/variable
		 */
		public Token(TokenType tokenType, String name) {
			this.tokenType = tokenType;
			this.name = name;
		}
		
		/**
		 * @return TokenType
		 */
		public TokenType getTokenType() {

			return tokenType;
		}
		
		/**
		 * @return Name of method\variable
		 */
		public String getName() {
			return name;
		}
		
	}
	
	/**
	 * This method receives a string from the line in a file
	 * and creates a line object loaded with a list of tokens that appear in that string.
	 * @param lineContent Line in file to tokenize
	 * @param lineNumber Number of the line being created
	 * @return Line object loaded with the token list and line number.
	 * @throws ParseException 
	 */
	public Line tokenize(String lineContent, int lineNumber) throws ParseException {
		
		List<Token> tokenList = new ArrayList<Token>();
		
		Pattern pattern = Pattern.compile(tokenPatterns);
		Matcher matcher = pattern.matcher(lineContent);
		while(matcher.find()){
			for(TokenType tokenType:TokenType.values()){
				// Adding to the token list all tokens except the whitespace token.
				// If it's a method or variable name, adding the name to the token, excluding the
				// white spaces and parenthesis.
				 if(matcher.group(tokenType.name()) != null && tokenType != TokenType.WHITESPACE){
					
					if(tokenType == TokenType.VARNAME){
						tokenList.add(new Token(tokenType,matcher.group(tokenType.name())));
					}else if(tokenType == TokenType.METHODNAME){
						// Getting the index of the end of the actual method name from the full METHODNAME.
						int endOfName = matcher.group(tokenType.name()).indexOf(END_OF_NAME_FIRST_OPTION);
						if(endOfName == NO_OCCURENCE_IN_STRING_SYMBOL){
							endOfName = matcher.group(tokenType.name()).indexOf(END_OF_NAME_SECOND_OPTION);
						}
						
						tokenList.add(new Token(tokenType,matcher.group(tokenType.name())
								                                             .substring(0, endOfName)));
					}else{
						tokenList.add(new Token(tokenType));
					}
				}
			}
		}
		
		return new Line(lineNumber,tokenList);
	}

	
	/*
	 * This method appends all the existing patterns into a long string. Each pattern
	 * appears in a named capturing group, with that name being the name of the pattern. Each
	 * capturing group is separated by "|", making the whole string one long pattern that checks
	 * to see which tokens (=capturing groups) fit a whole line of text.
	 */
	private String getPatternsString(){
		StringBuffer tokenPatterns = new StringBuffer();
		for(TokenType tokenType: TokenType.values()){
			tokenPatterns.append(String.format("|(?<%s>%s)",tokenType.name(), tokenType.pattern));		
		}
		//Removing the "|" at the beginning of the string.
		return tokenPatterns.substring(FIRST_OR_OPERATOR_LOCATION); 
		
	}
	
}
