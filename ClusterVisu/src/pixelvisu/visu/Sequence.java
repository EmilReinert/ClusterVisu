package pixelvisu.visu;

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
	final int sect =100; // for horizontal clustering of size 'sect'
	int sect_vis; // sections for 
	String name ="x";
	private int pos =0;
	double min=10000;
	double max=-10000;
	int weight;

	public Sequence(){
		data = new ArrayList<>();
//		timestamps = new ArrayList<>();
		weight =0;
	}
	public Sequence(int size) {
		// zero value
		data = new ArrayList<>();
//		timestamps = new ArrayList<>();
		for(int i = 0; i< size;i++)
			data.add(0.0);
		weight = 0;
	}
	public Sequence(int size, double val) {
		//one value
		data = new ArrayList<>();
//		timestamps = new ArrayList<>();
		for(int i = 0; i< size;i++)
			data.add(val);

		makeWeight();
	}
	
	public Sequence(int size, double val, String random) {
		//random
		data = new ArrayList<>();
//		timestamps = new ArrayList<>();
		for(int i = 0; i< size;i++)
			data.add(Math.random()*100);
		makeWeight();
	}
	
	public Sequence(Sequence s){
		pos = s.pos;
		weight = s.weight;
		name = s.name;
		data = new ArrayList<>(s.data);
//		timestamps = new ArrayList<>(s.timestamps);
		min = s.min;
		max = s.max;
	}

	public Sequence(JSONArray a, int size, JSONObject nj) {
		String n = nj.optString("instance");
		this.name = n;

		data = new ArrayList<>();
		
		// a is multidimensional so we have to first take the array at the index and 
		// then extract the wanted data ( at 0 or 1 )
		ArrayList<Float> data_hold = new ArrayList<>();
		// Storing data in holder float arraylist so we can adjust them to
		// x>=0 to 255 spectrum for all data// that is int color spectrum
		JSONArray hold ;
		for(int i = 0; i<size;i++) {
			hold = a.getJSONArray(i);
			data.add(hold.getDouble(1));
		}
		
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
			if(next) {
				data.add(Double.parseDouble(first));
//				timestamps.add(Double.parseDouble(second));
			}
		}
		min = getMin();
		max = getMax();

		makeWeight();
		//compress(10);
	}
	
	
	/// Compress
//	public void compress(int s) {
//		if(s == sect)return;
//		sect = s;
//		
//		for(int i =0; i<=data.size()-sect;i+=sect) {
//			double av = 0;
//			for(int j =0;j<sect;j++) av+=get(j+i)/sect;
//			for(int j =0;j<sect;j++) set(i+j, av);			
//		}
//		
//		
//	}
	
	
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
	
	public double compareAbsolute(Sequence other, int start,int end) {
		double w = Math.abs(other.getWeight(start, end)-getWeight(start,end));
		return w;
	}
	
	public double compareMaximum(Sequence other, int start,int end) {
		return Math.abs(other.getMax(start,end)-getMax(start,end));
	}
	
	public double compareMinkowski(Sequence other, int start, int end) {
		float diff =0; int r = 10;
		for(int i = start;i<data.size()&&i<end;i+=sect) {
			diff+= (Math.pow((get(i)-other.get(i)),r));
		}
		return Math.pow(diff,1/(float)r);
	}
	
	public double compareCosine(Sequence other, int start,int end) {
		double dot =0;
		for(int i =start;i<data.size()&&i<end;i+=sect) {
			dot+= get(i)*other.get(i);
		}
		dot =dot/(getMathLength(start, end)*other.getMathLength(start, end));
		dot = Math.acos(dot)/Math.PI;
		if(Double.isNaN(dot)) {
			return 0;
		}
//		System.out.println(dot);
		return dot;
	}
	 	
	public double compareRMS(Sequence other, int start,int end) {
		
		return compareEuclid(other, start, end)/(getLength()/sect);
		
	}
	
	
	
	
	
	
	public int getPos() {
		return this.pos;
	}
	

	private void makeWeight() {
		// TODO Auto-generated method stub
		weight = 0;
		for (double i: data)
		{
			weight+= i;
		}
	}
	
	public int getWeight() {
		return weight;
	}
	
	public double getWeight(int start, int end) {
		float sum = 0;
		for(int i = start; i<data.size()&&i<end;i+=sect) {
			sum+= get(i);
		}
		return sum;
		
	}
	public double getMathLength(int start, int end) {
		// return spacial length of data vector
		float sum =0;
		for(int i = start; i<data.size()&&i<end;i+=sect) {
			sum+=get(i)*get(i);
		}
		return Math.sqrt(sum);
	}

	private int getMax(int start, int end) {
		// TODO Auto-generated method stub
		double max = -100000;
		for(int i = start; i<data.size()&&i<end;i+=sect) {
			if(data.get(i)>max) {
				max = data.get(i);
			}
		}
		if(max ==-100000)
		{
			System.err.println("no max found");return 0;
		}
		return (int) max;
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
