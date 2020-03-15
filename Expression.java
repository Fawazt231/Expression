//Fawaz Tahir
//Expression Evaluation
package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	expr = expr.replaceAll("\\s", "");
    	Pattern p = Pattern.compile("\\*|\\[|\\+|\\-|\\/|\\(|\\s|\\)|\\]");
    	Matcher m = p.matcher(expr);
    	ArrayList<Integer> t = new ArrayList();
    	t.add(0);
    	while (m.find()) {
    		t.add(m.start());	
    		}

    	System.out.println(t);
    	if (t.size() == 1 && Character.isLetter(expr.charAt(0))) {
    		vars.add(new Variable(expr));
    		System.out.println(vars);
    		return;
    	}
    	for(int i = 0; i<= t.size()-1; i++) {
    		if (expr.charAt(t.get(i)) == '[') {
    			if (t.get(i-1) == 0) {
    				arrays.add(new Array(expr.substring(t.get(i-1), t.get(i))));
    			} else {
    				arrays.add(new Array(expr.substring(t.get(i-1)+1, t.get(i))));
    			}
    		} else if(t.get(i) != 0 && Character.isLetter(expr.charAt(t.get(i)-1))) {
    			if (t.get(i-1) == 0 && expr.charAt(t.get(i-1)) != '(') {
    				vars.add(new Variable(expr.substring(t.get(i-1), t.get(i))));
    			} else if(Character.isLetter(expr.charAt(t.get(i)-1))){
    				vars.add(new Variable(expr.substring(t.get(i-1)+1, t.get(i))));
    			} 
    		} 			
    	}
    	if(Character.isLetter(expr.charAt(expr.length()-1))) {
			vars.add(new Variable(expr.substring(t.get(t.size()-1)+1,expr.length())));
			
		}


    	System.out.println(arrays);
    	System.out.println(vars);    
    }
    //expr2.charAt(t.get(i)) != ')'
    //expr.charAt(t.get(i)) != ']' && expr.charAt(t.get(i)) != '('&& expr.charAt(t.get(i)) != '['&& !(Character.isDigit(expr.charAt(t.get(i))))){
    //if (expr.charAt(t.get(i)-1) != ')' && expr.charAt(t.get(i)-1) != ']' ) 
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
        System.out.println(arrays);
        System.out.println(vars);
        
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
   private static int find(String expr, char p) {
	   for(int i = expr.length()-1; i>=0;) {
		   if(expr.charAt(i) == p) {
			   return i;
		   } else {
			   i--;
		   }
	   }
	   return 1;
   }
   private static int find2(String expr, char p) {
	   for(int i = 0; i<expr.length(); i++) {
		   if(expr.charAt(i) == p) {
			   return i;
		   }
	   }
	   return 0;
   }
   private static int findVar(ArrayList<Variable> list, String var) {
	   for (int i = 0; i<list.size(); i++) {
		   if(list.get(i).name.equals(var)) {
			   return i;
		   }
	   }
	   return 0;
   }
   private static int findArray(ArrayList<Array> list, String arr) {
	   for (int i = 0; i<list.size(); i++) {
		   if(list.get(i).name.equals(arr)) {
			   return i;
		   }
	   }
	   return 0;
   }
    public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	float result = 0;
    	Stack<String> k = new Stack<String>();
    	int j = 0;
    	int n = 0;
    	int z = 0;
    	boolean negative = false;
    	
    	//Parentheses
    	StringTokenizer st0 = new StringTokenizer(expr,delims,true);
    	while (st0.hasMoreTokens() ) {
    		k.push(st0.nextToken());
    	}
    	while (!k.isEmpty()) {
    		if(k.peek().equals("(")) {
    			j= Expression.find(expr, '(');
    			n = Expression.find2(expr.substring(j+1), ')');
    			String subexpr = new String(expr.substring(j+1).substring(0, n));
    			result = Expression.evaluate(subexpr, vars, arrays);
    			expr = expr.replace(expr.substring(j,j+subexpr.length()+2), Float.toString(result));
        		k.pop();
        		k.push(Float.toString(result));
    		} else {
    			k.pop();
    		}
    		

    	}
    	//Brackets
    	StringTokenizer stb = new StringTokenizer(expr,delims,true);
    	while (stb.hasMoreTokens() ) {
    		k.push(stb.nextToken());
    	}
    	while (!k.isEmpty()) {
    		if(k.peek().equals("[")) {
    			j= Expression.find(expr, '[');
    			n = Expression.find2(expr.substring(j+1), ']');
    			String subexpr = new String(expr.substring(j+1).substring(0, n));
    			result = Expression.evaluate(subexpr, vars, arrays);
    			k.pop();
    			int tgt = Expression.findArray(arrays, k.peek());
    			result = arrays.get(tgt).values[(int) result];
    			k.pop();
    			expr = expr.replace(expr.substring(j-arrays.get(tgt).name.length(),j+subexpr.length()+2), Float.toString(result));
        		k.push(Float.toString(result));
    		} else {
    			k.pop();
    		}
    		

    	}
    	//if Negative
    	if(expr.contains("-")) {
    		j = Expression.find2(expr, '-');
    		if (j!=0 ) {
    			if (expr.charAt(j-1) == '/' || expr.charAt(j-1) == '*') {
    			negative = true;
    			}
    		}
    	}
    	//ADD
    	StringTokenizer st = new StringTokenizer(expr,delims,true);
    	while (st.hasMoreTokens() ) {
    		k.push(st.nextToken());
    	}
    	while (!k.isEmpty()) {
    		if(k.peek().equals("+")) {
    			j= Expression.find(expr, '+');
    			result = Expression.evaluate(expr.substring(0, j), vars, arrays)+Expression.evaluate(expr.substring(j+1,expr.length()), vars, arrays);
    			return result;
    		} 
    		k.pop();
    	}
    	//SUBTRACT
    	
    	StringTokenizer st2 = new StringTokenizer(expr,delims,true);
    	k.push(st2.nextToken());
    	if (!k.peek().equals("-")&&!negative) {
    		while (st2.hasMoreTokens() ) {
    			k.push(st2.nextToken());
    		}
    		while (!k.isEmpty()) {
    			if(k.peek().equals("-")) {
    				j= Expression.find(expr, '-');
    				z= Expression.find2(expr, '-');
    	//IF SUBTRACTING A NEGATIVE NUMBER
    				if (expr.charAt(j-1) == '*'||expr.charAt(j-1) == '*'||expr.charAt(j-1) == '-') {
    					if (expr.charAt(z-1) == '-') {
    						z = z-1;
    						k.pop();
    						result = Expression.evaluate(expr.substring(0, z), vars, arrays)-Expression.evaluate(expr.substring(z+1,expr.length()), vars, arrays);
    						return result;
    					} else if(expr.charAt(z-1) =='*'||expr.charAt(z-1) == '/') {
    					k.pop();
    					} else {
    						result = Expression.evaluate(expr.substring(0, z), vars, arrays)-Expression.evaluate(expr.substring(z+1,expr.length()), vars, arrays);
    						return result;
    					}
    				
    				} else {
    					if (expr.charAt(j-1) == '-') {
    						j = j-1;
    						k.pop();
    						result = Expression.evaluate(expr.substring(0, z), vars, arrays)-Expression.evaluate(expr.substring(z+1,expr.length()), vars, arrays);
    						return result;
    					} else if(expr.charAt(j-1) =='*'||expr.charAt(j-1) == '/') {
        					k.pop();
        				} else {
        					result = Expression.evaluate(expr.substring(0, j), vars, arrays)-Expression.evaluate(expr.substring(j+1,expr.length()), vars, arrays);
            				return result;
        				}
    				}
    			}
    			k.pop();
    		}
    	} else {
    		k.pop();
    	}
    	//DIVISION
    	StringTokenizer st3 = new StringTokenizer(expr,delims,true);
    	while (st3.hasMoreTokens() ) {
    		k.push(st3.nextToken());
    	}
    	while (!k.isEmpty()) {
    		if(k.peek().equals("/")) {
    			j= Expression.find(expr, '/');
    			result = Expression.evaluate(expr.substring(0, j), vars, arrays)/Expression.evaluate(expr.substring(j+1,expr.length()), vars, arrays);
    			return result;
    		} 
    		k.pop();
    	}
    	//MULTIPLICATION
    	StringTokenizer st4 = new StringTokenizer(expr,delims,true);
    	while (st4.hasMoreTokens() ) {
    		k.push(st4.nextToken());
    	}
    	while (!k.isEmpty()) {
    		if(k.peek().equals("*")) {
    			j= Expression.find(expr, '*');
    			result = Expression.evaluate(expr.substring(0, j), vars, arrays)*Expression.evaluate(expr.substring(j+1,expr.length()), vars, arrays);
    			return result;
    		} 
    		k.pop();
    	}
    	//INTEGERS
    	StringTokenizer st5 = new StringTokenizer(expr,delims,true);
    	k.push(st5.nextToken());
    	//if negative
    	if (k.peek().equals("-")) {
    		k.pop();
    		while (st5.hasMoreTokens() ) {
        		k.push(st5.nextToken());
        	}
        	while (!k.isEmpty()) {
        		if(k.peek().matches("-?\\d+(\\.\\d+)?")) {
        			result = Float.parseFloat(k.pop())*-1;
        		} else if(k.peek().matches("[a-zA-Z]+")) {
        			int tgt = Expression.findVar(vars, k.peek());
        			result = vars.get(tgt).value*-1;
        			k.pop();
        		}
        	}
    	} 
    	//if positive
    	else {
    		while (st5.hasMoreTokens() ) {
        		k.push(st5.nextToken());
        	}
        	while (!k.isEmpty()) {
        		if(k.peek().matches("-?\\d+(\\.\\d+)?")) {
        			result = Float.parseFloat(k.pop());
        		} else if(k.peek().matches("[a-zA-Z]+")) {
        			int tgt = Expression.findVar(vars, k.peek());
        			result = vars.get(tgt).value;
        			k.pop();
        		}
        	}
    	}
    	
    	return result;
    	
    }
}
