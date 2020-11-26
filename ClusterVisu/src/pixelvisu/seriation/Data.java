package pixelvisu.seriation;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.json.JSONArray;
import org.json.JSONObject;

import pixelvisu.visu.Cluster;
import pixelvisu.visu.Group;
import pixelvisu.visu.Node;
import pixelvisu.visu.Sequence;
import pixelvisu.visu.TxtReader;
import pixelvisu.visu.Bundle;




public class Data implements Serializable{
	Group sequences;// unordered weights // image
	Group sequences_diff; // ordered by similarity

	double max =0;
	
	ArrayList<Cluster> clusters;
	
	String dataname = "hi";
	Cluster single_euclid;
	Cluster single_maximum;
	Cluster single_absolute;
	Cluster single_minkowski;
	Cluster single_cosine;
	Cluster single_rms;

	Cluster complete_euclid;
	Cluster complete_maximum;
	Cluster complete_absolute;
	Cluster complete_minkowski;
	Cluster complete_cosine;
	Cluster complete_rms;
	
	Cluster average_euclid;
	Cluster average_maximum;
	Cluster average_absolute;
	Cluster average_minkowski;
	Cluster average_cosine;
	Cluster average_rms;
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
	
	public Data(String path) throws IOException {
		sequences = new Group(group_count);

		readData(path);
//		testCat();
//		testDataLinear();
//		testDataSquare();
//		testDataRandom();
		
		for(Sequence s: sequences.sequences)
			if(s.getMax()>max)
				max = s.getMax();				
		
		clusters = new ArrayList<Cluster>();

		
//		single_euclid = new Cluster(sequences,"agglomerative","single","euclidean","test",0,100000000,true);
//		single_rms = new Cluster(sequences,"agglomerative","single","rms","test",0,100000000,true);
//		single_minkowski = new Cluster(sequences,"agglomerative","single","minkowski","test",0,100000000,true);
//		single_maximum = new Cluster(sequences,"agglomerative","single","maximum","test",0,100000000,true);
//		single_absolute = new Cluster(sequences,"agglomerative","single","absolute","test",0,100000000,true);
//		single_cosine = new Cluster(sequences,"agglomerative","single","cosine","test",0,100000000,true);
//		
		complete_euclid = new Cluster(sequences,"agglomerative","complete","euclidean","test",0,100000000,true);
//		complete_rms = new Cluster(sequences,"agglomerative","complete","rms","test",0,100000000,true);
//		complete_minkowski = new Cluster(sequences,"agglomerative","complete","minkowski","test",0,100000000,true);
//		complete_maximum = new Cluster(sequences,"agglomerative","complete","maximum","test",0,100000000,true);
//		complete_absolute = new Cluster(sequences,"agglomerative","complete","absolute","test",0,100000000,true);
//		complete_cosine = new Cluster(sequences,"agglomerative","complete","cosine","test",0,100000000,true);
		
//		average_euclid = new Cluster(sequences,"agglomerative","average","euclidean","test",0,100000000,true);
//		average_rms = new Cluster(sequences,"agglomerative","average","rms","test",0,100000000,true);
//		average_minkowski = new Cluster(sequences,"agglomerative","average","minkowski","test",0,100000000,true);
//		average_maximum = new Cluster(sequences,"agglomerative","average","maximum","test",0,100000000,true);
//		average_absolute = new Cluster(sequences,"agglomerative","average","absolute","test",0,100000000,true);
//		average_cosine = new Cluster(sequences,"agglomerative","average","cosine","test",0,100000000,true);
		

//		clusters.add(single_euclid);
//		clusters.add(single_maximum);
//		clusters.add(single_absolute);
//		clusters.add(single_minkowski);
//		clusters.add(single_cosine);
//		clusters.add(single_rms);
//
		clusters.add(complete_euclid);
//		clusters.add(complete_maximum);
//		clusters.add(complete_absolute);
//		clusters.add(complete_minkowski);
//		clusters.add(complete_cosine);
//		clusters.add(complete_rms);
//
//		clusters.add(average_euclid);
//		clusters.add(average_maximum);
//		clusters.add(average_absolute);
//		clusters.add(average_minkowski);
//		clusters.add(average_cosine);
//		clusters.add(average_rms);
		
		


	}
	
	protected void readData(String path) throws IOException {
		System.out.println("READ");
		String file =new TxtReader().read(path);
		JSONObject obj = new JSONObject(file);

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
				
		System.out.println(sequences.getLength()+ " dataseries found");
		System.out.println("------READ------");
		
	}

	private void testDataSquare() {

		for (int i = 0; i<270;i++) {
			sequences.add(new Sequence(175,i*i));
		}
	}
	

	private void testDataLinear() {

		for (int i = 0; i<270;i++) {
			sequences.add(new Sequence(175,i));
		}
	}
	
	private void testDataRandom() {

		for (int i = 0; i<270;i++) {
			sequences.add(new Sequence(175,i,"random"));
		}
	}
	
	private void testCat() throws IOException {
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
			c.makeSectionsSimMedian(group_count);
	}
	
	public void sectionMedianSim (int group_count) {
		//takes group count as percentage (0-100) value of max distance 
		//=> 100-group count is similarity
		if(group_count<0)group_count=0;
		if(group_count>100)group_count = 100;
		if(group_count == this.group_count) return;
		this.group_count = group_count;
		
		for(Cluster c:clusters)
			c.makeSectionsSimMedian(group_count);
	}
	
	public void order(String mode) {
		for(Cluster c:clusters) {
			if(mode == "density")
				c.flat_c.densityOrder();
			
		}
	}
	
	
	public int getColor(double value) {
		double scale =255;
		value = (value/max)*scale; // GLOBAL normalizing
		
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
