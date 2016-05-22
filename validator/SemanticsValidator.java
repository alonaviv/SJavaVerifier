package oop.ex6.validator;

import java.util.List;

import oop.ex6.line.Line;
import oop.ex6.line.Line.LineType;
import oop.ex6.line.Tokenizer.Token;
import oop.ex6.line.TokenType;
import oop.ex6.tables.InvalidTableAccessException;
import oop.ex6.tables.TableAndScopeMaker;
import oop.ex6.tables.Variable;
import oop.ex6.tables.VariableTable;
import oop.ex6.tables.MethodTable;
import oop.ex6.tables.Variable.VarType;

/**
 * Semantics validator runs through list of lines, and checks that each line is
 * semantically valid within the context of the sJava file.
 * 
 * @author Michal and Alon
 *
 */
public class SemanticsValidator {

	List<Line> lineList;
	VariableTable varTable;
	MethodTable methodTable;

	/**
	 * Constructor receives list of lines, variable table and method table as
	 * already created by Tokenizer and TableAndScopeMaker in main.
	 * 
	 * @param lineList
	 * @param varTable
	 * @param methodTable
	 */
	public SemanticsValidator(List<Line> lineList, VariableTable varTable,
			MethodTable methodTable) {
		this.lineList = lineList;
		this.varTable = varTable;
		this.methodTable = methodTable;
	}

	/**
	 * Main function of SemanticsValidator class, runs over list of lines and
	 * validates each line according to type. When reaching the end of the list,
	 * checks that the brackets have been balanced, and there does not exist a
	 * scope that has not been exited.
	 * 
	 * @throws SemanticsException
	 * @throws SyntaxException
	 * @throws InvalidTableAccessException
	 */
	public void validateSemantics() throws SemanticsException, SyntaxException,
			InvalidTableAccessException {
		updateVarInitiliazationLines();
		for (Line line : lineList) {
			validateLine(line);
		}
		Line lastLine = lineList.get(lineList.size() - 1);
		if (!lastLine.getScope().equals(TableAndScopeMaker.GLOBAL_SCOPE)) {
			throw new SemanticsException("Not all brackets were closed");
		}
	}

	/**
	 * Iterates over all lines in the file, and if the line is of a type that can
	 * include initiliazation of a variable, it updates the variable table
	 * to that effect.
	 * @throws InvalidTableAccessException
	 */
	private void updateVarInitiliazationLines() throws InvalidTableAccessException {
		for (Line line : lineList) {
			LineType lineType = line.getLineType();
			if (lineType.equals(LineType.FINAL_VAR_DECLARATION)
					|| lineType.equals(LineType.VAR_DECLARATION)
					|| lineType.equals(LineType.VAR_ASSIGNMENT)) {
				initializeVarsInVariableLine(line);
			}
		}
	}

	/**
	 * Receives a single line, and for each variable in line for which there is
	 * an assignment, updates lineInitialized in Variable object.
	 * @param line
	 * @throws InvalidTableAccessException
	 */
	private void initializeVarsInVariableLine(Line line) throws InvalidTableAccessException {
		List<Token> tokenList = line.getTokenList();
		for (int i = 0; i < tokenList.size() -1; ++i) {
			Token token = tokenList.get(i);
			Token nextToken = tokenList.get(i + 1);
			if (token.getTokenType().equals(TokenType.VARNAME)) {
				Variable newVar = varTable.getVariableInLegalScope(
						token.getName(), line.getScope());
				if (nextToken.getTokenType().equals(TokenType.EQUALS)) {
					newVar.initialize(line.getLineNumber(), line.getScope());
				}
			}
		}
	}

