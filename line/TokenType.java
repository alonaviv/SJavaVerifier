package oop.ex6.line;



public  enum TokenType {
	
	//Note: DOUBLEVAL only finds "pure" doubles - with a decimal point.
	//When checking a double variable, it can be both DOUBLEVAL and INTVAL.
	DOUBLEVAL("-?\\d++(?:\\.\\d+)",TokenType.VALUE_SYMBOL),
	INTVAL("-?\\d+",TokenType.VALUE_SYMBOL),
	STRINGVAL("\".+\"+",TokenType.VALUE_SYMBOL),
	CHARVAL("\'.\'",TokenType.VALUE_SYMBOL),
	//Same for boolean, is only the "pure" true/false attributes. 
	//When checking for a boolean, it can be both BOOLEANVAL and INTVAL.
	BOOLEANVAL("true|false",TokenType.VALUE_SYMBOL),
	INT("int ",TokenType.TYPE_SYMBOL),
	DOUBLE("double ",TokenType.TYPE_SYMBOL),
	STRING("String ",TokenType.TYPE_SYMBOL),
	CHAR("char ",TokenType.TYPE_SYMBOL),
	BOOLEAN("boolean ",TokenType.TYPE_SYMBOL),
	VOID("void "),
	FINAL("final "),
	IF("if\\s*\\("),
	WHILE("while\\s*\\("),
	RETURN("return;"),
	WHITESPACE("\\s+"),
	COMMA("\\,"),
	OPENINGBRACKET("\\{"),
	CLOSINGBRACKET("\\}"),
	OPENINGPARENTHESIS("\\("),
	CLOSINGPARENTHESIS("\\)"),
	EQUALS("="),
	ANDBOOL("&&"),
	ORBOOL("\\|{2}"),
	SEMICOLON(";"),
	COMMENTDECLARE("//"),
	METHODNAME("[a-zA-Z]\\w*\\s*\\("),
	VARNAME("[a-zA-Z][a-zA-Z-0-9_]*|_\\w+"),
	OTHERTOKEN(".");
	

	private static final int VALUE_SYMBOL = 1;
	private static final int TYPE_SYMBOL= 2;
	
	public final String pattern;
	private int valueOrTypeMarker;
	
	/**
	 * Token Type constructor
	 * @param pattern The token's regex pattern.
	 */
	private TokenType(String pattern) {
        this.pattern = pattern;
    }
	
	/**
	 * Token Type constructor
	 * @param pattern The token's regex pattern.
	 */
	private TokenType(String pattern, int valueOrTypeMarker) {
        this.pattern = pattern;
        this.valueOrTypeMarker = valueOrTypeMarker;
    }
	
	/**
	 * Checks if the given token type is a type of variable declaration
	 * @param tokenType A token type
	 * @return True iff the token type is a variable declaration
	 */
	public static boolean isVarType(TokenType tokenType) {
		return tokenType.valueOrTypeMarker == TYPE_SYMBOL;
	}
	
	/**
	 * Checks if the given token type is a type of variable value
	 * @param tokenType A token type
	 * @return True iff the token type is a variable value
	 */
	public static boolean isVarValue(TokenType tokenType) {
		return tokenType.valueOrTypeMarker == VALUE_SYMBOL;
	}
		
}
