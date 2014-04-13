/**
 * Sudoku solver
 * A very fast human-like algorithm
 * Input: file for initial numbers
 * 
 * Algorithm:
 * - Start with a 9x9 grid with initial (given) numbers and 0 for missing numbers
 * - An element is a row, a column, or a 3x3 block
 * - A minimal element is defined as the element with least 0 (missing number)
 * - Loop until the grid is full
 * 		+ Find a minimum element
 * 		+ For each of 0 squares, fill with one of the remaining numbers
 * 		+ Check related elements, continue
 * - End
 * @author	Son N. Han
 * @date	2005/09/15
 * 			Hanoi University of Technology
 */
package vn.edu.hut.mi;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.text.DecimalFormat;
import java.util.Calendar;
import javax.swing.*;

@SuppressWarnings("serial")
public class Sodokure extends JPanel {
	private byte rootSquare[][] = {
			{0,0,2,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,8,0},
			{0,0,0,1,0,6,9,7,4},
			{0,7,0,0,0,5,0,0,1},
			{0,2,6,3,9,1,8,4,0},
			{4,0,0,8,0,0,0,5,0},
			{1,8,4,9,0,3,5,0,0},
			{0,9,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,4,0,0}
	};
	private byte initSquare[][] = new byte[9][9];/* {
			{2,0,0,0,0,0,0,0,0},
			{0,0,5,7,8,0,0,0,3},
			{0,7,0,2,0,4,0,6,0},
			{8,2,0,0,1,3,0,0,0},
			{0,0,9,0,0,0,4,0,0},
			{0,0,0,4,7,0,0,8,2},
			{0,6,0,3,0,2,0,1,0},
			{3,0,0,0,4,9,7,0,0},
			{0,0,0,0,0,0,0,0,4}
	};*/
	private byte resultSquare[][][] = new byte[100][9][9]; // storing maximum 100 solutions
	private byte dataSquare[][] = new byte[9][9];
	
	JButton okButton = new JButton("Run",new ImageIcon("res/mProcess.gif"));
	JButton resetButton = new JButton("Reset", new ImageIcon("res/Ignore16.gif"));
	JButton nextButton = new JButton(new ImageIcon("res/Next.gif"));
	JButton preButton = new JButton(new ImageIcon("res/Pre.gif"));
	
	JMenuBar menuBar = new JMenuBar();
	JMenu menuFile = new JMenu("File");
	JMenuItem newItem = new JMenuItem("Demo Puzzle",new ImageIcon("res/New16.gif"));
	JMenuItem openItem = new JMenuItem("Open a Sudoku Puzzle", new ImageIcon("res/Open16.gif"));
	JMenuItem exitItem = new JMenuItem("Exit", new ImageIcon("res/End16.gif"));
	
	JMenu helpMenu = new JMenu("Help");
	JMenuItem aboutItem = new JMenuItem("About", new ImageIcon("res/About16.gif"));
	JMenuItem infoItem = new JMenuItem("Solutions Info", new ImageIcon("res/Info16.gif"));
	
	private SquareVisual squareVisual = new SquareVisual(rootSquare);
	
	private JPanel controlPanel = new JPanel();
	private JLabel info = new JLabel("9x9 Square");
	
	private long duration;
	private int countStep = 0;
	private int countResult = 0;
	private int cursor;
	private boolean running = false;
	private int value;
	Timer timer;
	private SwingWorker worker;
	JProgressBar progressBar = new JProgressBar(0,100);
	DecimalFormat format = new DecimalFormat("0.00");
			
