# Calculator
Does the basic operations like add, sub, mul and div.

After cloning the repository, run the following command to build and download the dependencies
  mvn clean install

Below is the command to execute the project:
  mvn exec:java -Dexec.mainClass="homework.calculator.CalculatorImpl" -Dexec.args="add(2,2)"
