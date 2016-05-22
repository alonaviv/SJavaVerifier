package oop.ex6.validator;

import java.util.List;

import oop.ex6.line.Line;
import oop.ex6.line.Tokenizer.Token;
import oop.ex6.line.TokenType;
import oop.ex6.tables.InvalidTableAccessException;
import oop.ex6.tables.MethodTable;
import oop.ex6.tables.TableAndScopeMaker;
import oop.ex6.tables.VariableTable;

/**
 * StateMachine for a ClosingBracket line. Contains methods to determine
 * whether line is syntactically and semantically valid.
 * @author Benjy
 *
 */
class ClosingBracketStateMachine implements StateMachine {

	List<Line> lineList;
	
	/**
	 * Constructor for class receives a list of lines.
	 * @param lineList
	 */
	public ClosingBracketStateMachine(List<Line> lineList) {
		this.lineList = lineList;
	}
	/* (non-Javadoc)
	 * Checks syntax of line according to following conditions:
	 * List should only contain a single token, and that token should be a
	 * closing bracket. Throws an exception if does not answer these conditions.
	 * @see oop.ex6.validator.StateMachine#isLineSyntaxValid(oop.ex6.line.Line)
	 */
	@Override
	public void isLineSyntaxValid(Line line) throws SyntaxException {
		List<Token> tokenList = line.getTokenList();
		if (tokenList.size() != 1
				&& tokenList.get(tokenList.size() - 1).getTokenType() 
				!= TokenType.CLOSINGBRACKET) {
			throw new SyntaxException("Illegal closing bracket line");
		}
	}

	/* (non-Javadoc)
	 * Checks semantics of line: this line is always semantically valid
	 * (if brackets are not balanced and there are too many - this will
	 * be caught by the TableAndScopeMaker), except when it closes a method
	 * and is not preceded by a "return" statement.
	 * @see oop.ex6.validator.StateMachine#isLineSemanticsValid(oop.ex6.line.Line,
	 * oop.ex6.tables.VariableTable, oop.ex6.tables.MethodTable)
	 */
	@Override
	public void isLineSemanticsValid(Line line, VariableTable varTable,
			MethodTable methodTable) throws SemanticsException, SyntaxException, 
			                                                 InvalidTableAccessException{
		if (line.getScope().equals(TableAndScopeMaker.GLOBAL_SCOPE)) {
			if (!lineList.get(line.getLineNumber() - 2).getLineType()
					.equals(Line.LineType.RETURN)) {
				throw new SemanticsException("Symantics error");
			}
		}
	}
}
