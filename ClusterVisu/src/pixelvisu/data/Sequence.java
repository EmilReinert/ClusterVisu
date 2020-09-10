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
	ArrayList<Double> original;
	int sect =60; // for horizontal clustering of size 'sect'
	int sect_vis; // sections for 
	String name ="";
	int pos =0;
	double min=10000;
	double max=-10000;

	public Sequence(){
		data = new ArrayList<>();
	}
	public Sequence(int size) {
		// zero value
		data = new ArrayList<>();
		for(int i = 0; i< size;i++)
			data.add(0.0);
	}
	public Sequence(int size, double val) {
		//one value
		data = new ArrayList<>();
		for(int i = 0; i< size;i++)
			data.add(val);
	}
	
	public Sequence(int size, double val, String random) {
		//random
		data = new ArrayList<>();
		for(int i = 0; i< size;i++)
			data.add(Math.random()*100);
	}
	
	public Sequence(Sequence s){
		pos = s.pos;
		data = new ArrayList<>(s.data);
		min = s.min;
		max = s.max;
		
	}
	
	public Sequence(JSONArray a, int size, JSONObject nj, int pos) {
		this.pos = pos;
		String n = nj.optString("instance");
		this.name = n;
		
		// a is multidimensional so we have to first take the array at the index and 
		// then extract the wanted data ( at 0 or 1 )
		ArrayList<Float> data_hold = new ArrayList<>();
		
		// Storing data in holder float arraylist so we can adjust them to
		// x>=0 to 255 spectrum for all data// that is int color spectrum
		JSONArray hold ;
		for(int i = 0; i<size;i++) {
			if(i<a.length()) { 
				hold = a.getJSONArray(i);
				data_hold.add(hold.getFloat(1));
			}
			else data_hold.add(0.0f);
		}

		data = new ArrayList<>();
		
		float scale =0;
		// min and max value for scaling
		min = getMin(data_hold);
		max = getMax(data_hold);
		
		for(float f: data_hold) {
			data.add((double)f);
		}
		compress(10);
	}
	public Sequence(String group, int pos) {
		// Takes full string arraylist and splits it into sequence data
		this();
		this.pos = pos;
		String [] cuts = group.split(",");
		System.out.println(cuts.length);
		for(int i=1;i<cuts.length;i++ ) {
			data.add(Double.parseDouble(cuts[i].substring(0, 10)));
		}
		min = getMin();
		max = getMax();
	}
	/// Compress
	public void compress(int s) {
		if(s == sect)return;
		sect = s;
		
		for(int i =0; i<=data.size()-sect;i+=sect) {
			double av = 0;
			for(int j =0;j<sect;j++) av+=data.get(j+i)/sect;
			for(int j =0;j<sect;j++) data.set(i+j, av);			
		}
		
		
	}
	
	
	//// PIPELINE SIMILARITY MEASURES
	
	public double compare(Sequence other, String measure) {
		if(measure =="euclidean") return compareEuclid(other);
		if(measure =="maximum") return compareMaximum(other);
		if(measure =="weight") return compareWeight(other);
		if(measure =="manhattan") return compareManhattan(other);
		else
			System.err.println("Similarity Measure Does not exist");
			return -10;
	}
	
	
	
	public double compareEuclid(Sequence other) {
		float diff =0;
		for(int i = 0;i<data.size();i+=sect) {
			diff+= (get(i)-other.get(i))*(get(i)-other.get(i));
		}
		return Math.sqrt(diff);
		
	}
	public double compareManhattan(Sequence other) {
		float diff =0;
		for(int i = 0;i<getLength();i+=sect) {
			diff+= Math.abs(get(i)-other.get(i));
		}
		return diff;
		
	}
	
	public double compareWeight(Sequence other) {
		return Math.abs(other.getWeight()-getWeight());
	}
	
	public double compareMaximum(Sequence other) {
		return Math.abs(other.getMax()-getMax());
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
	public float getMin(ArrayList<Float> a) {
		float min = 100000;
		for(int i = 0; i<a.size();i++) {
			if(a.get(i)<min) {
				min = a.get(i);
			}
		}
		if(min ==100000)
		{
			System.err.println("no min found");return 0;
		}
		return min;
	}
	
	public float getMax(ArrayList<Float> a) {
		float max = -100000;
		for(int i = 0; i<a.size();i++) {
			if(a.get(i)>max) {
				max = a.get(i);
			}
		}
		if(max ==-100000)
		{
			System.err.println("no max found");return 0;
		}
		return max;
	}

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
