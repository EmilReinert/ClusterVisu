package pixelvisu.pipeline;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Cluster {
	ArrayList<Cluster> branches; // already ordered list of sequences
	Sequence data;
	Group flat;
	ArrayList<Group> sections;
	boolean isLeaf;
	int maxdepth;
	int size;
	int group_count =-10;
	int length = 269;
	
	public Cluster() {
		maxdepth = 0;
		branches = new ArrayList<Cluster>();
		isLeaf = true;
		data = new Sequence();
	}
	
	public Cluster(Sequence s) {
		// Making branch
		maxdepth = 100;
		branches = new ArrayList<Cluster>();
		isLeaf = true;
		data = new Sequence(s);
	}
	
	public Cluster(ArrayList<Cluster> bs) {
		// making cluster
		

		maxdepth = 100;
		branches =bs ;
		isLeaf = false;
		data = new Sequence(length);
	}
	public Cluster(ArrayList<Cluster> bs,String a) {
		// making cluster
		
		maxdepth = 100;
		branches =bs ;
		isLeaf = false;
		data = new Sequence(length);
	}
	
	
	public Cluster(Cluster other) {
		maxdepth=other.maxdepth;
		group_count = other.group_count;
		isLeaf = other.isLeaf;
		data = new Sequence(other.data);
		if(!isLeaf)
			branches = copyBranches(other.branches);
		
	}

	
	public void makeSections(int max) {
		// iterates over Cluster Tree sturcture and returns once max group count is gotten
		if(group_count== max)return ;
		group_count = max;
		ArrayList<Group> groups = new ArrayList<Group>();
		if(isLeaf) {System.err.println("something went wrong doing sectioning"); return ;}
		if(max >= size) {System.err.println("something went wrong doing sectioning");return ;}
		
		ArrayList<Cluster> singles = new ArrayList<Cluster>();
		ArrayList<Cluster> plane = branches;
		
		for(int i = 0; i<100000;i++) {
			ArrayList<Cluster> hold = new ArrayList<Cluster>();
			ArrayList<Cluster> singles_hold = new ArrayList<Cluster>();
			for(Cluster cc: plane) {
				for(Cluster c:cc.branches) {
					if(c.isLeaf) {singles_hold.add(c);}
					else {hold.add(c);}
				}
			}
			if(hold.size()>max) {
				// max grouping accomplished (overstepped)
				// plane and singles contain all groups
//				plane.addAll(singles);
				flat = new Group();
				int pos = 0;
				ArrayList<Integer> secs = new ArrayList<Integer>();
				for(Cluster c:plane) {
					for(Sequence s:c.getFlatBranchesDepth().sequences) {
						flat.add(s);
					}
					if(pos>0)secs.add(pos);
					pos+=c.getFlatBranchesDepth().sequences.size();
					
				}
				for(Cluster c: singles) {flat.add(c.data);secs.add(pos);pos++;}// if we dont look at singles like own groups
				flat.sections = secs;
				System.out.println("sectioning done");
				return;
			
			}
			singles.addAll(singles_hold);
			plane =hold;
		}
		System.err.println("something went wrong doing sectioning");
	}
	
	
	public Cluster(Group sequences,String clustering, String link, String similarity) {
		maxdepth = sequences.getDepth();
		length = sequences.getLength();
		group_count = sequences.group_count;
		isLeaf = false;
		size = sequences.getDepth();
		//branches = mergeSequences(sequences); // adding sequences to depth
		branches = new ArrayList<Cluster>();
		for(Sequence s : sequences.sequences)
			branches.add(new Cluster(s));
//		merge(0,1);merge(0,1);merge(0,1);merge(0,1);merge(0,1);merge(0,1);merge(0,1);merge(0,1);merge(0,1);merge(0,1);merge(0,1);merge(0,1);
		
		// HERE WE CAN DECIDE DIFFERENT PIPELINE APPROACHES
		// BOTTOMS UP
		clusterize(clustering, link,similarity);
		
//		if(link == "complete")swap(a, b);
		printCluster();
		flat = getFlatBranchesDepth();
		System.out.println("Cluster size:"+branches.size()+" and Size"+flat.getDepth());
	}
	
	
	public void clusterize(String clustering, String link, String similarity) {
		// Complete
		if(clustering == "agglomerative")
		{
			int a =0; int b =0; // indices to most similar clusters
			double min_diff = 100000000;
			double hold = min_diff;
			for (int o = 0; o<100000;o++) {
				if(branches.size()<2)return;
				for (int i = 0; i<branches.size();i++) {
					for(int j = i+1; j<branches.size();j++) {
						hold = getBranch(i).compare(getBranch(j),link, similarity);
						if(hold<min_diff) {min_diff= hold; a=i;b=j;}
					}
				}
				System.out.println(hold);
				// -> debug
				//System.out.println(o+" "+branches.size()+" "+min_diff);	System.out.println(a+" "+b);if(branches.size()==200) return;
				merge(a,b);a=0;b =0; min_diff = 1000000;
			}
		
		}
	}
	
	
	public double compare(Cluster other,String link, String similarity) {
		if(link =="complete") {
			Group a =getFlatBranchesDepth();
			Group b = other.getFlatBranchesDepth();
			
			double maximum = -10;
			double hold =maximum;
			for (Sequence sa:a.sequences) {
				for(Sequence sb:b.sequences) {
					hold =sa.compare(sb, similarity);
					if(hold>maximum){
						maximum=hold;
					}
				}
			}
			return maximum;
		}
		if(link =="single") {
			Group a =getFlatBranchesDepth();
			Group b = other.getFlatBranchesDepth();
			
			double minimum = 10000000;
			double hold =minimum;
			for (Sequence sa:a.sequences) {
				for(Sequence sb:b.sequences) {
					hold =sa.compare(sb, similarity);
					if(hold<minimum){
						minimum=hold;
					}
				}
			}
			return minimum;
		}
		if(link =="average") {
			Group a =getFlatBranchesDepth();
			Group b = other.getFlatBranchesDepth();
			
			double hold =0;
			
			for (Sequence sa:a.sequences) {
				for(Sequence sb:b.sequences) {
					hold +=(sa.compare(sb, similarity)/(length*length));
				}
			}
			return hold;
		}
		return -1;
		
	}
	
	
	
	
	
	
	
	

	public void merge(int a, int b) {
		if(a==b)return;
		ArrayList<Cluster> bs = new ArrayList<Cluster>();
		bs.add(new Cluster(getBranch(a)));
		bs.add(new Cluster(getBranch(b)));
		Cluster merged = new Cluster(bs,"");
		branches.remove(getBranch(b));
		branches.remove(getBranch(a));
		branches.add(merged);maxdepth--;
	}
	
	
	public void printClusternSize() {
		ArrayList<Cluster> plane = branches;
		for(int i = 0; i<100000;i++) {
			int size = 0;
			ArrayList<Cluster> hold = new ArrayList<Cluster>();
			boolean lastleaf =true;
			for(Cluster cc: plane) {for(Cluster c:cc.branches) {
				if(!c.isLeaf)lastleaf=false;
			}}
			
			for(Cluster cc: plane) {for(Cluster c:cc.branches) {
				
				if(c.isLeaf) {}
				else {hold.add(c);}
				size++;
			}
			}
			System.out.println(size);
			plane =hold;
			if(lastleaf)return ;
		}
	}
	public void printCluster() {
		String s ="";
		ArrayList<Cluster> plane = branches;
		for(int i = 0; i<100000;i++) {
			ArrayList<Cluster> hold = new ArrayList<Cluster>();
			boolean lastleaf =true;
			for(Cluster cc: plane) {for(Cluster c:cc.branches) {
				if(!c.isLeaf)lastleaf=false;
			}}
			
			for(Cluster cc: plane) {for(Cluster c:cc.branches) {
				
				if(c.isLeaf)s+="o";
				else {s+="x";hold.add(c);}
				s+=",";
			}
			}
			System.out.println(s);s="";
			plane =hold;
			if(lastleaf)return ;
		}
	}
	public int getDepth() {
		ArrayList<Cluster> plane = branches;
		int depth = 0;
		for(int i = 0; i<100000;i++) {
			int size = 0;
			ArrayList<Cluster> hold = new ArrayList<Cluster>();
			boolean lastleaf =true;
			for(Cluster cc: plane) {for(Cluster c:cc.branches) {
				if(!c.isLeaf)lastleaf=false;
			}}
			
			for(Cluster cc: plane) {for(Cluster c:cc.branches) {
				
				if(c.isLeaf) {}
				else {hold.add(c);}
				size++;
			}
			}
			plane =hold;
			depth++;
			if(lastleaf)return depth;
		}
		return 0;
	}
	
	public int getSize() {
		ArrayList<Cluster> plane = branches;
		int leaf_sum = 0;
		for(int i = 0; i<100000;i++) {
			int size = 0;
			ArrayList<Cluster> hold = new ArrayList<Cluster>();
			boolean lastleaf =true;
			for(Cluster cc: plane) {for(Cluster c:cc.branches) {
				if(!c.isLeaf)lastleaf=false;
			}}
			
			for(Cluster cc: plane) {for(Cluster c:cc.branches) {
				
				if(c.isLeaf) {leaf_sum++;}
				else {hold.add(c);}
				size++;
			}
			}
			plane =hold;
			if(lastleaf)return leaf_sum;
		}
		return 0;
	}
	
	public ArrayList<Cluster> copyBranches( ArrayList<Cluster> bs){
		ArrayList<Cluster> copy = new ArrayList<Cluster>();
		
		for (Cluster c:bs) {
			copy.add(new Cluster(c));
			
		}
		return copy;
	}
	
	public Cluster getBranch(int i) {
		if(isLeaf) return null;
				
		return branches.get(i);
		
	}
	public ArrayList<Cluster> getBranches() {
		if(isLeaf) return null;
				
		return branches;
		
	}

	public void swap(int a, int b) {
		Collections.swap(branches, a, b); 
	}
	
	
	public float getData(int idx) {
		// returns average data
		float sum =0;
		for (Sequence branch: flat.sequences) {
			sum+= branch.get(idx);
		}
		return sum/flat.getDepth();
	}
	
	public int getData(int row, int idx) {
		// returns average data
		return (int)flat.get(row).get(idx);
	}
	
	
	public Group getFlatBranchesDepth() {
		Group cl = new Group();
		if(isLeaf) {cl.add(this.data); return cl;}
		

		ArrayList<Cluster> all = getChildrenValues(branches);
		for (Cluster c :all){
			cl.add(c.data);
		}
		return cl;
		
	}	
	
	public ArrayList<Cluster> getChildrenValues(ArrayList<Cluster> cs){
		ArrayList<Cluster> all =new ArrayList<Cluster>();
			for(Cluster c:cs) {
				if(c.isLeaf)all.add(c);
				else all.addAll(getChildrenValues(c.branches));
			}
		return all;
	}

}
