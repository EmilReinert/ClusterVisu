package pixelvisu.visu;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


public class Group implements Serializable{
	// Group contains collection of sequences and

	ArrayList<Integer> sections; // holder for same threshold section calculation
	ArrayList<Integer> densities;
	int group_count=0;
	int length = 0;
	ArrayList<Sequence> sequences ; // already ordered list of sequences
	ArrayList<Integer> weights;
	
	public Group(int group) {
		group_count = group;
		densities = new ArrayList<Integer>();
		sections = new ArrayList<Integer>();
		sequences = new ArrayList<Sequence>();
		makeWeights();
		
	}
	public Group() {
		group_count = 0;
		sections = new ArrayList<Integer>();
		sequences = new ArrayList<Sequence>();
		densities = new ArrayList<Integer>();
		makeWeights();
		
	}
	public Group(ArrayList<Sequence> s, int group) {
		group_count =0;
		sequences = copySeqs(s);
		densities = new ArrayList<Integer>();
		makeWeights();
		sections = new ArrayList<Integer>();
	}

	public Group(Group other) {
		//copy constructor
		group_count =other.group_count;
		sequences =copySeqs(other.sequences) ;
		densities = other.densities;
		makeWeights();
		sections = new ArrayList<Integer>();
//		this.mapping =other.mapping;
	}
	
	public void makeDensities(ArrayList<Integer> halfden) {
		// takes densities and fits them to sequence size by repeating denisty size
		densities = new ArrayList<Integer>();
		for(int i = 0; i<halfden.size();i++) {
			for(int j =0;j<halfden.get(i);j++)
				densities.add(halfden.get(i));
		}
	}
	public void makeWeights() {
		weights = new ArrayList<>();
		length = 0;
		for(int i = 0; i<sequences.size();i++) {
			weights.add(sequences.get(i).getWeight());
			if(sequences.get(i).getLength()>length) {
				length = sequences.get(i).getLength();}
//			System.out.println(sequences.get(i).getWeight());
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
			if(sequences.size()>i)
			return sequences.get(i);
//		System.err.println("Sequence not found! Pos: "+i+", Sequence Length: "+sequences.size());
		return new Sequence(getLength(),-1);
	}
	
	public double get(int row, int idx) {
		if(row>=getDepth()) {
			return -1;
		}
		if(idx>=getLength()) {
			return -1;
		}
		return sequences.get(row).get(idx);//getContrast(idx); //////////
	}

//	public double getTime(int row, int idx) {
//		if(row>=getDepth()) {
//			return -1;
//		}
//		if(idx>=getLength()) {
//			return -1;
//		}
//		return sequences.get(row).getTime(idx);
//	}
	
	public double getContrast(int row, int idx) {
		if(row>=getDepth()) {
			return Color.white.getRGB();//white
		}
		if(idx>=getLength()) {
			return Color.white.getRGB();
		}
		return sequences.get(row).getContrast(idx); //////////
	}

	public void add(Sequence d) {
		sequences.add(d);
		weights.add(d.getWeight());
		if(d.getLength()>length) length = d.getLength();
	}
	
	public int getDepth() {
		if(sequences!=null&&sequences.size()>0)
		return sequences.size();
		return -1;
	}
	public int getLength() {
//		System.out.println(length);
		return length;
	}
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
			comb.add((int) (((sum)/(end-start))));
			
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
	
	
	public void differOrder() {
		// order Image Group similarities to reduce noise
		
		//finding index of smallest weight for start
		int start_idx =0;
		int min_weight = getMinWeight();
		for(int i = 0; i< getDepth();i++) {
			if(get(i).getWeight()==min_weight)start_idx=i;
		}
		swap(start_idx,0);

		double min_diff =500000; 
		
		for(int i = 0; i< getDepth()-1;i++) {
			int min_pointer =i;
			for(int j = i+1; j< getDepth();j++) {
				if( get(j).compare(get(j),get(i))<min_diff) {
					min_diff = get(j).compare(get(j),get(i));
					min_pointer=j;
					}
			}
			swap( i+1, min_pointer); 
			min_diff =500000;
			//System.out.println("swap");
		}
		System.out.println("reordered similarities");
		//for(int i = 0; i<getDepth();i++) {System.out.println(sequences_o.get(i).getWeight());}
	}
	
	public int getSeqValue(int i) {
		return getWeight(i);
	}
	
	public int getWeight(int i) {
		return weights.get(i);
	}

	
}
