package pixelvisu.seriation;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Cluster implements Serializable {

	String name;
	Node tree;
	Node treeorder;
	Group flat;
	Bundle flat_c;

	ArrayList<Group> sections;
	int maxdepth;
	int size;
	int group_count = -10; // SIMILARITY CUT

	public Cluster(Group sequences, String clustering, String link, String sim) throws IOException {
		name = link+sim;
		try {
			tree = serializeDataIn("save/trees/"+name);
		}
		catch(Exception e) {
			tree = new Node(sequences, clustering, link, sim);
			serializeDataOut("save/trees/"+name,tree);
		}
		
		flat = tree.getFlatBranchesDepth();

		System.out.println("Clusterized: "+name+" : size:" + tree.branches.size() + " and Size" + flat.getDepth());
//		tree.printClusterSim();
		try {
			treeorder = serializeDataIn("save/trees/"+name+"_o");
		}
		catch(Exception e) {
			treeorder = new Node(flat, clustering, link, sim);
			serializeDataOut("save/trees/"+name+"_o",treeorder);
		}
//		System.out.println("Ordered Tree");

		System.out.println("Tree Linked\n");//br
	}

	public void makeSections(int maxsim) {
		// iterates over cluster tree and adds all groups with the min cluster length
		if (group_count == maxsim)
			return;
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
//					else singles.add(c);
				}
			}
			plane = hold;
			if (lastleaf) {

				flat = new Group();
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
				for(Node n: singles) {pos++;flat.add(n.data);secs.add(pos);}// if we dont look at singles like own groups
				flat.sections = secs;
				flat.densities = dens;
				flat_c = new Bundle(flat);
				System.out.println("sectioning done");
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

	public static void serializeDataOut(String savepath, Node ish) throws IOException {
		FileOutputStream fos = new FileOutputStream(savepath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(ish);
		oos.close();
	}

	public static Node serializeDataIn(String savepath) throws IOException, ClassNotFoundException {
		FileInputStream fin = new FileInputStream(savepath);
		BufferedInputStream bis = new BufferedInputStream(fin);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Node loadRoot = (Node) ois.readObject();
		ois.close();
		return loadRoot;
	}

}
