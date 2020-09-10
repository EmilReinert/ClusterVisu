package pixelvisu.data;

import java.util.ArrayList;
import java.util.Collections;


public class Bundle extends Group{
	
	ArrayList<Integer> densities; // 
	ArrayList<Sequence> original; // original sequence
	ArrayList<Integer> mapping; // mapping from original compressed to ordered compressed
	ArrayList<Sequence> differentials; // difference measure for compressed data bundles
	ArrayList<Integer> sections;
	
	public Bundle() {
		sections = new ArrayList<>();
		sequences = new ArrayList<>();
		weights = new ArrayList<>();
		densities = new ArrayList<>();
		original = new ArrayList<>();
	}
	
	
	public Bundle(Group g) {

		sections = g.sections;
		original = copySeqs(g.sequences);
		sequences =copySeqs(g.sequences);
		compress();
//		densityOrder();
		
	}
	
	public void compress() {
		// compresses sequences data by section 
		// and stores results in compressed data and densities
		ArrayList<Sequence> sequences_hold = new ArrayList<>();
		densities= new ArrayList<>();
		differentials= new ArrayList<>();
		int prev = 0;
		int delta = 0;
		//iterates over sec data and takes that index to accessd data
		for(int i=0; i<sections.size();i++) {
			delta = sections.get(i)-prev;
			densities.add(delta);
			
			sequences_hold.add(combineSequences(prev, sections.get(i)-1));
			differentials.add(measureDiff(prev,sections.get(i)-1));
			prev = sections.get(i);
		}
		sequences = sequences_hold;
		
//		System.out.println("Densities:");
//		for(int i: densities)			System.out.print(i+" ");
//		System.out.println("\n");
	}
	
	
	public void densityOrder() {
		//order Data Bundle by densities+
		mapping = new ArrayList<>();
		ArrayList<Integer> map = new ArrayList<>();
		float max =0; 
		for(int i = 0; i<getDepth();i++) {
			int max_pointer =i;
			for(int j = i; j< getDepth();j++) {
				if( max<getDensity(j)) {
					max_pointer=j;
					max = getDensity(j);
					}
			}
			swap( i, max_pointer); 
			Collections.swap(densities, i, max_pointer);
			Collections.swap(differentials, i, max_pointer);
			map.add(max_pointer);
			max =0;
		}
		// creating mapping
		for(int i=0;i<map.size();i++) mapping.add(i);
		for(int i=0;i<map.size();i++)Collections.swap(mapping, i, map.get(i));

	}
	
	public void weightOrder() {
		//order Data Bundle by average weight
		mapping = new ArrayList<>();
		ArrayList<Integer> map = new ArrayList<>();
		float max =0; 
		for(int i = 0; i<getDepth();i++) {
			int max_pointer =i;
			for(int j = i; j< getDepth();j++) {
				if( max<getWeight(j)) {
					max_pointer=j;
					max = getWeight(j);
					}
			}
			swap( i, max_pointer); 
			Collections.swap(densities, i, max_pointer);
			Collections.swap(differentials, i, max_pointer);
			map.add(max_pointer);
			max =0;
		}
		// creating mapping
		for(int i=0;i<map.size();i++) mapping.add(i);
		for(int i=0;i<map.size();i++)Collections.swap(mapping, i, map.get(i));

	}	
	
	
	


	public int getSeqValue(int i) {
		return getDensity(i);
	}
	
	/*
	public float getAllDensity(int i) {
		if(all_densities.size()>0)
			return all_densities.get(i);
		return 0;
	}*/

	public int getDensity(int i) {
		if(densities.size()>0)
			return (int) (densities.get(i));
		System.out.println("density out of range");
		return 0;
	}
	public int getWeight(int i) {
		return sequences.get(i).getWeight();
	}
	
	public int getDiff(int row, int idx) {
		// returns average differential value of specific compressed bundle element
		return (int)(differentials.get(row).get(idx)*.1f);
	}
	
	public double getOriginal(int sec_idx, int i,int j) {
		//returns original value with index of compressed data
		if(i<0||j>getLength())return -1;
		int sec_sum = 0;
		if(mapping!=null) {
			sec_idx=mapping.get(sec_idx);
			for(int s = 0;s<sec_idx;s++) {
				sec_sum += getDensity(mapping.indexOf(s));
				//System.out.println(getDensity(mapping.get(s)));
			}
		}
		else {
			for(int s = 0;s<sec_idx;s++) {
		
				sec_sum += getDensity(s);
			//System.out.println(sec_sum);
			}
		}
		
		//System.out.println("sum = "+sec_sum+'\n');
		//System.out.println("Opening Original Section: "+sec_idx+" at Row Idx "+sec_sum+"; size = "+getDensity(mapping.indexOf(sec_idx)));
		if((sec_sum+i)>=original.size())return -1;
		return original.get(sec_sum+i).get(j);
	}
	public double getOriginalContrast(int sec_idx, int i,int j) {
		//returns original value with index of compressed data
		if(i<0||j>getLength())return -2;
		int sec_sum = 0;
		if(mapping!=null) {
			sec_idx=mapping.get(sec_idx);
			for(int s = 0;s<sec_idx;s++) {
				sec_sum += getDensity(mapping.indexOf(s));
				//System.out.println(getDensity(mapping.get(s)));
			}
		}
		else {
			for(int s = 0;s<sec_idx;s++) {
		
				sec_sum += getDensity(s);
			//System.out.println(sec_sum);
			}
		}
		
		//System.out.println("sum = "+sec_sum+'\n');
		//System.out.println("Opening Original Section: "+sec_idx+" at Row Idx "+sec_sum+"; size = "+getDensity(mapping.indexOf(sec_idx)));
		if((sec_sum+i)>=original.size())return -2;
		return original.get(sec_sum+i).getContrast(j);
	}
}