	public Sodokure(){
		
		controlPanel.add(resetButton);
		controlPanel.add(okButton);
		controlPanel.add(preButton);
		controlPanel.add(nextButton);
		controlPanel.add(progressBar);
				
		progressBar.setStringPainted(true);
		nextButton.setEnabled(false);
		preButton.setEnabled(false);
		squareVisual.add(info);
		
		this.setLayout(new BorderLayout());
		this.add(controlPanel, BorderLayout.SOUTH);
		this.add(squareVisual,BorderLayout.CENTER);
		
		hookupEvents();
	}
	private JMenuBar createMenu() {
		MenuAction menuAction = new MenuAction();
	    newItem.addActionListener(menuAction);
		openItem.addActionListener(menuAction);
	    exitItem.addActionListener(menuAction);
	    aboutItem.addActionListener(menuAction);
	    infoItem.addActionListener(menuAction);
	    infoItem.setEnabled(false);
	       
	    menuBar.add(menuFile);
	    menuBar.add(helpMenu);
	    
	    menuFile.add(newItem);
	    menuFile.add(openItem);
	    menuFile.addSeparator();
	    menuFile.add(exitItem);
	    
	    helpMenu.add(aboutItem);
	    helpMenu.add(infoItem);
	    return menuBar;
	}
	private void hookupEvents() {
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				doIt();
			}
		});
		resetButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (running){
					if (worker != null) worker.interrupt();
					running = false;
				}
				else {
					 squareVisual.setStatus(0);
					 squareVisual.repaint();
					 info.setText("9x9 Square");
					 nextButton.setEnabled(false);
					 preButton.setEnabled(false);
					 value = 0;
					 progressBar.setValue(value);
					 progressBar.setString("");
				}
			}
		});
		nextButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				cursor++;
				if (cursor == 99 | cursor == countResult-1)
					nextButton.setEnabled(false);
				if (cursor == 1) preButton.setEnabled(true);				
				showResult(cursor);
			}
		});
		preButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				cursor--;
				if (cursor == 98 | cursor == countResult - 1) 
					nextButton.setEnabled(true);
				if (cursor == 0) {
					preButton.setEnabled(false);
					nextButton.setEnabled(true);
				}
				//if (cursor == countResult-2) nextButton.setEnabled(true); 
				showResult(cursor);
			}
		});
		timer = new Timer(100, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (running){
					value++;
					progressBar.setValue(value % 100);
					progressBar.setString(format.format(value/10.0) + " secs");
				}
				else{
					timer.stop();
					okButton.setEnabled(true);
				}
			}
		});
	}
	public static void createAndShowGUI(){
		Sodokure ms = new Sodokure();
		JFrame f = new JFrame("Sudokure");
//		f.setDefaultLookAndFeelDecorated(true);
		f.setContentPane(ms);
		f.setJMenuBar(ms.createMenu());
		f.setIconImage(new ImageIcon("res/logohfc.gif").getImage());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(460,550);
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		f.setLocation(((ss.width - f.getWidth())/2),(ss.height - f.getHeight())/2);
		
		f.setVisible(true);
	}
	protected void doIt() {
		if (squareVisual.getStatus() != 1){
			JOptionPane.showMessageDialog(null, "No initial numbers");
			return;
		}
		okButton.setEnabled(false);
		value = 0;
		running = true;
		timer.start();
		worker = new SwingWorker() {
			public Object construct() {
				startSearch();
				running = false;
		       	return this;
		    }
		};
		worker.start();		
	}
