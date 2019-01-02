import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pemdas
{
	public static void main(String[] args)
	{
		String equation = "(-2)^(-3)^-4"; //"(-2)^(-3)^-4" doesn't work because (-3)^-4 produces an even decimal, and you can't even root a negative number.
		System.out.println(calculate(equation));
		
	}
	
	public static double calculate(String equation)
	{
		equation = equation.replaceAll("\\)\\(", "\\)*\\(").replaceAll("\\)(?=\\d)", "\\)*").replaceAll("(?<=\\d)\\(", "*\\(");
		return postCalculate(equation);
	}
	
	/*
	 * Computes String equation while following PEMDAS 
	 * Accounts for chaining exponents (2^3^4)
	 * Accounts for parenthesis inside parenthesis
	 * Regular expression matches numbers that have decimal places (10.07)
	 * Computes left to right when there are chains of multiplication and division
	 * Computes left to right when there are chains of addition and subtraction 
	 * Accounts for very large numbers (2.42E10)
	 * Accounts for negative numbers (even if they are in the exponents, and -#^2 will output a negative number unless (-#)^2)
	 * (#)(#), (#)#, and #(#) will be interpreted as multiplying together. 
	 */
	public static double postCalculate(String equation)  
	{
		//Parenthesis 
		while(equation.contains("("))
		{
			String inside = insideParen(equation.substring(equation.indexOf("(")+1));
			double valueInside = calculate(inside); 
			String StrInside = valueInside + "";
			if(valueInside<0)
				StrInside = "<" + StrInside;
			equation = equation.replace("(" + inside + ")", StrInside);
		}
		
		//Exponents 
		String regex = "((?:(?<=<)-)?\\d+(?:\\.\\d+)?(?:E-?\\d+)?)\\^(\\<?(-?\\d+(?:\\.\\d+)?(?:E-?\\d+)?))(\\^)?";
		String temp = equation;
		int start = 0, end = 0;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(equation); 
		while(m.find())
		{	
			if(m.group(4) != null)
			{
				end += m.start(2);
				start += m.start(2);
				temp = temp.substring(m.start(2));
				m = p.matcher(temp);
				continue;
			}
			double basePart = Double.parseDouble(m.group(1));
			double exponentPart = Double.parseDouble(m.group(3));
			end += m.end(2);
			start += m.start(1);
			if(basePart < 0)
				start -= 1;
			equation = equation.substring(0, start) + Math.pow(basePart, exponentPart) + equation.substring(end);
			m = p.matcher(equation);
			temp = equation;
			start = 0;
			end = 0;
		}
		equation = equation.replace("<", "");

		//Multiplication and Division 
		regex = "(?:(?<!\\d)-)?\\d+(?:\\.\\d+)?(?:E-?\\d+)?(?:(?:\\*|/)(?:(?<!\\d)-)?\\d+(?:\\.\\d+)?(?:E-?\\d+)?)+";
		p = Pattern.compile(regex);
		m = p.matcher(equation);
		while(m.find())
		{	
			String[] numbers = m.group(0).split("\\*|/");
			String[] operations = m.group(0).split("(?:(?<!\\d)-)?\\d+(?:\\.\\d+)?(?:E-?\\d+)?");
			double total = Double.parseDouble(numbers[0]);
			for(int i = 1; i < numbers.length; i++)//Starting at 1 because the first element is blank due to the first character being a digit. 
			{
				if(operations[i].equals("*"))
					total *= Double.parseDouble(numbers[i]);
				else
					total /= Double.parseDouble(numbers[i]);
			}
			equation = equation.replace(m.group(0), total + "");
		}
		
		//Addition and Subtraction 
		String[] numbers = equation.split("\\+|(?<=\\d)-"); // it will split at #- which is wrong 
		String[] operations = equation.split("(?:(?<!\\d)-)?\\d+(?:\\.\\d+)?(?:E-?\\d+)?");
		double total = Double.parseDouble(numbers[0]);
		for(int i = 1; i < numbers.length; i++)//Starting at 1 because the first element is blank due to the first character being a digit.
		{
			if(operations[i].equals("-"))
				total -= Double.parseDouble(numbers[i]);
			else
				total += Double.parseDouble(numbers[i]);
		}
	
		return total;
	}
	
	public static String insideParen(String equation)
	{
		int paren1 = 0; //)
		int paren2 = 1; //(
		char[] characters = equation.toCharArray();
		for(int i = 0; i<characters.length; i++)
		{
			if(characters[i] == '(')
				paren2++;
			if(characters[i] == ')')
				paren1++;
			if(paren1 == paren2)
				return equation.substring(0, i);
		}
		System.out.println("Incomplete parenthesis");
		return null;
	}
}











