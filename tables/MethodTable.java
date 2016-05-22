package oop.ex6.tables;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oop.ex6.tables.Variable.VarType;

/**
 * This class holds a table that maps method names to Method objects.
 * each method object hold a list of VarType enums that represent
 * the parameters of that method.
 * @author Alon and Michal
 *
 */
public class MethodTable {
	
	private Map<String, Method> table;
	
	public MethodTable(){
		table = new HashMap<String, Method>();
	}
	
	/**
	 * Represents a single method. Holds a list of VarType enums,
	 * one for each of the method's parameters.
	 * @author Alon and Michal
	 *
	 */
	private class Method {
		
		private final List<VarType> parameterTypeList;
		
		/**
		 * Constructor
		 * @param typeList List of parameter types
		 */
		private Method(List<VarType> parameterTypeList) {
			this.parameterTypeList = parameterTypeList;
		}
		
		/**
		 * @return List of parameter types
		 */
		private List<VarType> getParameterTypeList() {
			return parameterTypeList;
		}
		

	}
	
	/**
	 * Receives a method name and ordered list of arguments, adds to method
	 * table. 
	 * @param name Method name
	 * @param typeList List of the types of parameters in the method
	 * @throws InvalidTableAccessException 
	 */
	public void addMethod(String name, List<VarType> parameterTypeList) 
			                                   throws InvalidTableAccessException{
		if (table.containsKey(name)) {
			throw new InvalidTableAccessException("Requested method already exists");
		}
		Method method = new Method(parameterTypeList);
		table.put(name, method);
	}
	

	/**
	 * Receives the name of a method and an ordered list of the types of the
	 * parameters called with the method. Makes sure that a method that has this name
	 * and parameter list exists in the method table. if not, throws an exception
	 * @param name Method name
	 * @param paramList List of parameter types
	 * @throws InvalidTableAccessException 
	 */
	public void verifyMethod(String name, List<VarType> paramList) throws InvalidTableAccessException{
		Method extractedMethod = table.get(name);
		List<VarType> extractedMethodParams;
		if(extractedMethod != null){
			extractedMethodParams = extractedMethod.getParameterTypeList();
			if(paramList.size() == extractedMethodParams.size()){
				Iterator<VarType> paramListIterator = paramList.iterator();
				Iterator<VarType> extractedParamListIterator = extractedMethodParams.iterator();
				while(paramListIterator.hasNext()){
					if(!VarType.doVarTypesMatch(extractedParamListIterator.next(),
							                              paramListIterator.next())){
						throw new InvalidTableAccessException("Trying to call method with invalid "
							                                                      	+ "parameter type");
					}
				}		
			}else{
				throw new InvalidTableAccessException("Trying to call method with invalid "
																				+ "number of parameters");
			}
		}else{
			throw new InvalidTableAccessException("Requested method has not been declared");
		}
	}
	
}
