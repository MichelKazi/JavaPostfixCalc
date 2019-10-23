package project2;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import generics.*;



@SuppressWarnings("serial")
class CalculatorFrame extends JFrame implements ActionListener  {
	JTextField jtfInfix = new JTextField(21); // for infix 
	JTextField jtfPostfix = new JTextField();  // for postix
	JTextField result = new JTextField("0");   // for result
	
	JButton[][] calcButton = new JButton[4][5];
	
	JPanel calcPanel = new JPanel();	
	JPanel topPanel = new JPanel();    

	
	public CalculatorFrame(){
		String[][] buttonText = 
				new String[][]{{"7","8","9","/","C"},{"4","5","6","*","B"},
				{"1","2","3","-","R"},{"0","(",")","+","="}};
				
		this.setTitle("CSCI212 Calculator");
		this.setLayout(new BorderLayout(2,1));

		jtfInfix.setHorizontalAlignment(JTextField.RIGHT);
		jtfPostfix.setHorizontalAlignment(JTextField.RIGHT);
		result.setHorizontalAlignment(JTextField.RIGHT);
		jtfPostfix.setEnabled(false);
		result.setEnabled(false);
		//jtfInfix.setEditable(false); // hide text caret
		
		// set the font size to 34 for the text fields
		Font textFieldFont=new Font(jtfPostfix.getFont().getName(),jtfPostfix.getFont().getStyle(),24);
		jtfInfix.setFont(textFieldFont);
		jtfPostfix.setFont(textFieldFont);
		result.setFont(textFieldFont);
		
		topPanel.setLayout(new GridLayout(3,1));				
		topPanel.add(jtfInfix);		
		topPanel.add(jtfPostfix);
		topPanel.add(result);
		
		calcPanel.setLayout(new GridLayout(4,5,3,3));
		
		for (int i = 0; i < 4; i++) {
			for(int j = 0; j < 5; j++) {
				calcButton[i][j]= new JButton("" + buttonText[i][j]);
				calcButton[i][j].setForeground(Color.blue);
				calcButton[i][j].setFont(new Font("sansserif",Font.BOLD,42));
				calcButton[i][j].addActionListener(this);
				calcButton[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
				calcPanel.add(calcButton[i][j]);
			}
		}
		this.add(topPanel,BorderLayout.NORTH);
		this.add(calcPanel,BorderLayout.CENTER);
	}
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < 4; i++) {
			for(int j = 0; j < 5; j++) {				
				if(e.getSource() == calcButton[i][j]){
					// clear
					if(i==0 && j == 4){
						jtfInfix.setText(null);
						jtfPostfix.setText(null);
						result.setText("0");
					}
					// backspace
					else if(i==1 && j == 4){
						if(jtfInfix.getDocument().getLength()>0)
							try {
								jtfInfix.setText(jtfInfix.getText(0, jtfInfix.getDocument().getLength()-1));
							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}
						
					}
					// number or operator
					else if(j<4){
						jtfInfix.setText(jtfInfix.getText()
							+ calcButton[i][j].getText());
						}
					// = button pressed
					else if(i==3&&j==4){
						// erase contents of the postfix textfield
						jtfPostfix.setText(null);  
						// update the postfix textfield with the String returned  
						try {
							jtfPostfix.setText(infixToPostfix());
						} catch (StackEmptyException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// update the result textfield with the result of the computation
						try {
							result.setText("" + calculate());
						} catch (StackEmptyException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		}
    }
	public boolean isNumber(String str)  
	{  
	  try  
	  {  
	    @SuppressWarnings("unused")
		int i = Integer.parseInt(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	public int precedence (String str) {
		switch (str) {
		case "+" : return 0;
		case "-" : return 0;
		case "*" : return 1;
		case "/" : return 1;
		}
		return -1;
	}
	public String infixToPostfix() throws StackEmptyException{		
		Stack <String> s = new Stack<> ();
		
		String expression = jtfInfix.getText();
		String delims = "+-*/() ";
		StringTokenizer strToken = new StringTokenizer(expression, delims, true);
		String postFix = "";
		while (strToken.hasMoreTokens()) {
			String token = strToken.nextToken();
			//switch case 1-6 here
		
			if (isNumber(token)) {
				postFix+=token + " ";
			}
			if(token.equals("(")) {
				s.push(token);
			}
			if(token.equals(")")) {
				while (!s.peek().equals("(")) {
					postFix += s.pop() + " ";	
				}
				s.pop();
			}
			if (token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-")) {
				System.out.println(token);
				while (!s.isEmpty() && (precedence(token) <= precedence(s.peek()))) {
					postFix+=s.pop();
				}
				s.push(token);	
			}
		}
		// code END here (While stack isn't empty)
		while (!s.isEmpty()) {
			postFix += s.pop() + " ";
		}
		
		return postFix;
	}
	public String calculate() throws StackEmptyException {
		Stack <String> s = new Stack<>();
		String expression = jtfPostfix.getText();
		String delims = "+-*/() ";
		
		StringTokenizer strToken = new StringTokenizer(expression, delims, true);
		while (strToken.hasMoreTokens()) {
			String token = strToken.nextToken();
			int eval = 0;
			if (isNumber(token)) {
				s.push(token);
			}
			else if (!s.isEmpty()&&(token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-"))) {
				String r = s.pop();
				String l = s.pop();
				
					if (token.equals("+"))
						eval = Integer.parseInt(l) + Integer.parseInt(r);
					else if (token.equals("-"))
						eval = Integer.parseInt(l) - Integer.parseInt(r);
					else if (token.equals("*"))
						eval = Integer.parseInt(l) * Integer.parseInt(r);
					else if (token.equals("/"))
						eval = Integer.parseInt(l) / Integer.parseInt(r);
				s.push(Integer.toString(eval));	
				//System.out.println(eval); //for debugging
			}
		}
		return s.peek();
	}
	
	static final int MAX_WIDTH = 398, MAX_HEIGHT = 440;
	
	public static void main(String arg[]){
		JFrame frame = new CalculatorFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(MAX_WIDTH,MAX_HEIGHT);	
		frame.setBackground(Color.white);		
		frame.setResizable(false);				
		frame.setVisible(true);
	}
}