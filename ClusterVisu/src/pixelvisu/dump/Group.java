package pixelvisu.dump;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

public class Group extends Sequences{
	// Group contains collection of sequences and

	ArrayList<Integer> sections_w; // holder for same threshold section calculation
	ArrayList<Integer> sections_s; // holder for same threshold section calculation
	int group_count2=0;
	
	public Group(int group) {
		group_count = group;
		sections_w = new ArrayList<Integer>();
		sections_s = new ArrayList<Integer>();
		sequences = new ArrayList<Sequence>();
		makeWeights();
		
	}
	public Group() {
		group_count = 0;
		sections_w = new ArrayList<Integer>();
		sections_s = new ArrayList<Integer>();
		sequences = new ArrayList<Sequence>();
		makeWeights();
		
	}
	public Group(ArrayList<Sequence> s, int group) {
		group_count =0;
		sequences = copySeqs(s);
		makeWeights();
		sections_w = getSectionsWeight(group_count);
		sections_s = getSectionsSimilar(group_count);
	}

	public Group(Group other) {
		//copy constructor
		group_count =other.group_count;
		sequences =copySeqs(other.sequences) ;
		makeWeights();
		sections_w = getSectionsWeight(group_count);
		sections_s = getSectionsSimilar(group_count);
	}
	
	public void weightOrder() {
		// order Image Group weights to create ordered weights data
		int max =500000; 
		for(int i = 0; i< getDepth();i++) {
			int max_pointer =i;
			for(int j = i; j< getDepth();j++) {
				if( max>get(j).getWeight()) {
					max_pointer=j;
					max = get(j).getWeight();
					}
			}
			swap( i, max_pointer); 
			max =500000;
			//System.out.println("swap");
		}
		System.out.println("reordered weights");
		//for(int i = 0; i<getDepth();i++) {System.out.println(sequences_o.get(i).getWeight());}
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

	public ArrayList<Integer> getSectionsWeight(int group_count) {
		//thresh= amount of groups
		if(this.group_count == group_count&&sections_w!=null&& sections_w.size()>0) {
			return sections_w;
		}
		if(group_count<2)return new ArrayList<>();
		this.group_count = group_count;

		makeWeights();
		
		// finding threshold size
		// by taking sections with decreasing threshold
		// note: the threshold isnt a linear curve
		int thresh = (getMaxWeight()-getMinWeight())/2;
		int weight = 0;
		int hold_weight; int prev = 0;
		ArrayList<Integer> sec = new ArrayList<>();
		while(sec.size()<group_count) {
			 sec = new ArrayList<>();
			 hold_weight = getWeight(0);
			 prev = 0;
			for(int i =0; i<getDepth();i++) {
				weight = getWeight(i);
				if(weight>thresh+hold_weight||weight<hold_weight-thresh) {
//					System.out.println(weight+" - "+thresh+" "+hold_weight+" "+i);
					if(prev==0||i>prev+1) {
						sec.add(i);
						hold_weight = weight;
						prev= i;
					}
				}
			}
			thresh--;
		}
		if (!sec.contains(getDepth()))sec.add(getDepth());
		
		//System.out.println(thresh);
		System.out.println("Sections: ");
		for(int i:sec)System.out.print(i+" ");
		System.out.println('\n');
		sections_w = sec;
		return sec;
	}
	
	
	public ArrayList<Integer> getSectionsSimilar(int group_count) {
		//thresh= amount of groups
		if(this.group_count2 == group_count&&sections_s!=null&& sections_s.size()>0) {
			return sections_s;
		}
		if(group_count<2)return new ArrayList<>();
		this.group_count2 = group_count;

		makeWeights();
		
		// finding threshold size
		// by taking sections with decreasing threshold
		// note: the threshold isnt a linear curve
		
		double thresh = getMaxDiff();
		double diff = 0;
		int cut = 0;
		
		ArrayList<Integer> sec = new ArrayList<>();
		
		while(sec.size()<group_count) {
			sec = new ArrayList<>();
			cut = 0;
			for(int i =0; i<getDepth();i++) {
				diff = get(0).compare(get(i),get(cut));// RECURSIVE
				if(diff>(thresh+(thresh*cut*0.001))) {
					if(i>cut+1) {// groups bigger than 1!
						sec.add(i);
						cut= i;
					}
				}
				
			}
			thresh*=0.99;
		}
		if (!sec.contains(getDepth()))sec.add(getDepth());
		
		//System.out.println(thresh);
		System.out.println("Sections: ");
		for(int i:sec)System.out.print(i+" ");
		System.out.println('\n');
		sections_s = sec;
		return sec;
	}
	
	
	
	
}
