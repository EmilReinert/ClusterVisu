package pixelvisu.application;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.json.JSONArray;
import org.json.JSONObject;




public class Data implements Serializable{
	Group sequences;// unordered weights // image
	Group sequences_diff; // ordered by similarity
	
	ArrayList<Cluster> clusters;
	
	Cluster single_euclid;
	Cluster single_maximum;
	Cluster single_weight;
	Cluster single_trivial;
	Cluster single_manhattan;

	Cluster complete_euclid;
	Cluster complete_maximum;
	Cluster complete_weight;
	Cluster complete_trivial;
	Cluster complete_manhattan;
	
	Cluster average_euclid;
	Cluster average_maximum;
	Cluster average_weight;
	Cluster average_trivial;
	Cluster average_manhattan;
	/*
	single_euclid
	single_maximum
	single_weight
	single_trivial
	single_manhattan

	complete_euclid
	complete_maximum
	complete_weight
	complete_trivial
	complete_manhattan
	
	average_euclid
	average_maximum
	average_weight
	average_trivial
	average_manhattan
	*/
	int group_count = -1;
	String dataname = "error";
	
	public Data() {
		
	}
	
	public Data(String path) throws IOException {
		sequences = new Group(group_count);
		
		readData(path);
//		testCat();
//		testDataLinear();
//		testDataRandom();
		
		clusters = new ArrayList<Cluster>();
		String dataname = path.substring(path.lastIndexOf("/")+1);
		
		single_euclid = new Cluster(sequences,"agglomerative","single","euclidean",dataname,true);
		single_maximum = new Cluster(sequences,"agglomerative","single","maximum",dataname,true);
		single_weight = new Cluster(sequences,"agglomerative","single","weight",dataname,true);
		single_trivial = new Cluster(sequences,"agglomerative","single","trivial",dataname,true);
		single_manhattan = new Cluster(sequences,"agglomerative","single","manhattan",dataname,true);
		
		
		complete_euclid = new Cluster(sequences,"agglomerative","complete","euclidean",dataname,true);
		complete_maximum = new Cluster(sequences,"agglomerative","complete","maximum",dataname,true);
		complete_weight = new Cluster(sequences,"agglomerative","complete","weight",dataname,true);
		complete_trivial = new Cluster(sequences,"agglomerative","complete","trivial",dataname,true);
		complete_manhattan = new Cluster(sequences,"agglomerative","complete","manhattan",dataname,true);
		
		
		average_euclid = new Cluster(sequences,"agglomerative","average","euclidean",dataname,true);
		average_maximum = new Cluster(sequences,"agglomerative","average","maximum",dataname,true);
		average_weight = new Cluster(sequences,"agglomerative","average","weight",dataname,true);
		average_trivial = new Cluster(sequences,"agglomerative","average","trivial",dataname,true);
		average_manhattan = new Cluster(sequences,"agglomerative","average","manhattan",dataname,true);
		

		clusters.add(single_euclid);
		clusters.add(single_maximum);
		clusters.add(single_weight);
		clusters.add(single_trivial);
		clusters.add(single_manhattan);

		clusters.add(complete_euclid);
		clusters.add(complete_maximum);
		clusters.add(complete_weight);
		clusters.add(complete_trivial);
		clusters.add(complete_manhattan);

		clusters.add(average_euclid);
		clusters.add(average_maximum);
		clusters.add(average_weight);
		clusters.add(average_trivial);
		clusters.add(average_manhattan);
		
	}
	
	public void update(String path) throws IOException {
		sequences = new Group(group_count);
		
		readData(path);
//		testCat();
//		testDataLinear();
//		testDataRandom();
		
		clusters = new ArrayList<Cluster>();
		String dataname = path.substring(path.lastIndexOf("/")+1);
		
		single_euclid = new Cluster(sequences,"agglomerative","single","euclidean",dataname,true);
		single_maximum = new Cluster(sequences,"agglomerative","single","maximum",dataname,true);
		single_weight = new Cluster(sequences,"agglomerative","single","weight",dataname,true);
		single_trivial = new Cluster(sequences,"agglomerative","single","trivial",dataname,true);
		single_manhattan = new Cluster(sequences,"agglomerative","single","manhattan",dataname,true);
		
		
		complete_euclid = new Cluster(sequences,"agglomerative","complete","euclidean",dataname,true);
		complete_maximum = new Cluster(sequences,"agglomerative","complete","maximum",dataname,true);
		complete_weight = new Cluster(sequences,"agglomerative","complete","weight",dataname,true);
		complete_trivial = new Cluster(sequences,"agglomerative","complete","trivial",dataname,true);
		complete_manhattan = new Cluster(sequences,"agglomerative","complete","manhattan",dataname,true);
		
		
		average_euclid = new Cluster(sequences,"agglomerative","average","euclidean",dataname,true);
		average_maximum = new Cluster(sequences,"agglomerative","average","maximum",dataname,true);
		average_weight = new Cluster(sequences,"agglomerative","average","weight",dataname,true);
		average_trivial = new Cluster(sequences,"agglomerative","average","trivial",dataname,true);
		average_manhattan = new Cluster(sequences,"agglomerative","average","manhattan",dataname,true);
		

		clusters.add(single_euclid);
		clusters.add(single_maximum);
		clusters.add(single_weight);
		clusters.add(single_trivial);
		clusters.add(single_manhattan);

		clusters.add(complete_euclid);
		clusters.add(complete_maximum);
		clusters.add(complete_weight);
		clusters.add(complete_trivial);
		clusters.add(complete_manhattan);

		clusters.add(average_euclid);
		clusters.add(average_maximum);
		clusters.add(average_weight);
		clusters.add(average_trivial);
		clusters.add(average_manhattan);
		
	}
	
