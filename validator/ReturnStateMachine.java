package oop.ex6.validator;

import java.util.List;

import oop.ex6.line.Line;
import oop.ex6.line.Tokenizer.Token;
import oop.ex6.line.TokenType;
import oop.ex6.tables.MethodTable;
import oop.ex6.tables.TableAndScopeMaker;
import oop.ex6.tables.VariableTable;

/**
 * StateMachine for return statement line. Includes methods to check 
 * whether line is syntactically and semantically valid.
 * @author Michal and Alon
 *
 */
class ReturnStateMachine implements StateMachine {

	/* (non-Javadoc)
	 * Runs through all tokens in line's tokenList, and ensures that they
	 * match a legal state. Also checks that line ends in semicolons.
	 * @see oop.ex6.validator.StateMachine#isLineSyntaxValid(oop.ex6.line.Line)
	 */
	public void isLineSyntaxValid(Line line) throws SyntaxException{
		List<Token> tokenList = line.getTokenList();
		if (tokenList.size() != 1
				&& tokenList.get(tokenList.size() - 1).getTokenType() != TokenType.RETURN) {
			throw new SyntaxException("Illegal closing bracket line");
		}
	}
	/* (non-Javadoc)
	 * Return statement must appear in non-global scope;
	 * @see oop.ex6.validator.StateMachine#isLineSemanticsValid(oop.ex6.line.Line, 
	 * oop.ex6.tables.VariableTable, oop.ex6.tables.MethodTable)
	 */
	
	@Override
	public void isLineSemanticsValid(Line line, VariableTable varTable,
				MethodTable methodTable) throws SemanticsException {
		if (line.getScope().equals(TableAndScopeMaker.GLOBAL_SCOPE)) {
			throw new SemanticsException("Return statement cannot appear in global scope");
		}
		
	}
}
