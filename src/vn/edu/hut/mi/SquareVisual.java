package vn.edu.hut.mi;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;
/**
 * Square Visual
 * 
 * @author Son Han
 *
 */
@SuppressWarnings("serial")
public class SquareVisual extends JPanel{
	private int status = 0;
	private byte initSquare[][] = new byte[9][9];
	private byte dataSquare[][] = new byte[9][9];
	public SquareVisual(byte init[][]){
		this.initSquare = init;
		this.setBackground(Color.white);
	}
	
	public void setStatus(int status){this.status = status;}
	public int getStatus(){return status;}
	public void setInitData(byte data[][]){
		for(int i =0; i<9; i++)
			for(int j =0; j<9; j++){
				initSquare[i][j] = data[i][j];
			}
	}
	public void setData(byte data[][]){
		for(int i =0; i<9; i++)
			for(int j =0; j<9; j++){
				dataSquare[i][j] = data[i][j];
			}
	}
	public void paint(Graphics g){
		super.paint(g);
		
		g.setColor(Color.lightGray);
		for (int i = 0; i <= 9; i++){
			g.drawLine(40, 40 +40*i, 400, 40 +40*i);
		}
		for (int i = 0; i <= 9; i++){
			g.drawLine(40 +40*i, 40, 40 +40*i, 400);
		}
		g.setColor(Color.darkGray);
		for (int i = 0; i <= 3; i++){
			g.drawLine(40, 40 +120*i, 400, 40 +120*i);
		}
		for (int i = 0; i <= 3; i++){
			g.drawLine(40 +120*i, 40, 40 +120*i, 400);
		}
		g.setColor(Color.magenta);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		if (status == 1)
			for (int i = 0; i< 9; i++)
				for (int j = 0; j< 9; j++){
					if (initSquare[i][j] != 0)
						g.drawString(Byte.toString(initSquare[i][j]), 55 + 40*j, 70 + 40*i);
				}
		if (status == 2)
			for (int i = 0; i< 9; i++)
				for (int j = 0; j< 9; j++){
					if (initSquare[i][j] != 0){
						g.setColor(Color.magenta);
						g.drawString(Byte.toString(dataSquare[i][j]), 55 + 40*j, 70 + 40*i);
					}
					else{
						g.setColor(Color.gray);
						g.drawString(Byte.toString(dataSquare[i][j]), 55 + 40*j, 70 + 40*i);
					}
				}
	}

}
