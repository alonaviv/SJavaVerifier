package oop.ex6.tables;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oop.ex6.line.Line;
import oop.ex6.tables.Variable.VarType;
import oop.ex6.line.TokenType;
import oop.ex6.line.Tokenizer.Token;


/**
 * This object creates the method and variable tables. It then can receive a line object, and
 * if it contains a variable or method
 * declaration it adds the relevant variable to the relevant table. 
 * The object saves his current scope within the program and updates the given
 * line with that scope.
 * @author Alon and Michal
 *
 */
public class TableAndScopeMaker{
	public static final String GLOBAL_SCOPE = "global";
	public static final String SCOPE_SEPERATOR = ".";
	public static final String IF_SCOPE = "if";
	public static final String WHILE_SCOPE = "while";
	

	private String currentScope;
	private int ifCounter;
	private int whileCounter;
	private VariableTable variableTable;
	private MethodTable methodTable;
	
	
	/**
	 * Constructor. Starts the beginning scope off as "Global".
	 */
	public TableAndScopeMaker(){
		currentScope = GLOBAL_SCOPE;
		variableTable = new VariableTable();
		methodTable = new MethodTable();
		ifCounter = 0;
		whileCounter = 0;
	}
	
	/**
	 * Receives a line, updates the line's scope and adds a variable or 
	 * method in the relevant table accordingly. The line receives the previous existing scope,
	 * before the scope is updated. This is true except for the case of the closing bracket line,
	 * which receives the scope that is outside of the bracket.
	 * @param line
	 * @throws InvalidTableAccessException 
	 */
	public void addtoTable(Line line) throws InvalidTableAccessException, ScopeException{
		List<Token> tokenList = line.getTokenList();
		switch(line.getLineType()){
		case VAR_DECLARATION: 
			addVariable(tokenList, false);
			line.setScope(currentScope);
			break;
			
		case FINAL_VAR_DECLARATION:
			addVariable(tokenList, true);
			line.setScope(currentScope);
			break;
			
		case METHOD_DECLARATION:
			line.setScope(currentScope);
			addMethod(tokenList, line.getLineNumber(), line.getScope());
			// Scope is updated within addMethod, after the name of
			// the method is located.
			break;
			
		case IF_STATEMENT:
			line.setScope(currentScope);
			ifCounter ++;
			currentScope += SCOPE_SEPERATOR + IF_SCOPE + ifCounter;
			break;
			
		case WHILE_STATEMENT:
			line.setScope(currentScope);
			whileCounter ++;
			currentScope += SCOPE_SEPERATOR + WHILE_SCOPE + whileCounter;
			break;
	
		case CLOSING_BRACKET:
			if(currentScope == GLOBAL_SCOPE){
				throw new ScopeException("Too many closing brackets");
			}
			currentScope =  currentScope.substring(0, currentScope.lastIndexOf(SCOPE_SEPERATOR));
			line.setScope(currentScope);
			break;
			
		default:
			line.setScope(currentScope);
			break;
				
		}
	}
	
	public VariableTable getVariableTable(){
		return variableTable;
	}
	
	public MethodTable getMethodTable(){
		return methodTable;
	}

	/**
	 * Goes over a given token list of a variable declaration line and
	 * and adds variables to the VariableTable.
	 * @param tokenList TokenList of a method declaration line.
	 * @param isFinal Boolean stating if the declaration starts with a final modifier
	 * @throws InvalidTableAccessException 
	 */	
	private void addVariable(List<Token> tokenList, boolean isFinal)
			                                       throws InvalidTableAccessException{
		VarType varType;
		Iterator<Token> tokenIterator = tokenList.iterator();
		Token currentToken;
		
		if(isFinal){ // If the final modifier appears, the method skips it.
			tokenIterator.next();
		}
		
		// Saving the first token as the VarType.
		//The VarType and Tokens at the start of VAR_DECLARATION are of the same names, 
		// so conversion is possible.	
		varType = VarType.valueOf(tokenIterator.next().getTokenType().name());
		
		
		currentToken = tokenIterator.next();
		// The token after the VarType should be the name of the variable to be added. Adding it
		// to the list if it is in fact a variable name.
		if(currentToken.getTokenType() == TokenType.VARNAME){
			variableTable.addVariable(currentToken.getName(), new Variable(varType, currentScope,
																							isFinal));					
		}
		
		//Adding every other variable name that is preceded by a comma.
		while(tokenIterator.hasNext()){
			currentToken = tokenIterator.next();
			if (currentToken.getTokenType() == TokenType.COMMA){
				currentToken = tokenIterator.next();
				if(currentToken.getTokenType() == TokenType.VARNAME)
					variableTable.addVariable(currentToken.getName(), new Variable(varType, 
																			currentScope, isFinal));
			}
		}
		
	}
	
	/**
	 * Goes over a given token list of a method declaration line and
	 * and adds the method to the methodTable and its parameters to the VariableTables
	 * @param tokenList TokenList of a method declaration line.
	 * @return method name
	 * @throws InvalidTableAccessException 
	 */
	
	private String addMethod(List<Token> tokenList, int lineNumber, String lineScope)
			 throws InvalidTableAccessException{
		VarType varType;
		Iterator<Token> tokenIterator = tokenList.iterator();
		List<VarType> methodParameters = new ArrayList<VarType>();
		Token currentToken = tokenIterator.next();
		String methodName;
		boolean isFinal = false;
		Variable newVariable;

		// Advance iterator until the name of the method
		while(currentToken.getTokenType() != TokenType.METHODNAME && tokenIterator.hasNext()){
			currentToken = tokenIterator.next();
		}

		methodName = currentToken.getName();
		currentScope += SCOPE_SEPERATOR + methodName;
		currentToken = tokenIterator.next();

		while(currentToken.getTokenType() != TokenType.CLOSINGPARENTHESIS){
			if(currentToken.getTokenType() == TokenType.FINAL){
				currentToken = tokenIterator.next();
				isFinal = true;
			}
			
			varType = VarType.valueOf(currentToken.getTokenType().name());
			methodParameters.add(varType);
			currentToken = tokenIterator.next();
			newVariable = new Variable(varType,currentScope, isFinal);
			newVariable.initialize(lineNumber, currentScope);
			variableTable.addVariable(currentToken.getName(), newVariable);									
		
			// Passing over the comma onto the next 
			currentToken = tokenIterator.next();
			if(currentToken.getTokenType() == TokenType.COMMA){
				currentToken = tokenIterator.next();
			}
	
		}
		
		methodTable.addMethod(methodName, methodParameters);
		return methodName;
	}
}

	