	/**
	 * Creates a stateMachine depending on lineType, and then validates syntax
	 * according to specific stateMachine.
	 * 
	 * @param line
	 * @throws SemanticsException
	 * @throws SyntaxException
	 * @throws InvalidTableAccessException
	 */
	private void validateLine(Line line) throws SemanticsException,
			SyntaxException, InvalidTableAccessException {
		LineType type = line.getLineType();
		StateMachine stateMachine;
		switch (type) {
		case FINAL_VAR_DECLARATION:
		case VAR_DECLARATION:
			stateMachine = new VarDeclarationStateMachine();
			break;
		case VAR_ASSIGNMENT:
			stateMachine = new VarAssignmentStateMachine();
			break;
		case METHOD_DECLARATION:
			stateMachine = new MethodDeclarationStateMachine();
			break;
		case METHOD_CALL:
			stateMachine = new MethodCallStateMachine();
			break;
		case IF_STATEMENT:
		case WHILE_STATEMENT:
			stateMachine = new ConditionStatementStateMachine();
			break;
		case RETURN:
			stateMachine = new ReturnStateMachine();
			break;
		case CLOSING_BRACKET:
			stateMachine = new ClosingBracketStateMachine(lineList);
			break;
		case COMMENT:
			stateMachine = new CommentStateMachine();
			break;
		default:
			throw new IllegalArgumentException();
		}

		stateMachine.isLineSemanticsValid(line, varTable, methodTable);
		return;
	}

	/**
	 * Receives a token representing a variable or a literal, a variable table
	 * and a line, and checks what variable type matches the method. If token
	 * represents a variable, it checks that the variable exists in the table
	 * with a value that was assigned before the reference (or in a global
	 * scope).
	 * 
	 * @param token
	 * @param varTable
	 * @param line
	 * @return VarType of Token
	 * @throws InvalidTableAccessException
	 * @throws SyntaxException
	 * @throws SemanticsException
	 */
	static VarType getVarTypeFromToken(Token token, VariableTable varTable,
			Line line) throws InvalidTableAccessException, SyntaxException,
			SemanticsException {
		TokenType tokenType = token.getTokenType();
		switch (tokenType) {
		case INTVAL:
		case INT:
			return VarType.INT;
		case DOUBLEVAL:
		case DOUBLE:
			return VarType.DOUBLE;
		case STRINGVAL:
		case STRING:
			return VarType.STRING;
		case CHARVAL:
		case CHAR:
			return VarType.CHAR;
		case BOOLEANVAL:
		case BOOLEAN:
			return VarType.BOOLEAN;
		case VARNAME:
			Variable variable = varTable.getVariableInLegalScope(
					token.getName(), line.getScope());
			if (!wasVarInitializedInBadLine(variable, line)) {
				return variable.getType();
			}
			throw new SemanticsException(
					"Variable does not exist with value in legal scope");
		default:
			throw new SyntaxException("Illegal TokenType");
		}
	}

	/**
	 * Receives a variable and a line number, and checks whether variable has
	 * been assigned a value before the desired line, or in the global scope.
	 * 
	 * @param var Variable object to be checked
	 * @param lineNumber Line number to be checked
	 * @return true iff the given variable wasn't initialized in a location that
	 * allows it to be used within the current line
	 */
	static boolean wasVarInitializedInBadLine(Variable var, Line line) {
			if(var.lineInitialized() == Variable.NOT_INITIALIZED){
				return true;
			}
			
			if(!var.getScope().equals(TableAndScopeMaker.GLOBAL_SCOPE)){
				if(!var.getScope().equals(line.getScope()) || (!var.scopeInitialized(). 
						     equals(line.getScope())) ||
						var.lineInitialized() >= line.getLineNumber()){
					return true;
					
				}
				
			}else{
				if(line.getScope().equals(TableAndScopeMaker.GLOBAL_SCOPE)){
					if(var.lineInitialized() >= line.getLineNumber()){
						return true;
					}
				}else{
					if(!var.scopeInitialized().
									equals(TableAndScopeMaker.GLOBAL_SCOPE)){
						if(!var.scopeInitialized().equals(line.getScope())){
						return true;
						}
					}
				}
			}
			
			return false;				
				
	}
}
