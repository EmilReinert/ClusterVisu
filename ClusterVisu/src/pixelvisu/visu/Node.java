package pixelvisu.visu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


public class Node implements Serializable{
	public ArrayList<Node> branches; // already ordered list of sequences
	public Sequence data;
	public boolean isLeaf;
	public double similarity=-1; // similarity value of combined clusters
	public float x_pos; // horizontal position for tree drawing
	
	
	public Node(Sequence s, int idx) {
		// Making branch
		x_pos = idx;
		branches = new ArrayList<Node>();
		isLeaf = true;
		data = new Sequence(s);
	}
	
	public Node(ArrayList<Node> bs, double sim, int length) {
		// making cluster
		similarity =sim;
		branches =bs ;
		isLeaf = false;
		data = new Sequence(length);
		// xpos center of children
		x_pos =0;
		if(bs.size()>0)
			for(int i = 0; i<bs.size();i++)
				x_pos+=bs.get(i).x_pos/bs.size();
//		System.out.print(x_pos+" ");
	}
	
	
	public Node(Node other) {
		x_pos = other.x_pos;
		similarity = other.similarity;
		isLeaf = other.isLeaf;
		data = new Sequence(other.data);
		if(!isLeaf)
			branches = copyBranches(other.branches);
		
	}
	
	public Node(Group sequences) {
		similarity = -10;
		x_pos = -10;
		isLeaf = false;
		//branches = mergeSequences(sequences); // adding sequences to depth
		branches = new ArrayList<Node>();
		for(int i =0; i< sequences.sequences.size();i++) {
			branches.add(new Node(sequences.sequences.get(i),i));
			}

	}
	

	

	public double getMaxDistance() {
		// iterates over tree and retusn the max distance
		double max = -1;
		ArrayList<Node> plane = new ArrayList<Node>();plane.add(this);
		for(int i = 0; i<100000;i++) {
			ArrayList<Node> hold = new ArrayList<Node>();
			boolean lastleaf =true;
			for(Node cc: plane) {for(Node c:cc.branches) {
				if(!c.isLeaf)lastleaf=false;
			}}
			
			for(Node cc: plane) {for(Node c:cc.branches) {
				
				if(!c.isLeaf) {
					hold.add(c);
					if(max<c.similarity)max = c.similarity;}
			}
			}
			plane =hold;
			if(lastleaf)return max;
		}
		return -1;
	}
	
	public ArrayList<Double> getSimilarities() {
		ArrayList<Double> sims = new ArrayList<Double>();
		ArrayList<Node> plane = new ArrayList<Node>();plane.add(this);
		int depth = 0;
		for(int i = 0; i<100000;i++) {
			int size = 0;
			ArrayList<Node> hold = new ArrayList<Node>();
			boolean lastleaf =true;
			for(Node cc: plane) {for(Node c:cc.branches) {
				if(!c.isLeaf)lastleaf=false;
			}}
			
			for(Node cc: plane) {for(Node c:cc.branches) {
				
				if(c.isLeaf) {}
				else {hold.add(c);
				sims.add(c.similarity);}
				size++;
			}
			}
			plane =hold;
			depth++;
			if(lastleaf)return sims;
		}
		return sims;
	}
	
	public int getSize() {
		// returns amount of branches and leafs size of whole node tree
		ArrayList<Node> plane = new ArrayList<Node>();plane.add(this);
		int leaf_sum = 0;
		for(int i = 0; i<100000;i++) {
			int size = 0;
			ArrayList<Node> hold = new ArrayList<Node>();
			boolean lastleaf =true;
			for(Node cc: plane) {for(Node c:cc.branches) {
				if(!c.isLeaf)lastleaf=false;
			}}
			
			for(Node cc: plane) {for(Node c:cc.branches) {
				
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
	
	
	public ArrayList<Node> copyBranches( ArrayList<Node> bs){
		ArrayList<Node> copy = new ArrayList<Node>();
		
		for (Node c:bs) {
			copy.add(new Node(c));
			
		}
		return copy;
	}
	
	public Node getBranch(int i) {
		if(isLeaf) return null;
				
		return branches.get(i);
		
	}
	public ArrayList<Node> getBranches() {
		if(isLeaf) return null;
				
		return branches;
		
	}

	public void swap(int a, int b) {
		Collections.swap(branches, a, b); 
	}
	
	
	
	public Group getFlatBranchesDepth() {
		Group cl = new Group();
		ArrayList<Integer> mapping = new ArrayList<Integer>();
		if(isLeaf) {cl.add(this.data);cl.length++; return cl;}
		

		ArrayList<Node> all = getChildrenValues(branches);
		for (Node c :all){
			cl.add(c.data);
			mapping.add((int) c.x_pos);
		}
//		cl.mapping=mapping;
		return cl;
		
	}	
	
	public ArrayList<Node> getChildrenValues(ArrayList<Node> cs){
		ArrayList<Node> all =new ArrayList<Node>();
			for(Node c:cs) {
				if(c.isLeaf)all.add(c);
				else all.addAll(getChildrenValues(c.branches));
			}
		return all;
	}


}
