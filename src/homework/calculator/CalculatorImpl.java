package homework.calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

public class CalculatorImpl {
	final static Logger logger = Logger.getLogger(CalculatorImpl.class);
	Stack<String> stack;
	Map<String, Integer> operations;
	Map<String, Integer> variables;
	CalculatorOperations calcOperations;

	CalculatorImpl() {
		stack = new Stack<>();
		variables = new HashMap<>();
		calcOperations = new CalculatorOperations();
		operations = calcOperations.getOperations();
	}
	
	/* Command to execute in terminal
	 * mvn exec:java -Dexec.mainClass="homework.calculator.CalculatorImpl" 
	 * -Dexec.args="add(2,2)"
	 */
	public static void main(String[] args) throws Exception {
		/*Assuming the input does not contain any spaces*/
		String input = args[0];
		CalculatorImpl calc = new CalculatorImpl();
		logger.info("Result: " + calc.parseString(input));
	}

	private long parseString(String input) throws Exception {
		String func = "";
		try {
			logger.info("input: " +  input);
			for(int index = 0; index<input.length(); index++) {
				func += input.charAt(index);
				if(operations.containsKey(func)) {
					// After finding the the right operation from the operations map,
					// if the func/operation immediately doesn't contain '(' then,
					// operation is considered as "Unrecognized operation".
					if(input.charAt(index+1) != '(') {
						throw new Exception("Unrecognized operation found.");
					}
					return execute(func, operations.get(func), input.substring(index+2, partitionIt(index+1, input)));
				}
			}
			throw new Exception("Unrecognized input."); 
		} catch(Exception e) {
			logger.error(e.getMessage() + " \n***Exiting***");
			System.exit(1);
		}
		return -1; // default value returned as -1; It will never reach to this statement.
	}
	
	private long execute (String func, int numParams, String input) throws Exception{
		logger.info("operation: " + func + ", Parameters {" + input + "}");
		long result = -1;
		String variable = "";
		try {
			if(func.equals("let")) {
				variable = input.substring(0, input.indexOf(",")); 
				variables.put(variable, null);
				numParams--;
				input = input.substring(input.indexOf(",")+1, input.length());
			}
			
			// variables used in arithmetic operation
			int a=0, b=0;
			
			while(numParams != 0) {
				/* if suppose input is "5,mul(5,10)"
				* As the first character is a digit, the else condition is executed
				*	and takes '5' as one of the variable value in this arithmetic operation.
				* The next loop in while where input becomes "mul(5,10)", 
				*   'm' is not a digit. Hence "parseString" function is called to retrieve
				*   result for input "mul(5,10)".
				*/
				if(!Character.isDigit(input.charAt(0))) {
					if(numParams == 2) {
						int tempIndex = partitionIt(0, input);
						int value = (int) parseString(input.substring(0, tempIndex+1));
						if(func.equals("let")) {
							variables.put(variable, value);
						}else {
							a = value;
						}
						input = input.substring(tempIndex+2, input.length());
					} else {
						if(func.equals("let")) {
							/* When we are executing the last parameter in let function,
							 * We should check, if the operation is another "let" or 
							 * an Arithmetic operation
							 */
							if(input.startsWith("let")) {
								return parseString(input);
							} else {
								/* If the final operation is arithmetic,
								 * split the function and its parameters
								 * and return the final result*/
								func = input.substring(0, input.indexOf('('));
								a = variables.get(input.substring(input.indexOf('(')+1, input.indexOf(',')));
								b = variables.get(input.substring(input.indexOf(',')+1, input.indexOf(')')));
							}
						}
						else {
							b = (int) parseString(input);
						}
					}
				}else {
					int commaIndex = input.indexOf(',');
					if(commaIndex != -1) {
						String temp = input.substring(0, commaIndex);
						int value = Integer.valueOf(temp);
						if(func.equals("let")) {
							logger.info("adding variable: " + variable + " to the map "
									+ "with value: " + value);
							variables.put(variable, value);
						} else {
							a=value;
						}
						input = input.substring(commaIndex+1, input.length());
					} else {
						String temp = input.substring(0, input.length());
						int value = Integer.valueOf(temp);
						b = value;
					}
				}
				numParams -- ;
			}
			
			if(func.equals("add")) {
				result = calcOperations.add(a,b);
			}
			
			if(func.equals("mul")) {
				result = calcOperations.mul(a,b);
			}
			
			if(func.equals("sub")) {
				result = calcOperations.sub(a, b);
			}
			
			if(func.equals("div")) {
				result = calcOperations.div(a, b);
			}
		}catch(Exception e) {
			throw new Exception(e);
		}
		return result;
	}
	
	
	/**
	 * This function returns the end index of where the function ends.
	 * 
	 * For example: if given input as "mul(a,10),add(b,a)"
	 * 	then, returns the index as 8. Because that is where the "mul" function ends.
	 **/
	private int partitionIt (int index, String input) {
		Stack<Character> s = new Stack<> ();
		for(int i=index; i<input.length(); i++) {
			if(input.charAt(i) == ')') {
				s.pop();
				if(s.isEmpty())
					return i;
			} else if(input.charAt(i) == '(') {
				s.push('(');
			}
		}
		return input.length();
	}
}


class CalculatorOperations implements Calculator {
	Map<String, Integer> operations;
	
	public long add (int a, int b) {
		return a+b;
	}
	
	public long mul(int a, int b) {
		return a*b;
	}
	
	public long sub(int a, int b) {
		return a-b;
	}
	
	public long div(int a, int b) {
		if(b == 0) {
			throw new IllegalArgumentException("Argument 'divisor' is 0.");
		}
		return a/b;
	}
	
	public Map<String, Integer> getOperations() {
		return getCalcOperations();
	}
}