//	*/
	public void startSearch(){
		copy(dataSquare, initSquare);
		countStep = countResult = countStep = cursor = 0;
		Calendar beforeTime = Calendar.getInstance();
		fillSquare();
		Calendar afterTime = Calendar.getInstance();
		//dialog.hide();
		infoItem.setEnabled(true);
		if (countResult == 0) {info.setText("No solution found!");return;}
		if (countResult > 1) nextButton.setEnabled(true);
		duration = (afterTime.getTimeInMillis() - beforeTime.getTimeInMillis());
		showResult(0);
	}
	private class MenuAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if (e.getSource() == newItem ){
				copy(initSquare,rootSquare);
				squareVisual.setStatus(1);
				squareVisual.repaint();
				info.setText("Initial Square (Demo)");
				nextButton.setEnabled(false);
				preButton.setEnabled(false);
			}
			if (e.getSource() == openItem ){
				if (openFile()){
					squareVisual.setStatus(1);
					squareVisual.setInitData(initSquare);
					squareVisual.repaint();
					info.setText("Initial Square");
					
					nextButton.setEnabled(false);
					preButton.setEnabled(false);
				}
			}
			if (e.getSource() == exitItem ){
				System.exit(0);
			}
			if (e.getSource() == infoItem){
				String message = "Solutions: " + countResult + " found\n\t"
						+ "Time: " + duration + " ms\n\t"
						+ "Tries: " + countStep;
				JOptionPane.showMessageDialog(null,message, "Info", JOptionPane.INFORMATION_MESSAGE);
			}
			if (e.getSource() == aboutItem){
				String message = "SUDOKURE\n"
						+ "A very fast human-like sudoku solver\n\n"		
						+ "Son N. Han (Hàn Ng\u1ECDc S\u01A1n) \n"
						+ "Hanoi University of Technology\n"
						+ "Copyright 9/2005";
				ImageIcon icon =  new ImageIcon("res/logohfc.gif");
				JOptionPane.showMessageDialog(null,message, "About", JOptionPane.INFORMATION_MESSAGE, icon );
			}
		}
	}
	
	public boolean openFile(){
		JFileChooser fileChooser = new JFileChooser("*.txt");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION) return false;
		File file = fileChooser.getSelectedFile();
		if (file == null) return false;
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
			BufferedReader br=new BufferedReader(isr);
			for (int i =0; i<9; i++){
				for (int j=0; j <9; j++){
					//s = br.readLine();
					byte ee = (byte) (br.read()-48);
					initSquare[i][j] = ee;
					br.read();
				}
				br.read();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();return false;
		} catch (IOException e) {
			e.printStackTrace();return false;
		} 
		return true;
	}

	private void showResult(int i){
		info.setText("Solution " + (i+1) + "/" + countResult);
		squareVisual.setStatus(2);
		squareVisual.setData(resultSquare[i]);
		squareVisual.repaint();
		
	}
	
