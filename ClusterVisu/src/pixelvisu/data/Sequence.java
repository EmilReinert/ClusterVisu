package pixelvisu.data;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Sequence implements Serializable {
	ArrayList<Double> data;
//	ArrayList<Double> timestamps;
	int sect =60; // for horizontal clustering of size 'sect'
	int sect_vis; // sections for 
	String name ="x";
	private int pos =0;
	double min=10000;
	double max=-10000;

	public Sequence(){
		data = new ArrayList<>();
//		timestamps = new ArrayList<>();
	}
	public Sequence(int size) {
		// zero value
		data = new ArrayList<>();
//		timestamps = new ArrayList<>();
		for(int i = 0; i< size;i++)
			data.add(0.0);
	}
	public Sequence(int size, double val) {
		//one value
		data = new ArrayList<>();
//		timestamps = new ArrayList<>();
		for(int i = 0; i< size;i++)
			data.add(val);
	}
	
	public Sequence(int size, double val, String random) {
		//random
		data = new ArrayList<>();
//		timestamps = new ArrayList<>();
		for(int i = 0; i< size;i++)
			data.add(Math.random()*100);
	}
	
	public Sequence(Sequence s){
		pos = s.pos;
		name = s.name;
		data = new ArrayList<>(s.data);
//		timestamps = new ArrayList<>(s.timestamps);
		min = s.min;
		max = s.max;
	}
	
	
	public Sequence(String group, int pos) {
		// Takes full string arraylist and splits it into sequence data
		this();
		this.pos = pos;
		this.name = "Node_"+pos;
		String [] cuts = group.split(",");
		for(int i=1;i<cuts.length;i++ ) {
//			System.out.println(cuts[i]);
			String first ="", second="";
			boolean next = false;
			for (int j = 0; j < cuts[i].length(); j++){
			    char ch = cuts[i].charAt(j);
			    if(ch=='K'||ch=='L')next = true;
			    else 
			    	if(!next)first+=ch;
			    	else second +=ch;
			}
//			System.out.println(cuts[i]);
//			System.out.println(first+ " "+second);
			if(next) {
				data.add(Double.parseDouble(first));
//				timestamps.add(Double.parseDouble(second));
			}
		}
		min = getMin();
		max = getMax();
		
		//compress(10);
	}
	
	
	/// Compress
	public void compress(int s) {
		if(s == sect)return;
		sect = s;
		
		for(int i =0; i<=data.size()-sect;i+=sect) {
			double av = 0;
			for(int j =0;j<sect;j++) av+=get(j+i)/sect;
			for(int j =0;j<sect;j++) set(i+j, av);			
		}
		
		
	}
	
	
	//// PIPELINE SIMILARITY MEASURES
	
	
	public double compareEuclid(Sequence other, int start,int end) {
		float diff =0;
		for(int i = start;i<data.size()&&i<end;i+=sect) {
			diff+= (get(i)-other.get(i))*(get(i)-other.get(i));
		}
		return Math.sqrt(diff);
		
	}
	public double compareManhattan(Sequence other, int start,int end) {
		float diff =0;
		for(int i = start;i<getLength()&&i<end;i+=sect) {
			diff+= Math.abs(get(i)-other.get(i));
		}
		return diff;
		
	}
	
	public double compareWeight(Sequence other, int start,int end) {
		return Math.abs(other.getWeight(start, end)-getWeight(start,end));
	}
	
	public double compareMaximum(Sequence other, int start,int end) {
		return Math.abs(other.getMax(start,end)-getMax(start,end));
	}
	
	
	
	public int getPos() {
		return this.pos;
	}
	

	private int getWeight(int start, int end) {
		// TODO Auto-generated method stub
		return 0;
	}
	private int getMax(int start, int end) {
		// TODO Auto-generated method stub
		return 0;
	}
	public String getName() {
		return this.name;
	}
	
	public void add(double d) {
		if(d>max)max = d;
		if(d<min)min =d;
		data.add(d);
	}
	
	public double get(int i) {
		if(i>=data.size()||i<0)return -1;
		return data.get(i);
	}
//	public double getTime(int i) {
//		if(i>=timestamps.size()||i<0)return -1;
//		return timestamps.get(i);
//	}
	public void set(int i, double num) {
		if(i>=data.size()||i<0)return;
		data.set(i, num);
	}
	
	
	public double getContrast(int i) {
		if(i>= getLength()||i<0)return -3;
		if(max<min) {return -4;}
		if(max==min)return max;
		return (data.get(i)-min)*(255/(max-min));
	}
	
	public void multiplicate(int mul) {
		// multiplicates value onto each data element
		// for increasing sequence values in recursive pattern finding
		// maybe not
		for(int i = 0; i< getLength();i++) {
			data.set(i, data.get(i)*mul);
		}
	}
	
	public String toString() {
		return data.toString();
	}
	public int getLength() {
		return data.size();
	}
	
	
	
	
	public double getMin() {
		double min = 100000;
		for(int i = 0; i<data.size();i++) {
			if(data.get(i)<min) {
				min = data.get(i);
			}
		}
		if(min ==100000)
		{
			System.err.println("no min found");return 0;
		}
		return min;
	}
	
	public double getMax() {
		double max = -100000;
		for(int i = 0; i<data.size();i++) {
			if(data.get(i)>max) {
				max = data.get(i);
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
		return 0;
//		double weight = 0;
//		for(int i = 0; i<data.size();i++) {
//			weight+=data.get(i);
//		}
//		return (int) Math.abs(weight);
	}
	

	
	
	
	public double compare(Sequence a, Sequence b) {
		// calculates difference pattern and returns total value for sectioning
		// the bigger the difference the bigger the returned value
		return a.measureUncertain(b).getWeight()/25500;
		
//		return compareRecursive(a, b);
	}
	
	public Sequence measureUncertain( Sequence b) {
		// measure Difference from Sequence a to b for visu
		Sequence diff = new Sequence();
		for(int j = 0;j<getLength();j++) {
			diff.add(measureDiff(get(j),b.get(j)));
		}
		return diff;
	}

	
	public int measureDiff(double a, double b) {
		double diff = a-b;
		return (int)(diff*diff/4);
	}
}
