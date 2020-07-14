package pixelvisu.seg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class Sequence {
	ArrayList<Integer> data;

	public Sequence(){
		data = new ArrayList<>();
	}
	public Sequence(int size) {
		data = new ArrayList<>();
		for(int i = 0; i< size;i++)
			data.add(0);
	}
	
	public Sequence(Sequence s){
		data = new ArrayList<>();
		for (int i = 0; i< s.getLength();i++) {
			add(s.get(i));
		}
	}
	
	public void add(int d) {
		data.add(d);
	}
	
	public int get(int i) {
		return data.get(i);
	}
	
	public String toString() {
		return data.toString();
	}
	public int getLength() {
		return data.size();
	}

	public int getMin() {
		int min = 100000;
		for(int i = 0; i<getLength();i++) {
			if(get(i)<min) {
				min = get(i);
			}
		}
		if(min ==100000)
		{
			System.err.println("no min found");return 0;
		}
		return min;
	}
	
	public int getMax() {
		int max = -100000;
		for(int i = 0; i<getLength();i++) {
			if(get(i)>max) {
				max = get(i);
			}
		}
		if(max ==-100000)
		{
			System.err.println("no max found");return 0;
		}
		return max;
	}
	
	//Math
	public int getWeight() {
		double weight = 0;
		for(int i = 0; i<getLength();i++) {
			weight+= get(i)/1000000;
		}
		return (int) Math.abs(weight);
	}
	
	public float compareAverage(Sequence other) {
		// compares 2 sequences by measuring the average difference values 
		return 0;
	}
	
	public int getDiff(Sequence b) {
		// calculates difference pattern and returns total value
		return measureDiff(b).getWeight();
	}
	
	public Sequence measureDiff( Sequence b) {
		Sequence diff = new Sequence();
		for(int j = 0;j<getLength();j++) {
			diff.add(measureDiff(get(j),b.get(j)));
		}
		return diff;
	}

	public int measureDiff(int [] data) {
		// iterates over sequence and returns the average difference value
		Arrays.parallelSort(data);
		double diff = 0;
		double av = 0;//average value
		for(int i = 0; i<data.length; i++) {
			av+= Math.abs((data[i]/100000));
		}
		av = av/data.length;
		for(int i = 0; i<data.length; i++) {
			diff+= Math.abs((data[i]/100000)-av);
		}
		return (int)(1000000*diff*diff/data.length);
	}
	
	public int measureDiff(int a, int b) {
		double diff = Math.abs(a-b)/100000;
		return (int)(1000000*diff*diff/2);
	}
}
