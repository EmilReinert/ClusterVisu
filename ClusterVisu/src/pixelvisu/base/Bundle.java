package pixelvisu.base;

import java.util.ArrayList;
import java.util.Collections;

public class Bundle extends Sequences {
	// behaves like a compressed group
	
	ArrayList<Float> densities; // 
	ArrayList<Float> all_densities; // 
	
	public Bundle() {
		sequences = new ArrayList<>();
		weights = new ArrayList<>();
		densities = new ArrayList<>();
		all_densities = new ArrayList<>();
	}
	
	public Bundle(Bundle other) {
		sequences =copySeqs(other.sequences);
		weights = new ArrayList<>(other.weights);
		densities = new ArrayList<>(other.densities);
	}
	
	public Bundle(Group g, int thresh) {
		weights = new ArrayList<>(g.weights);
		compress(g.sequences, g.getSections(thresh));
		
	}
	
	public void compress(ArrayList<Sequence> seqs, ArrayList<Integer> secs) {
		// compresses sequences data by section 
		// and stores results in compressed data and densities
		ArrayList<Sequence> sequences_hold = new ArrayList<>();
		densities= new ArrayList<>();
		int prev = 0;
		int delta = 0;
		Sequence compresso = new Sequence();
		//iterates over sec data and takes that index to accessd data
		for(int i=0; i<secs.size();i++) {
			//System.out.println(compresso.getWeight());
			delta = secs.get(i)-prev;
			densities.add((float) delta/seqs.size());
			
			compresso = seqs.get(secs.get(i)); 
			sequences_hold.add(compresso);
			prev = secs.get(i);
		}
		sequences = sequences_hold;
		//System.out.println("extracted desnse"+all_densities.size());
		
	}
	
	
	public void compressOrder() {
		//order Data Bundle densities
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
			max =0;
		}

		System.out.println("reordered compressed densities");
	}
	
	
	public int getSeqValue(int i) {
		return getDensity(i);
	}
	
	public float getAllDensity(int i) {
		if(all_densities.size()>0)
			return all_densities.get(i);
		return 0;
	}

	public int getDensity(int i) {
		if(densities.size()>0)
			return (int) (densities.get(i)*100);
		return 0;
	}
	
}
