package homework.calculator;

import java.util.HashMap;
import java.util.Map;

public interface Calculator {
	default Map<String, Integer> getCalcOperations() {
		// Operations contain the arithmetic operation it does
		// and how many parameters it would take
		Map<String, Integer> operations = new HashMap<>();
		operations.put("add", 2);
		operations.put("sub", 2);
		operations.put("mul", 2);
		operations.put("div", 2);
		operations.put("let", 3);
		return operations;
	}
	
	long add (int a, int b);
	long sub (int a, int b);
	long mul (int a, int b);
	long div (int a, int b);
}
