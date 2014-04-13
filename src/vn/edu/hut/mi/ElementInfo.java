package vn.edu.hut.mi;

public class ElementInfo {
	public byte id;
	public byte type; 
	//0->8 for rows, 9->17 for columns, 18->26 for 3x3 squares
	public byte data[] = new byte[9];
	public byte remainDigits[] = new byte[9];
	public byte digitNo = 0;
	public int combiNo;
	public Combination combination[];// = new combinationbination[30];

	private boolean notUsed[] = new boolean[9];
	private byte result[] = new byte[9];
		
	public ElementInfo(){}
	public ElementInfo(byte dataE[]){
		for (int i=0; i<9; i++) this.data[i] = dataE[i];
		digitNo = 0;
		boolean remain;
		for (byte i = 1; i<=9; i++){
			remain = true;
			for (byte j=0; j<9; j++)
				if (data[j]==i) {remain = false;}
			if (remain) remainDigits[digitNo++] = i;
		}	
		
	}
	public void createCombination(){
		int gt = 1;
		for (int i = 1; i<=digitNo; i++)
			gt = gt*i;
		combination = new Combination[gt]; 
		for (int i=0;i<digitNo;i++) notUsed[i] = true;
		combiNo = 0;
		listCom(digitNo -1);
	}
	public void listCom(int k){
		if (k == -1) {
			//combiNo++;
			combination[combiNo++] = new Combination(result,digitNo);
			//com[combiNo++].setValue(result,digitNo);
			return;
		}
		for (int i = 0; i< digitNo; i++){
			if (notUsed[i]){
				result[k]=remainDigits[i];
				notUsed[i] = false;
				listCom(k-1);
				notUsed[i] = true;
			}
		}
		//return;
	}
	public static void main(String args[]){
		byte atest[]={0,7,2,0,0,5,0,0,1};
		ElementInfo ei = new ElementInfo(atest);
		ei.createCombination();
		System.out.print("Finish");
	}
}
