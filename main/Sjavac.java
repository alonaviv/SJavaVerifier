package oop.ex6.main;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import oop.ex6.line.Line;
import oop.ex6.line.Tokenizer;
import oop.ex6.validator.SemanticsException;
import oop.ex6.validator.SyntaxException;
import oop.ex6.validator.SyntaxValidator;
import oop.ex6.validator.SemanticsValidator;
import oop.ex6.tables.InvalidTableAccessException;
import oop.ex6.tables.ScopeException;
import oop.ex6.tables.TableAndScopeMaker;

/**
 * Main class of the s-Java verifier. The class receives a source code file written in
 * Sjava and returns 0 if the file is valid, 1 if the file isn't valid, and 2 for IO errors.
 * In the case the file isn't valid, explanations will be printed to the error stream.
 * @author Alon and Michal
 *
 */
public class Sjavac {
		
		private static final int LEGAL_CODE_MARKER = 0;
		private static final int ILLEGAL_CODE_MARKER = 1;
		private static final int IO_ERROR_MARK = 2;
	
	
		private static final int NUMBER_OF_SOURCE_FILES = 1;
		private static final int STARTING_LINE_NUMBER = 1;
		
		/**
		 * Main method. Runs the verifier
		 * @param args The path to the s-Java code file.
		 */
		
		public static void main(String[] args){
			if(args.length != NUMBER_OF_SOURCE_FILES){
				System.err.println("Wrong number of parameters.");
				return;
			}
			
			TableAndScopeMaker tableMaker = new TableAndScopeMaker();
			Tokenizer tokenizer = new Tokenizer();
			SyntaxValidator syntaxValidator = new SyntaxValidator();
			SemanticsValidator semanticsValidator;
			File file = new File(args[0]);
			int currentLineNumber = STARTING_LINE_NUMBER;
			String currentLineString;
			Line currentLineObject;
			List<Line> linesList =new ArrayList<Line>();
			try(Scanner scanner = new Scanner(file)){
				while(scanner.hasNextLine()){
					currentLineString = scanner.nextLine();
					//The program only pays attention to non empty lines
					if(!currentLineString.trim().isEmpty()){
						currentLineObject = tokenizer.tokenize(currentLineString, currentLineNumber);
						syntaxValidator.validate(currentLineObject);
						tableMaker.addtoTable(currentLineObject);
						linesList.add(currentLineObject);
						currentLineNumber++;
					}
					
				}
				semanticsValidator =  new SemanticsValidator(linesList, tableMaker.getVariableTable(),
						                                                      tableMaker.getMethodTable());
				semanticsValidator.validateSemantics();
			}
			catch(InvalidTableAccessException e){
				System.out.println(ILLEGAL_CODE_MARKER);
				System.err.println("Invalid table operation: "+ e.getMessage());
				return;
			}
			catch(ParseException e){
				System.out.println(ILLEGAL_CODE_MARKER);
				System.err.println("Invalid line type: "+ e.getMessage());
				return;
			}
			catch(SyntaxException e){
				System.out.println(ILLEGAL_CODE_MARKER);
				System.err.println("Syntax error: " + e.getMessage());
				return;
			}
			catch(SemanticsException e){
				System.out.println(ILLEGAL_CODE_MARKER);
				System.err.println("Semantics error: " + e.getMessage());
				return;
			}
			catch(ScopeException e){
				System.out.println(ILLEGAL_CODE_MARKER);
				System.err.println("Scope error: " + e.getMessage());
				return;
			}
			catch(IOException e){
				System.out.println(IO_ERROR_MARK);
				return;
			}
			System.out.println(LEGAL_CODE_MARKER);
		}
			
	
}
