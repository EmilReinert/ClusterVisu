package pixelvisu.seg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

public class Group extends Sequences{
	// Group contains collection of sequences and

	ArrayList<Integer> sections; // holder for same threshold section calculation
	
	public Group() {
		group_count =0;
		sequences = new ArrayList<Sequence>();
		makeWeights();
		
	}
	public Group(ArrayList<Sequence> s) {
		group_count =0;
		sequences = copySeqs(s);
		makeWeights();
	}

	public Group(Group other) {
		//copy constructor
		group_count =0;
		sequences =copySeqs(other.sequences) ;
		makeWeights();
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
		
		//finding smallest weight for start
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
				if( get(j).getDiff(get(i))<min_diff) {
					min_diff = get(j).getDiff(get(i));
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

	public ArrayList<Integer> getSections(int group_count) {
		//thresh= amount of groups
		if(this.group_count == group_count) {
			return sections;
		}
		this.group_count = group_count;

		makeWeights();
		
		// finding threshold size
		// by taking sections with decreasing threshold
		// note: the threshold isnt a linear curve
		int thresh = (getMaxWeight()-getMinWeight())/2;
		int weight = 0;
		int hold_weight = getWeight(0);
		ArrayList<Integer> sec = new ArrayList<>();
		while(sec.size()<group_count) {
			 sec = new ArrayList<>();
			 hold_weight = getWeight(0);
			for(int i =0; i<sequences.size();i++) {
				weight = getWeight(i);
				if(weight>thresh+hold_weight||weight<hold_weight-thresh) {
//					System.out.println(weight+" - "+thresh+" "+hold_weight+" "+i);
					sec.add(i);
					hold_weight = weight;
				}
			}
			thresh--;
		}
		//System.out.println(thresh);
		System.out.println("Sections: ");
		for(int i:sec)System.out.print(i+" ");
		System.out.println('\n');
		sections = sec;
		return sec;
	}

	
	
	
	
}
