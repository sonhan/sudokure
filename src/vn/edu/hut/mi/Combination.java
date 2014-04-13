package vn.edu.hut.mi;
/**
 * A combination of q digits
 * 
 * @author Son Han
 *
 */
public class Combination {
	private int q;
	public byte digits[];
	public Combination(){}
	public Combination(byte [] result, int n){
		q = n;
		digits = new byte[q];
		for (int i=0; i<n; i++) digits[i] = result[i];
	}
}
