package pixelvisu.data;

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
	

	

	public void clusterize(String clustering, String link, String similarity) {
		// cluster whole child nodes into tree by given parameters
		long last_time = System.nanoTime();
		if(clustering == "agglomerative")
		{
			int a =0; int b =0; // indices to most similar clusters
			double min_diff = 100000000;
			double hold = min_diff;
			for (int o = 0; o<=100000000;o++) {
				if(branches.size()<2)return;
				for (int i = 0; i<branches.size();i++) {
					for(int j = i+1; j<branches.size();j++) {
						hold = getBranch(i).compare(getBranch(j),link, similarity);
						if(hold<min_diff) {min_diff= hold; a=i;b=j;}
//						long time = System.nanoTime();System.out.println(((time - last_time) / 1000));last_time = time;
					}
				}
				// -> debug
//				System.out.println(o+" "+branches.size()+" "+min_diff);	System.out.println(a+" "+b);if(branches.size()==200) return;

//				if(o%(int)(length/(10*60))==0)System.out.print(",");
				merge(a,b,min_diff);a=0;b =0; min_diff = 1000000;
			}
			System.out.println("\n");//br
		
		}
	}
	
//	public void clusterize(Node other) {
//		// cluster current children by already clustered other root-node structure
//		
//	}	
	
	public double compare(Node other,String link, String sim) {
		if(link =="complete") {
			Group a =getFlatBranchesDepth();
			Group b = other.getFlatBranchesDepth();
			
			double maximum = -10;
			double hold =maximum;
			for (Sequence sa:a.sequences) {
				for(Sequence sb:b.sequences) {
					hold =sa.compare(sb, sim);
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
					hold =sa.compare(sb, sim);
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
					hold +=(sa.compare(sb, sim)/(length*length));
				}
			}
			return hold;
		}
		return -1;
		
	}
		

	public void merge(int a, int b, double sim) {
		// merging two clusters together by creating a new one and removing the previous branches
		if(a==b)return;
		ArrayList<Node> bs = new ArrayList<Node>();
		bs.add(new Node(getBranch(a)));
		bs.add(new Node(getBranch(b)));
//		System.out.println(sim);
		Node merged = new Node(bs,sim);
		branches.remove(getBranch(b));
		branches.remove(getBranch(a));
		branches.add(merged);
	}

	
	public void printCluster() {
		String s ="";
		ArrayList<Node> plane = new ArrayList<Node>();plane.add(this);
		for(int i = 0; i<100000;i++) {
			ArrayList<Node> hold = new ArrayList<Node>();
			boolean lastleaf =true;
			for(Node cc: plane) {for(Node c:cc.branches) {
				if(!c.isLeaf)lastleaf=false;
			}}
			
			for(Node cc: plane) {for(Node c:cc.branches) {
				
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
	
	public void printClusterSim() {
		String s ="";
		ArrayList<Node> plane = new ArrayList<Node>();plane.add(this);
		for(int i = 0; i<100000;i++) {
			ArrayList<Node> hold = new ArrayList<Node>();
			boolean lastleaf =true;
			for(Node cc: plane) {for(Node c:cc.branches) {
				if(!c.isLeaf)lastleaf=false;
			}}
			
			for(Node cc: plane) {for(Node c:cc.branches) {
				
				if(c.isLeaf)s+="o";
				else {s+="x";s+=c.similarity;hold.add(c);}
				s+=",";
			}
			}
			System.out.println(s);s="";
			plane =hold;
			if(lastleaf)return ;
		}
	}
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