	protected void readData(String path) throws IOException {
		dataname = path.substring(path.lastIndexOf("/")+1);
		System.out.println("READ");
		String file =new TxtReader().read(path);
		JSONObject obj = new JSONObject(file);
		System.out.println("------READ------");
//		System.out.println(obj.toString());

		//ASSIGNING values
		JSONArray nodes = obj.getJSONArray("nodes");
		JSONObject node0;
		Sequence se;

		for (int i = 0; i<270;i++) {
			node0 = nodes.getJSONObject(i);
			se = new Sequence(node0.optJSONArray("values"),175,node0.optJSONObject("metric"));
			sequences.add(se);
		}
		

	}

	protected void testDataLinear() {
		dataname = "Linear";
		for (int i = 0; i<270;i++) {
			sequences.add(new Sequence(175,i));
		}
	}
	protected void testDataRandom() {
		dataname = "Random";
		for (int i = 0; i<270;i++) {
			sequences.add(new Sequence(175,i,"random"));
		}
	}
	
	protected void testCat() throws IOException {
		dataname = "Cat";
		// creating test data based on input cat image
		BufferedImage image = ImageIO.read(new File("cat.jpg"));
		byte [] cat_bytes =((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		
		System.out.println(cat_bytes.length);
		//making sequences
		float value =0;int size =256; // 3 because byte is weird
		for(int i =0; i< size*size*3;i+=size*3) {
			Sequence se = new Sequence();
			for(int j = i;j<i+size*3;j+=3){
				//value = (cat_bytes[3 + offset] & 0xFF) | ((cat_bytes[2 + offset] & 0xFF) << 8) |((cat_bytes[1 + offset] & 0xFF) << 16) | ((cat_bytes[0 + offset] & 0xFF) << 24); 
				value =((float)Math.abs(cat_bytes[j])+size/4)/(256*1);
				//System.out.println(value);
				Color c = new Color(1-value,1-value,1-value);
				
				//adding the color value of image as data value
				se.add(-c.getRGB()/100000);
				//System.out.println(c.getRGB()+10000*Color.white.getRGB());
			}
			sequences.add(se);
			//System.out.println(se.toString());
		}
		System.out.println("Image dimensions: "+image.getHeight()+" "+image.getWidth());
	}
	
	
	public void section (int group_count) {
		if(group_count == this.group_count) return;
		this.group_count = group_count;
		
		for(Cluster c:clusters)
			c.makeSections(group_count);
	}
	
	public void order(String mode) {
		for(Cluster c:clusters) {
			if(mode == "density")
				c.flat_c.densityOrder();
			
		}
	}
	
	
	public int getColor(double value) {
		if(value>255)
			value = 255;
		if(value>=0)
			return new Color(0,(int)(value),(int)(value),1).getRGB();
		return Color.BLACK.getRGB();
	}

	public int getColor(Group seqs, int row, int idx) {
		double value =  getData(seqs, row, idx);
//		System.out.println(value);
		return getColor(value);
	}
	public int getOrColor(Group seqs, int sec_idx, int row, int idx) {
	
	double value =  seqs.get( row, idx);
	return getColor(value);
}

	
	
	
	public double getData(Group seqs, int row, int idx) {
		if(row>= seqs.getDepth()||idx>= seqs.getLength()) {
			return -1;
		}
		if(row<0||idx<0)
			return -1;
		return seqs.get(row,idx);
	}
	
	
	public int getLength() {
		return sequences.get(0).getLength();
	}
	public int getWeight(int i) {
		return sequences.get(i).getWeight();
	} 
	

	
}
