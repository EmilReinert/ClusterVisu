package pixelvisu.squeeze;

import java.util.ArrayList;
import java.util.Collections;

public class Bundle extends Sequences{
	// behaves like a compressed group
	
	ArrayList<Integer> densities; // 
	ArrayList<Sequence> original; // original sequence
	ArrayList<Integer> mapping; // mapping from original compressed to desity ordered compressed
	ArrayList<Sequence> differentials; // difference measure for compressed data bundles
	ArrayList<Integer> sections;
	
	public Bundle() {
		sections = new ArrayList<>();
		sequences = new ArrayList<>();
		weights = new ArrayList<>();
		densities = new ArrayList<>();
		original = new ArrayList<>();
	}
	
	public Bundle(Bundle other) {
		sections = new ArrayList<>();
		original = copySeqs(other.original);
		sequences =copySeqs(other.sequences);
		weights = new ArrayList<>(other.weights);
		densities = new ArrayList<>(other.densities);
		differentials = new ArrayList<>(other.differentials);
	}
	
	public Bundle(Group g, int group_count, String mode) {

		if(mode == "w")
			sections = g.getSectionsWeight(group_count);
		if(mode == "s")
			sections = g.getSectionsSimilar(group_count);
		original = copySeqs(g.sequences);
		sequences =copySeqs(g.sequences);
		weights = new ArrayList<>(g.weights);
		group_count = g.group_count = group_count;
		compress(g.sequences,sections );
		
	}
	public Sequence squeeze(int start, int end){
		// iterates over sequence member from start to end and returns the combined sequence pattern
		if(end==start)return original.get(start);
		if(end>=original.size())return original.get(start);
		Sequence comb = new Sequence();
		//iterate over all elements of sequence form start to end
		
		for(int j = 0;j<getLength();j++) {
			float sum = 0;
			for (int i = start;i<=end;i++) {
				sum+=original.get(i).get(j);
			}
			comb.add((int) (((sum)/(end-start+1))));
			
		}
		//System.out.println(start+" "+end);
		return comb;
	}
	
	public void compress(ArrayList<Sequence> seqs, ArrayList<Integer> secs) {
		// compresses sequences data by section 
		// and stores results in compressed data and densities
		ArrayList<Sequence> sequences_hold = new ArrayList<>();
		densities= new ArrayList<>();
		differentials= new ArrayList<>();
		int prev = 0;
		int delta = 0;
		//iterates over sec data and takes that index to accessd data
		for(int i=0; i<secs.size();i++) {
			//System.out.println(compresso.getWeight());
			delta = secs.get(i)-prev;
			densities.add(delta);
			
			sequences_hold.add(combineSequences(prev, secs.get(i)-1));
			differentials.add(measureDiff(prev,secs.get(i)-1));
			prev = secs.get(i);
		}
		sequences = sequences_hold;
		
		System.out.println("Densities:");
		for(int i: densities)			System.out.print(i+" ");
		System.out.println("\n");
	}
	
	
	public void compressOrder() {
		//order Data Bundle by densities
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
			map.add(max_pointer);
			max =0;
		}
		// creating mapping
		for(int i=0;i<map.size();i++) mapping.add(i);
		for(int i=0;i<map.size();i++)Collections.swap(mapping, i, map.get(i));

		System.out.println("reordered compressed densities");
		for(int d: densities)			System.out.print(d+" ");
		System.out.println("\n");
		
		System.out.println("mapping");
		for(int d:  mapping)			System.out.print(d+" ");
		System.out.println("\n");
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
	
	public int getDiff(int row, int idx) {
		// returns average differential value of specific compressed bundle element
		return (int)(differentials.get(row).get(idx)*.1f);
	}
	
	public int getOriginal(int sec_idx, int i,int j) {
		//returns original value with index of compressed data
		int local_den = 0;
		if(i<0||j>getLength())return -1;
		int sec_sum = 0;
		if(mapping!=null) {
			local_den=getDensity(mapping.indexOf(sec_idx));
			for(int s = 0;s<mapping.get(sec_idx);s++) {
				sec_sum += getDensity(mapping.indexOf(s));
				//System.out.println(getDensity(mapping.get(s)));
			}
		}
		else {
			local_den = getDensity(sec_idx);
			for(int s = 0;s<sec_idx;s++) {
		
				sec_sum += getDensity(s);
			//System.out.println(sec_sum);
			}
		}
		
		//System.out.println("sum = "+sec_sum+'\n');
		//System.out.println("Opening Section: "+sec_idx+" at Row Idx "+sec_sum+"; size = "+getDensity(mapping.indexOf(sec_idx)));
		if((sec_sum+i)>=original.size())return -1;
		return original.get(sec_sum+i).get(j);
	}
	public int getSqueezed(int sec_idx, int i,int j, int space) {
		// returns original sequence at given i and j as into "space" dimension 
		// squeezed rows
		// same as get original
		int local_den = 0;
		if(i<0||j>getLength())return -1;
		int sec_sum = 0;
		if(mapping!=null) {
			local_den=getDensity(mapping.indexOf(sec_idx));
			for(int s = 0;s<mapping.get(sec_idx);s++) {
				sec_sum += getDensity(mapping.indexOf(s));
				//System.out.println(getDensity(mapping.get(s)));
			}
		}
		else {
			local_den = getDensity(sec_idx);
			for(int s = 0;s<sec_idx;s++) {
		
				sec_sum += getDensity(s);
			//System.out.println(sec_sum);
			}
		}
		
		// finally determining the squeezing sizes
		// and returning combined data sequence
		float squeeze_ratio =  getDensity(sec_idx)/(float)space;
//		if(squeeze_ratio>1)System.out.println(sec_idx+" "+squeeze_ratio);
		if(squeeze_ratio<1)
			return original.get(sec_sum).get(i);
			
			
		float squeeze_start =sec_sum+ i*(float)squeeze_ratio ;
		float squeeze_end = squeeze_start+squeeze_ratio;
		

		if((squeeze_start)>=original.size())return -1;
		//System.out.println(squeeze_ratio +" "+squeeze_start+" "+squeeze_end);
		Sequence squeeze = squeeze((int)squeeze_start,(int)squeeze_end);
		return squeeze.get(j);
		
		
	}
	
}
