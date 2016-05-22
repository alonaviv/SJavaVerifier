package oop.ex6.tables;

/**
 * This class represents a single variable within the variable table. The
 * variable's name will be the map's key (there can be several variables for
 * each key). The variable holds his own scope, type, and isFinal and
 * wasInitialized booleans.
 * 
 * @author Alon and Michal
 *
 */
public class Variable {

	public static final int NOT_INITIALIZED = -1;

	private final VarType type;
	private final String scope;
	private String scopeOfInitialization;
	private final boolean isFinal;
	private int lineInitialized;

	public static enum VarType {
		INT, DOUBLE, FLOAT, STRING, CHAR, BOOLEAN;

		/**
		 * Receives a VarType (left) that is going to be assigned another
		 * VarType (right). Returns true iff the assignment is valid (For
		 * instance assigning an int to a double or assigning String to String)
		 * 
		 * @param leftVar
		 *            The variable type that will receive rightVar
		 * @param rightVar
		 *            The variable type that will be assigned to leftVar
		 * @return True iff the assignment is valid
		 */
		public static boolean doVarTypesMatch(VarType leftVar, VarType rightVar) {

			return (leftVar.equals(rightVar)
					|| (leftVar.equals(DOUBLE) && rightVar.equals(INT)) || (leftVar
					.equals(BOOLEAN))
					&& (rightVar.equals(DOUBLE) || (rightVar.equals(INT))));
		}

	}

	/**
	 * Constructor
	 * 
	 * @param type
	 *            VarType of variable
	 * @param scope
	 *            The scope of the variable's declaration
	 * @param isFinal
	 *            Is the variable defined as final.
	 */
	Variable(VarType type, String scope, boolean isFinal) {
		this.type = type;
		this.scope = scope;
		this.isFinal = isFinal;
		lineInitialized = NOT_INITIALIZED;
	}

	/**
	 * @return Type of variable
	 */
	public VarType getType() {
		return type;
	}

	/**
	 * @return Scope of variable declaration
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @return True iff the variable has a final modifier
	 */
	public boolean isFinal() {
		return isFinal;
	}

	/**
	 * @return Number of line when variable was initialized. -1 If it wasn't
	 *         initialized yet.
	 */
	public int lineInitialized() {
		return lineInitialized;
	}
	
	public String scopeInitialized(){
		return scopeOfInitialization;
	}
	

	/**
	 * Marks variable as initialized.
	 */
	public void initialize(int lineInitialized, String scopeOfInitialization){
		this.lineInitialized = lineInitialized;
		this.scopeOfInitialization = scopeOfInitialization;
	}

}