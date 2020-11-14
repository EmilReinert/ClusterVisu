package pixelvisu.visu;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


public class Node implements Serializable{
	ArrayList<Node> branches; // already ordered list of sequences
	Sequence data;
	boolean isLeaf;
	double similarity=-1; // similarity value of combined clusters
	int length; // sequence length
	float x_pos; // horizontal position for tree drawing
	
	public Node() {
		x_pos =0;
		length = 0;
		similarity = 0;
		branches = new ArrayList<Node>();
		isLeaf = true;
		data = new Sequence();
	}
	
	public Node(Sequence s, int idx) {
		// Making branch
		x_pos = idx;
		length=s.getLength();
		similarity = 0;
		branches = new ArrayList<Node>();
		isLeaf = true;
		data = new Sequence(s);
	}
	
	public Node(ArrayList<Node> bs, double sim) {
		// making cluster
		length = bs.get(0).length;
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
		length=other.length;
		similarity = other.similarity;
		isLeaf = other.isLeaf;
		data = new Sequence(other.data);
		if(!isLeaf)
			branches = copyBranches(other.branches);
		
	}
	
	public Node(Group sequences) {
		similarity = -10;
		x_pos = -10;
		length = sequences.getLength();
		isLeaf = false;
		//branches = mergeSequences(sequences); // adding sequences to depth
		branches = new ArrayList<Node>();
		for(int i =0; i< sequences.sequences.size();i++) {
			branches.add(new Node(sequences.sequences.get(i),i));
			}

	}
	

	

	
	
//	public void clusterize(Node other) {
//		// cluster current children by already clustered other root-node structure
//		
//	}	
	
//	
//	public void printCluster() {
//		String s ="";
//		ArrayList<Node> plane = new ArrayList<Node>();plane.add(this);
//		for(int i = 0; i<100000;i++) {
//			ArrayList<Node> hold = new ArrayList<Node>();
//			boolean lastleaf =true;
//			for(Node cc: plane) {for(Node c:cc.branches) {
//				if(!c.isLeaf)lastleaf=false;
//			}}
//			
//			for(Node cc: plane) {for(Node c:cc.branches) {
//				
//				if(c.isLeaf)s+="o";
//				else {s+="x";hold.add(c);}
//				s+=",";
//			}
//			}
//			System.out.println(s);s="";
//			plane =hold;
//			if(lastleaf)return ;
//		}
//	}
//	
//	public void printClusterSim() {
//		String s ="";
//		ArrayList<Node> plane = new ArrayList<Node>();plane.add(this);
//		for(int i = 0; i<100000;i++) {
//			ArrayList<Node> hold = new ArrayList<Node>();
//			boolean lastleaf =true;
//			for(Node cc: plane) {for(Node c:cc.branches) {
//				if(!c.isLeaf)lastleaf=false;
//			}}
//			
//			for(Node cc: plane) {for(Node c:cc.branches) {
//				
//				if(c.isLeaf)s+="o";
//				else {s+="x";s+=c.similarity;hold.add(c);}
//				s+=",";
//			}
//			}
//			System.out.println(s);s="";
//			plane =hold;
//			if(lastleaf)return ;
//		}
//	}
	public double getMaxSim() {
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
	
	public int getDepth() {
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
		if(isLeaf) {cl.add(this.data); return cl;}
		

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