//	 These following method used for matrix process	
	public void fillSquare(){
		//if (countStep > 100000) return;
		if (full()){
			if (countResult > 99){countResult++;return;}
			copy(resultSquare[countResult++],dataSquare);
			return;
		}
		else {
			ElementInfo minElement = new ElementInfo();
			minElement = checkSquare();
			minElement.createCombination();
			byte t[][] = new byte[9][9];
			copy(t,dataSquare);
			for(int i =0; i<minElement.combiNo; i++){
				put(minElement,i);
				// System.out.println("Step " + ++countStep); printResult();
				++countStep;
				if (checkCondition(minElement)) 	fillSquare();
				copy(dataSquare,t);
			}
			return;
		}
	}
	private boolean full(){
		for (int i = 0; i<9; i++)
			for (int j = 1; j<9; j++) if (dataSquare[i][j] == 0) return false;
		return true;
	}
	private void printResult(){
		for (int i = 0; i<9; i++){
			for (int j = 0; j<9; j++)
				System.out.print(" " + dataSquare[i][j]);
			System.out.println();
		}
	}
	private boolean checkConditionRow(byte k){
		for(int i =0; i<9; i++){
			if (dataSquare[k][i] != 0)
			for(int j = i + 1; j<9; j++){
				if (dataSquare[k][i] == dataSquare[k][j]) return false;
			}
		}
		return true;
	}
	private boolean checkConditionCol(byte k){
		for(int i =0; i<9; i++){
			if (dataSquare[i][k] != 0)
			for(int j = i + 1; j<9; j++){
				if (dataSquare[i][k]== dataSquare[j][k]) return false;
			}
		}
		return true;
	}
	private boolean checkCondition3x3(byte k){
		int r = 3*(k/3);
		int c = 3*(k%3);
		byte data[]=new byte[9];
		for(int i =0; i<3; i++)
			for(int j =0; j<3; j++) data[3*i+j] = dataSquare[r+i][c+j];
		for(int i =0; i<9; i++){
			if (data[i]!= 0)
			for(int j = i + 1; j<9; j++){
				if (data[i]== data[j]) return false;
			}
		}
		return true;
	}
	public boolean checkCondition(ElementInfo e){
		boolean result = true;
		switch (e.type){
			case 0:
				for (byte i = 0; i<3; i++)
					result = result && checkCondition3x3((byte) (3*(e.id/3) + i));
				for (byte j = 0; j<9; j++)
					result = result && checkConditionCol(j);
				break;
			case 1:
				for (byte i = 0; i<3; i++)
					result = result && checkCondition3x3((byte) (3*i + e.id/3));
				for (byte j = 0; j<9; j++)
					result = result && checkConditionRow(j);
				break;
			case 2:
				int r = 3*(e.id/3);
				int c = 3*(e.id%3);
				for (byte i = 0; i<3; i++)
					result = result && checkConditionRow((byte) (r + i));
				for (byte j = 0; j<3; j++)
					result = result && checkConditionCol((byte) (c + j));
				break;
		}
		
		return result;
	}
	private ElementInfo checkRow(byte k){
		byte data[]=new byte[9];
		for(int i =0; i<9; i++) data[i] = dataSquare[k][i];
		ElementInfo info = new ElementInfo(data);
		info.type = 0;
		info.id = k;
		return info;
		
	}
	private ElementInfo checkCol(byte k){
		byte data[]=new byte[9];
		for(int i =0; i<9; i++) data[i] = dataSquare[i][k];
		ElementInfo info = new ElementInfo(data);
		info.type = 1;
		info.id = k;
		return info;
		
	}
	private ElementInfo check3x3Square(byte k){
		int r = 3*(k/3);
		int c = 3*(k%3);
		byte data[]=new byte[9];
		for(int i =0; i<3; i++)
			for(int j =0; j<3; j++) data[3*i+j] = dataSquare[r+i][c+j];
		ElementInfo info = new ElementInfo(data);
		info.type = 2;
		info.id = k;
		return info;
		
	}
	
	private ElementInfo checkSquare(){
		ElementInfo info = new ElementInfo();
		ElementInfo temp = new ElementInfo();
		info.digitNo = 0;
		//info = checkRow((byte) 0);
		for(byte i =0; i<9; i++){
			temp = checkRow(i);
			if ((info.digitNo == 0)) {if(temp.digitNo > 0) info = temp;}
			else if ((temp.digitNo < info.digitNo) && (temp.digitNo > 0))
				info = temp;
		}
		for(byte i =0; i<9; i++){
			temp = checkCol(i);
			if ((info.digitNo == 0)) {if(temp.digitNo > 0) info = temp;}
			else if ((temp.digitNo < info.digitNo) && (temp.digitNo > 0))
				info = temp;
		}
		for(byte i =0; i<9; i++){
			temp = check3x3Square(i);
			if ((info.digitNo == 0)) {if(temp.digitNo > 0) info = temp;}
			else if ((temp.digitNo < info.digitNo) && (temp.digitNo > 0))
				info = temp;
		}
		return info;
	}
	public void put(ElementInfo e, int k){
		int countStep = 0;
		int id = e.id;
		switch (e.type){
			case 0:
				for(int i =0; i<9; i++){
					if (dataSquare[id][i]==0) dataSquare[id][i] = (byte) e.combination[k].digits[countStep++];
				}
				break;
			case 1:
				for(int i =0; i<9; i++){
					if (dataSquare[i][id]==0) dataSquare[i][id] = e.combination[k].digits[countStep++];
				}
				break;
			case 2:
				int r = 3*(id/3);
				int c = 3*(id%3);
				for(int i =0; i<3; i++)
					for(int j =0; j<3; j++) 
						if (dataSquare[r+i][c+j]==0) dataSquare[r+i][c+j] = e.combination[k].digits[countStep++]; 
				break;
		}
	}
	public void copy(byte dst[][], byte src[][]){
		for(int i =0; i<9; i++)
			for(int j =0; j<9; j++){
				dst[i][j] = src[i][j];
			}
	}
	
	public static void main(String args[]){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
}
