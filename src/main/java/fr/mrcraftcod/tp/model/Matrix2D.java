package fr.mrcraftcod.tp.model;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-03-22.
 *
 * @author Thomas Couchoud
 * @since 2019-03-22
 */
public class Matrix2D{
	private final double[][] data;
	private final int s1;
	private final int s2;
	
	public Matrix2D(int s1, int s2){
		this.data = new double[s1][s2];
		this.s1 = s1;
		this.s2 = s2;
	}
	
	public void put(int i, int j, double cost){
		if(i < 0 || i >= this.getS1()){
			throw new IndexOutOfBoundsException();
		}
		if(j < 0 || j >= this.getS2()){
			throw new IndexOutOfBoundsException();
		}
		this.data[i][j] = cost;
	}
	
	public double get(int i, int j){
		if(i < 0 || i >= this.getS1()){
			throw new IndexOutOfBoundsException();
		}
		if(j < 0 || j >= this.getS2()){
			throw new IndexOutOfBoundsException();
		}
		return this.data[i][j];
	}
	
	public int getS1(){
		return s1;
	}
	
	public int getS2(){
		return s2;
	}
}
