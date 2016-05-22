package oop.ex6.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class holds a table that maps Variable names to an array of variable
 * objects, each of them holding said name. The variables in each list have a different scope
 * as well as a "isFinal" and "wasInitialized" members. Each variable is also defined by its variableType.
 * The class manages the table, inserting new variables, getting information from existing variables
 * and checking if a variable exists in a valid scope.
 * @authors Alon and Michal
 *
 */
public class VariableTable {

	private Map<String, List<Variable>> table;


	public VariableTable(){
		table = new HashMap<String, List<Variable>>();
	}
	

	/**
	 * Attempts to add new variable to table. Checks whether a variable of the
	 * same name already exists in the same scope, in which case it does not add
	 * the variable to the table. 
	 * @param name Name of variable to add
	 * @param variableObject variable object to add, containing scope, type, and isFinal boolean.
	 * @throws InvalidTableAccessException 
	 */
	public void addVariable(String variableName, Variable variableObject)
			                                               throws InvalidTableAccessException{
		List<Variable> matchingVars = table.get(variableName);
		if (matchingVars != null) {
			for(Variable matchingVar:matchingVars){
				if (matchingVar.getScope().equals(variableObject.getScope())) {
					throw new InvalidTableAccessException("Requested variable already exists");
				}
			}
			matchingVars.add(variableObject);
		}else{
			List<Variable> newVariableList = new ArrayList<Variable>();
			newVariableList.add(variableObject);
			table.put(variableName, newVariableList);
		}
	}

	/**
	 * Receives a variable name and a current scope. Returns a variable object with that name 
	 * that exists within the given scope or above it. For example,
	 * given the scope global.foo.if and the variable name VAR, the method can return
	 * a variable VAR with a scope global.foo.
	 * @param varName Variable name
	 * @param scope The current scope that the caller is in
	 * @return Variable object with the given name that is accessed from within the given scope.
	 * @throws InvalidTableAccessException 
	 */
	public Variable getVariableInLegalScope(String varName, String scope) 
			                                                throws InvalidTableAccessException{
		List<Variable> varList = table.get(varName);
		if(varList != null){
			for(Variable var:varList){
				if(scope.startsWith(var.getScope())){
					return var;
				}
			}
		}
		throw new InvalidTableAccessException("Requested Variable hasn't been declared");
		
	}
	
}
