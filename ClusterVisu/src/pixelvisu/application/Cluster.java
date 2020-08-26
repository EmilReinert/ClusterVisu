package pixelvisu.application;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Cluster implements Serializable {

	String name;
	Group original; 
	Node tree;
	Node treeorder;
	Group flat;
	Bundle flat_c;

	int maxdepth;
	int size;
	int group_count = -10; // SIMILARITY CUT

	public Cluster(Group sequences, String clustering, String link, String sim, String dataname, boolean save) throws IOException {
		// Cluster creation (saving/loading) happens here
		// Tree structure is defined as linked node instances
		original = sequences;
		name = dataname + "/" + clustering + link + sim;

		//  MAKING FILES
		if (save) {
			try {
				tree = serializeDataIn("save/trees", name);
			} catch (Exception e) {
				tree = new Node(sequences);
				tree.clusterize(clustering, link, sim);
				serializeDataOut("save/trees" , name, tree);
			}
			flat = tree.getFlatBranchesDepth(); // untangles tree structure and returns the flattened tree
			System.out.println(
					"Clusterized: " + name + " : size:" + tree.branches.size() + " and Size" + flat.getDepth());
			
			
			/*
			 * FOR TREE DRAWING
			try {
				treeorder = serializeDataIn("save/trees", name + "_o");
			} catch (Exception e) {
				treeorder = new Node(flat);
				treeorder.clusterize(clustering, link, sim);
				serializeDataOut("save/trees" , name  + "_o", treeorder);
			}
			System.out.println("Tree Linked\n");
			*/
		} 
		
		// READING EXISTING FILES
		else {
			try {
				tree = serializeDataIn("save/trees", name);
			} catch (Exception e) {
				System.err.println("Error reading node file: "+name);
			}
			flat = tree.getFlatBranchesDepth();
			System.out.println(
					"Clusterized: " + name + " : size:" + tree.branches.size() + " and Size" + flat.getDepth());
			
			try {
				treeorder = serializeDataIn("save/trees", name + "_o");
			} catch (Exception e) {
				System.err.println("Error reading tree file: "+name);
			}
			System.out.println("Tree Linked\n");

		}
	}

	public Cluster(Group sequences,Cluster other) {
		// projects internal cluster structure to this clusters sequences
		original = sequences;
		flat = new Group(other.flat);
		ArrayList<Sequence> seq = new ArrayList<Sequence>();
		ArrayList<Integer> mapping = other.original.getMapping(other.flat);
		for(Sequence s:other.flat.sequences) {
			seq.add(sequences.get(s.pos));
		}
		flat.sequences=seq;
		
		flat_c = new Bundle(flat);
		
		// TODO copy depth and not just flat
		
	}
	

	public void makeSections(int maxsim,Cluster other) {

		group_count = maxsim;

		flat.sections = other.flat.sections;
		flat.densities = other.flat.densities;
		flat_c = new Bundle(flat);
	}
	
	public void makeSectionsSim(int maxsim) {
		// iterates over cluster tree and adds all groups with the min cluster length
		group_count = maxsim;

		double maxsim_local = tree.getMaxSim() * 0.01 * maxsim;
		System.out.println("Sectioning with similarity: " + maxsim_local);
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
						if (c.similarity < maxsim_local)
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
	
	public void makeSections(int maxsim) {
		// iterates over cluster tree and adds all groups with the min cluster length
		group_count = maxsim;

		int groupcount = (int) ( maxsim);
		System.out.println("Sectioning with group count: " + groupcount);
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
						if (c.getSize() < groupcount)
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
	
	public float getData(int idx) {
		// returns average data
		float sum = 0;
		for (Sequence branch : flat.sequences) {
			sum += branch.get(idx);
		}
		return sum / flat.getDepth();
	}

	public int getData(int row, int idx) {
		// returns average data
		return (int) flat.get(row).get(idx);
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
