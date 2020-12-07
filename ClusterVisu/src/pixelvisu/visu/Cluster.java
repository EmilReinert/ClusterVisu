package pixelvisu.visu;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Cluster implements Serializable {

	public String name="none";
	public Group original; 
	public Node tree;
	public Node treeorder;
	public Group flat;
	public Bundle flat_c;

	int start,end;
	int maxdepth;
	int size;
	int group_count = -10; // SIMILARITY CUT

	public Cluster(Group sequences, String clustering, String link, String sim, String dataname, int start, int end, boolean save) throws IOException {
		// Cluster creation (saving/loading) happens here
		// Tree structure is defined as linked node instances
		sequences.shuffle();
		
		this.start = start; this.end = end;
		original = sequences;
		name = dataname + "/" + clustering + link + sim;
//		System.out.println(				"Clustering from " + start+" to " +end);
		System.out.println(name);
		
		//  MAKING FILES
		tree = new Node(sequences);
		System.out.println("Depth: "+sequences.getDepth()+", Sequence Length: "+sequences.getLength());
		
		clusterize(tree,clustering, link, sim,sequences.getDepth());
		
		flat = tree.getFlatBranchesDepth(); // untangles tree structure and returns the flattened tree
//			System.out.println("Cluster size: "+flat.getDepth()+", Sequence Length: "+flat.getLength());
//			System.out.println(					"----Clusterized----");
			
		
		treeorder = new Node(flat);
		clusterize(treeorder,clustering, link, sim,sequences.getDepth());
	}

	public Cluster(Group sequences,Cluster other) {
		// projects internal cluster structure to this clusters sequences
		original = sequences;
		flat = new Group(other.flat);
		ArrayList<Sequence> seq = new ArrayList<Sequence>();
		for(Sequence s:other.flat.sequences) {
			seq.add(sequences.get(s.getPos()));
		}
		flat.sequences=seq;
		flat_c = new Bundle(flat);
		
		// TODO copy depth and not just flat
		
	}
	
	
	
	public void clusterize(Node tree, String clustering, String link, String similarity, int depth) {
		// cluster whole child nodes into tree by given parameters
		long last_time = System.nanoTime();
		if(clustering == "agglomerative")
		{
			int a =0; int b =0; // indices to most similar clusters
			double min_diff = 100000000;
			double hold = min_diff;
			for (int o = 0; o<=depth;o++) {
				for (int i = 0; i<tree.branches.size();i++) {
					for(int j = i+1; j<tree.branches.size();j++) {
						hold = compare(tree.getBranch(i),tree.getBranch(j),link, similarity);
						if(hold<min_diff) {min_diff= hold; a=i;b=j;}
//						long time = System.nanoTime();System.out.println(((time - last_time) / 1000));last_time = time;
					}
				}
				if((o%(int)(300/(10)))==0)System.out.print(",");
				merge(tree,a,b,min_diff);a=0;b =0; min_diff = 1000000;
			}
		
		}
		System.out.println("Clustering Time: "+(double)(System.nanoTime()-last_time)/1000000000);
	}
	
	
	

	public double compare(Node one, Node other,String link, String sim) {

		Group a =one.getFlatBranchesDepth();
		Group b = other.getFlatBranchesDepth();
		
		if (link == "complete") {

			double maximum = -10000000;
			double hold = maximum;
			for (Sequence sa : a.sequences) {
				for (Sequence sb : b.sequences) {
					hold = compare(sa, sb, sim);
					if (hold > maximum) {
						maximum = hold;
					}
				}
			}
			return maximum;
		}
		if (link == "single") {
			double minimum = 10000000;
			double hold = minimum;
			for (Sequence sa : a.sequences) {
				for (Sequence sb : b.sequences) {
					hold = compare(sa, sb, sim);
					if (hold < minimum) {
						minimum = hold;
					}
				}
			}
			return minimum;
		}
		if (link == "average") {
			double hold = 0;

			for (Sequence sa : a.sequences) {
				for (Sequence sb : b.sequences) {
					hold += (compare(sa, sb, sim) / (sa.getLength() * sb.getLength()));

				}
				if (hold < 0) {
					break;
				}
			}
			return hold;
		}
		return -1;
		
	}
	
	public double compare(Sequence one,Sequence other, String measure) {
		if(measure =="euclidean") return one.compareEuclid(other,start,end);
		if(measure =="maximum") return one.compareMaximum(other,start,end);
		if(measure =="absolute") return one.compareAbsolute(other,start,end);
		if(measure =="manhattan") return one.compareManhattan(other,start,end);
		if(measure =="minkowski") return one.compareMinkowski(other,start,end);
		if(measure =="cosine") return one.compareCosine(other,start,end);
		if(measure =="rms") return one.compareRMS(other,start,end);
		else
			
			return -10;
	}

	public void merge(Node tree, int a, int b, double sim) {
		// merging two clusters together by creating a new one and removing the previous branches
		if(a==b)return;
		ArrayList<Node> bs = new ArrayList<Node>();
		bs.add(new Node(tree.getBranch(a)));
		bs.add(new Node(tree.getBranch(b)));
//		System.out.println(sim);
		Node merged = new Node(bs,sim,original.length);
		tree.branches.remove(tree.getBranch(b));
		tree.branches.remove(tree.getBranch(a));
		tree.branches.add(merged);
	}

	
	
	

	public void makeSections(int maxsim,Cluster other) {

		group_count = maxsim;

		flat.sections = other.flat.sections;
		flat.densities = other.flat.densities;
		flat_c = new Bundle(flat);
	}
	
	
	
	public void makeSectionsSimMedian(int median) {
		// intakes similarity percentage value and sectiones by median similarity
		group_count = median;

		// determine median similarity
		ArrayList<Double> sims = tree.getSimilarities();
		int med_position = sims.size()-group_count-1;
		// sort similarites
		Collections.sort(sims);
		if(med_position>sims.size()||med_position<0)
		{
			System.out.println("something went wrong with calculating median similarity");
			System.out.println(sims.size()+" similarities found, investigating at: "+med_position);
			return;
		}
		double sim_thresh = sims.get(med_position);
				
		double maxsim_local = sim_thresh;
		System.out.println("Sectioning: "+name);
		System.out.println("Sectioning with at: "+med_position+", with similarity: " + maxsim_local);
		ArrayList<Node> groups = new ArrayList<Node>();
		ArrayList<Node> singles = new ArrayList<Node>();
		ArrayList<Node> plane = tree.branches;
		for (int i = 0; i < 100000; i++) {
			ArrayList<Node> hold = new ArrayList<Node>();
			boolean lastleaf = true;
			for (Node cc : plane) {
				for (Node c : cc.branches) {
					if (!c.isLeaf)
						lastleaf = false;
				}
			}

			for (Node cc : plane) {
				for (Node c : cc.branches) {
					if (!c.isLeaf) {
						if (c.similarity <= maxsim_local)
							groups.add(c);
						else
							hold.add(c);
					}
					else singles.add(c);
				}
			}
			plane = hold;
			if (lastleaf) {
				
				flat.sequences = new ArrayList<Sequence>();
				int pos = 0;
				ArrayList<Integer> secs = new ArrayList<Integer>();
				ArrayList<Integer> dens = new ArrayList<Integer>();
				for (Node c : groups) {
					for (Sequence s : c.getFlatBranchesDepth().sequences) {
						flat.add(s);
					}
					dens.add(c.getFlatBranchesDepth().sequences.size());
					pos += c.getFlatBranchesDepth().sequences.size();
					secs.add(pos);

				}
//				for(Node n: singles) {pos++;flat.add(n.data);secs.add(pos);}// if we dont look at singles like own groups
				for(Node n: singles) {flat.add(n.data);}secs.add(pos+singles.size());
				flat.sections = secs;
				flat.densities = dens;
				flat_c = new Bundle(flat);
				return;

			}
		}

		System.err.println("something went wrong doing sectioning");

	}


	public static void serializeDataOut(String savepath,String name, Node ish) throws IOException {
		FileOutputStream fos = new FileOutputStream(savepath+"/"+name);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(ish);
		oos.close();
	}

	public static Node serializeDataIn(String savepath, String name) throws IOException, ClassNotFoundException {
		FileInputStream fin = new FileInputStream(savepath+"/"+name);
		BufferedInputStream bis = new BufferedInputStream(fin);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Node loadRoot = (Node) ois.readObject();
		ois.close();
		return loadRoot;
	}

}
