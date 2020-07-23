package pixelvisu.seriation;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Sequence implements Serializable {
	ArrayList<Integer> data;
	String name ="";

	public Sequence(){
		data = new ArrayList<>();
	}
	public Sequence(int size) {
		data = new ArrayList<>();
		for(int i = 0; i< size;i++)
			data.add(0);
	}
	public Sequence(List<Integer> l) {

		data = new ArrayList<>();
		data.addAll(l);
		
	}
	
	public Sequence(Sequence s){
		data = new ArrayList<>();
		for (int i = 0; i< s.getLength();i++) {
			add(s.get(i));
		}
	}
	
	public Sequence(JSONArray a, int size, JSONObject nj) {
		String n = nj.optString("instance");
		this.name = n;
		
		
		// a is multidimensional so we have to first take the array at the index and 
				// then extract the wanted data ( at 0 or 1 )
				ArrayList<Float> data_hold = new ArrayList<>();
				// Storing data in holder float arraylist so we can adjust them to
				// x>=0 to 255 spectrum for all data// that is int color spectrum
				JSONArray hold ;
				for(int i = 0; i<size;i++) {
					hold = a.getJSONArray(i);
					data_hold.add(hold.getFloat(1));
				}
				
				float scale =0;
				// min and max value for scaling
				float min = getMin(data_hold);
				float max = getMax(data_hold);
				if(min ==max)scale =0;
				else
					scale = (255/(max-min));
				
				// adding scale data
				data = new ArrayList<>();
				for(int i = 0; i<size;i++) {
					hold = a.getJSONArray(i);
					data.add((int)((data_hold.get(i)-min)*scale));
				}
	}
	
	
	//// PIPELINE SIMILARITY MEASURES
	
	public double compare(Sequence other, String measure) {
		if(measure =="euclidean") return compareEuclid(other);
		if(measure =="maximum") return compareMaximum(other);
		if(measure =="weight") return compareWeight(other);
		if(measure =="trivial") return compare(this, other);
		if(measure =="manhattan") return compareManhattan(other);
		else
			System.err.println("Similarity Measure Does not exist");
			return -10;
	}
	
	
	
	public double compareEuclid(Sequence other) {
		float diff =0;
		for(int i = 0;i<getLength();i++) {
			diff+= (get(i)-other.get(i))*(get(i)-other.get(i));
		}
		return Math.sqrt(diff);
		
	}
	public double compareManhattan(Sequence other) {
		float diff =0;
		for(int i = 0;i<getLength();i++) {
			diff+= Math.abs(get(i)-other.get(i));
		}
		return diff;
		
	}
	
	public double compareWeight(Sequence other) {
		return getWeight()/other.getWeight();
	}
	
	public double compareMaximum(Sequence other) {
		return getMax()/other.getMax();
	}
	
	
	
	
	
	public String getName() {
		return this.name;
	}
	
	public void add(int d) {
		data.add(d);
	}
	
	public int get(int i) {
		return data.get(i);
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
		double weight = 0;
		for(int i = 0; i<getLength();i++) {
			weight+= get(i);
		}
		return (int) Math.abs(weight);
	}
	

	public double compare(Sequence a, Sequence b) {
		// calculates difference pattern and returns total value for sectioning
		// the bigger the difference the bigger the returned value
		return a.measureUncertain(b).getWeight()/25500;
		
//		return compareRecursive(a, b);
	}
	
	
	
	public double compareRecursive(Sequence a, Sequence b) {
		// calculates difference pattern and returns total value for sectioning
		if(a.getLength()==2) {
			//System.out.println(a.measureUncertain(b).getWeight());
			return a.measureUncertain(b).getWeight()/2;
		}
		if(a.getLength()==1) {
			return a.measureUncertain(b).getWeight()/1;
		}
		//increasing rate =
		double rate =(double)a.getLength()/(double)getLength();
		rate = rate*rate*rate*rate;
		
		List<Integer> la = new ArrayList<Integer>( a.data); 
		List<Integer> lb = new ArrayList<Integer>( b.data); 
		Sequence a1 = new Sequence(la.subList(0,(int)(la.size()/2)));
		Sequence a2 = new Sequence(la.subList((int)(la.size()/2),la.size()));

		Sequence b1 = new Sequence(lb.subList(0,(int)(lb.size()/2)));
		Sequence b2 = new Sequence(lb.subList((int)(lb.size()/2),lb.size()));
		return rate*(compareRecursive(a1, b1)+ compareRecursive(a2, b2));
		

	}
	
	public Sequence measureUncertain( Sequence b) {
		// measure Difference from Sequence a to b for visu
		Sequence diff = new Sequence();
		for(int j = 0;j<getLength();j++) {
			diff.add(measureDiff(get(j),b.get(j)));
		}
		return diff;
	}

	
	public int measureDiff(int a, int b) {
		double diff = a-b;
		return (int)(diff*diff/4);
	}
}
