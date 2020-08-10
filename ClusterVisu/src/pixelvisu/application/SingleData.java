package pixelvisu.application;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

public class SingleData {
	Group sequences;// unordered weights // image
	
	double min;
	double max;
	
	Cluster c;
	
	String dataname="error";
	int group_count = 44;
	
	public SingleData(String path) throws IOException {
		update(path,44);
		
	}
	
	public void update(String path, int gc) throws IOException {
		this.group_count = gc;
		sequences = new Group(group_count);
		
		readData(path);
//		testCat();
//		testDataLinear();
//		testDataRandom();

		dataname = path.substring(path.lastIndexOf("/") + 1);

		c = new Cluster(sequences, "agglomerative", "single", "euclidean", dataname, true);
	}
	
	public void updateClustering( String []circ) throws IOException {
		
		if(circ.length!=3)
			System.err.println("wrong circuit");
		c = new Cluster(sequences,circ[0],circ[1],circ[2],dataname,true);
		
		
	}
	public SingleData(String path, Cluster other) throws IOException {
		sequences = new Group(group_count);
		
		readData(path);
//		testCat();
//		testDataLinear();
//		testDataRandom();

		dataname = path.substring(path.lastIndexOf("/") + 1);

		c = new Cluster(sequences, other);
		c.makeSections(group_count,other);
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
			se = new Sequence(node0.optJSONArray("values"),181,node0.optJSONObject("metric"),i);
			sequences.add(se);
		}
		
		// getting global min+max
		min = 10000000;
		max = -10000000;
		for(Sequence s:sequences.sequences) {
			if(s.getMin()<min) min = s.getMin();
			if(s.getMax()>max) max = s.getMax();
		}
		System.out.println(min+" "+max);

	}

	protected void testDataLinear() {
		dataname = "Linear";
		for (int i = 0; i<270;i++) {
			sequences.add(new Sequence(181,i));
		}
	}
	protected void testDataRandom() {
		dataname = "Random";
		for (int i = 0; i<270;i++) {
			sequences.add(new Sequence(181,i,"random"));
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
		this.group_count = group_count;
		
			c.makeSections(group_count);
	}
	
	public void order(String mode) {
		if(mode == "density")c.flat_c.densityOrder();
			
		
	}
	
	
	public int getColor(double value) {
		double scale = (254/(max-min));
//		
//		value = ((value-min)*scale);
		if(value>255)
			value = 255;
		if(value>=0) {
			if(value<min)return 100000;
			if(value>max) {//System.out.println(value+" "+max);
			return 200000;}
			value =((value-min)*scale);
			return new Color(0,(int)(value),(int)(value),1).getRGB();}
		return Color.BLACK.getRGB();
	}

	public int getColor(Group seqs, int row, int idx) {
		double value =  getData(seqs, row, idx);
//		System.out.println(value);
		return getColor(value);
	}
	public int getOrColor(Bundle seqs, int sec_idx, int row, int idx) {
	
	double value =  seqs.getOriginal(sec_idx, row, idx);
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
