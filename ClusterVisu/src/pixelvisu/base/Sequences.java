package pixelvisu.base;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Sequences {
	ArrayList<Sequence> sequences ; // already ordered list of sequences
	ArrayList<Integer> weights;
	int group_count =-10;
	
	public void makeWeights() {
		weights = new ArrayList<>();
		for(int i = 0; i<sequences.size();i++) {
			weights.add(sequences.get(i).getWeight());
		}
	}
	
	public void swap(int a, int b) {
		Collections.swap(sequences, a, b); 
	}
	
	public ArrayList<Sequence> copySeqs(ArrayList<Sequence> ss) {
		ArrayList<Sequence> ssc = new ArrayList<Sequence>();
		for(int i = 0; i< ss.size();i++) {
			ssc.add(new Sequence(ss.get(i)));
		}
		return ssc;
	}

	public Sequence get(int i) {
		if(sequences.size()>0)
			return sequences.get(i);
		return null;
	}

	public void add(Sequence d) {
		sequences.add(d);
		weights.add(d.getWeight());
	}
	
	public int getDepth() {
		return sequences.size();
	}
	public int getLength() {
		return sequences.get(0).getLength();
	}
	public abstract int getSeqValue(int i);
	
	
	
	
	public int getMaxWeight() {
		int max =-500000; 
		for(int i = 0; i<getDepth();i++) {
			for(int j = i; j<getDepth();j++) {
				if( max<sequences.get(j).getWeight()) {
					max = sequences.get(j).getWeight();
					}
			}
		}
		return max;
	}
	public int getMinWeight() {
		int max =500000; 
		for(int i = 0; i<getDepth();i++) {
			for(int j = i; j<getDepth();j++) {
				if( max>sequences.get(j).getWeight()) {
					max = sequences.get(j).getWeight();
					}
			}
		}
		return max;
	}

}
