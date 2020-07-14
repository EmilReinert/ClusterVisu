package pixelvisu.dump;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	public int get(int row, int idx) {
		if(row>=getDepth()) {
			return Color.white.getRGB();//white
		}
		if(idx>=getLength()) {
			return Color.white.getRGB();
		}
		return sequences.get(row).get(idx);
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
	
	
	public double getMaxDiff() {
		// 
		double max = -1000000;
		double hold = 0;
		for(int i = 0; i< getDepth()-1;i++) {
			for(int j = i+1; j< getDepth();j++) {
				hold = get(i).compare(get(i), get(j));
				if(hold>max)
					max = hold;
			}
		}
		return max;
		
	}
	
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

	public Sequence combineSequences(int start, int end){
		// iterates over sequence member from start to end and returns the combined sequence pattern
		if(end==start)return sequences.get(start);
		Sequence comb = new Sequence();
		//iterate over all elements of sequence form start to end
		
		for(int j = 0;j<getLength();j++) {
			float sum = 0;
			for (int i = start;i<=end;i++) {
				sum+=get(i,j);
			}
			comb.add((int) (((sum)/(end-start+1))));
			
		}
		//System.out.println(start+" "+end);
		return comb;
	}
	public Sequence combineSequences(ArrayList<Sequence> ss){
		// iterates over sequence member from start to end and returns the combined sequence pattern
		if(ss.size()==0)return new Sequence(getLength());
		Sequence comb = new Sequence();
		//iterate over all elements of sequence form start to end
		
		for(int j = 0;j<getLength();j++) {
			float sum = 0;
			for (int i =0;i< ss.size();i++) {
				sum+=ss.get(i).get(j); 
			}
			comb.add((int) (((sum)/(ss.size()))));
			
		}
		//System.out.println(start+" "+end);
		return comb;
	}

	public Sequence measureDiff(int start, int end){
		if(end<=start+1)return new Sequence(getLength());
		// iterates over sequence member from start to end and returns the differential pattern
		ArrayList<Sequence> s = new ArrayList<>();
		Sequence average = combineSequences(start, end);
		
		for(int i = start;i<=end; i++) {
			s.add(average.measureUncertain(get(i)));
		}
		return combineSequences(s);
	}
	
	public Sequence measureDiff(ArrayList<Sequence> seqs){
		// iterates over input sequences and returns the differential pattern as a Sequence
		if(seqs.size() <=1)return new Sequence(getLength());
		ArrayList<Sequence> s = new ArrayList<>();
		Sequence average = combineSequences(seqs);

		//iterate over all elements of sequence form start to end

		for (int i = 0;i<seqs.size();i++) {
			s.add(average.measureUncertain(seqs.get(i)));
		}
			
		
		//System.out.println(start+" "+end);
		return combineSequences(s);
	}
	
	
}
