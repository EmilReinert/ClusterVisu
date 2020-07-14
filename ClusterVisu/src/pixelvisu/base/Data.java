package pixelvisu.base;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

public class Data {
	Group sequences;// unordered weights
	Group sequences_o; //ordered weights
	
	Bundle compressed;//compressed
	Bundle compressed_o;
	
	Bundle d_compressed; // density ordered
	Bundle d_compressed_o;
	
	int group_count = -1;
	
	public Data() throws IOException {
		sequences = new Group();
		sequences_o = new Group();
		testCat();
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
				se.add(-c.getRGB());
				//System.out.println(c.getRGB()+10000*Color.white.getRGB());
			}
			sequences.add(se);
			//System.out.println(se.toString());
		}
		System.out.println("Image dimensions: "+image.getHeight()+" "+image.getWidth());
		sequences_o = new Group(sequences);
		sequences_o.weightOrder();
	}
	
	
	public int getColor(int x, int y) {
		
		return get(x,y);
	}
	
	
	public void compress(int group_count) {
		if(this.group_count == group_count) {
			return;
		}
		
		compressed = new Bundle(sequences,group_count);
		compressed_o = new Bundle(sequences_o,group_count);

		d_compressed =  new Bundle(compressed);
		d_compressed.compressOrder();

		d_compressed_o =  new Bundle(compressed_o);
		d_compressed_o.compressOrder();
		

		this.group_count = group_count;
		
	}
	
	public int get(int row, int idx) {
		if(row>= sequences.getDepth()) {
			return getOrdered(row- sequences.getLength(), idx);
			
		}
		if(idx>= sequences.getLength()) {
			return -1;
		}
		return sequences.get(row).get(idx);
	}
	
	public int getOrdered(int row, int idx) {
		if(row>= sequences_o.getDepth()||idx>= sequences_o.getLength()) {
			return -1;
		}
		return sequences_o.get(row).get(idx);
	}
	
	
	public int getData(Sequences seqs, int row, int idx) {
		if(row>= seqs.getDepth()||idx>= seqs.getLength()) {
			return -1;
		}
		return seqs.get(row).get(idx);
	}
	
	public int get(Vec2 v) {
		return get((int)v.x,(int) v.y);
	}
	
	public int getDepth() {
		return sequences.getDepth();
	}
	public int getLength() {
		return sequences.get(0).getLength();
	}
	public int getWeight(int i) {
		return sequences.get(i).getWeight();
	}
	public int getWeightO(int i) {
		return sequences_o.get(i).getWeight();
	}
	public int getMaxWeight() {
		int max =-500000; 
		for(int i = 0; i<getDepth();i++) {
			for(int j = i; j<getDepth();j++) {
				if( max<sequences.get(j).getWeight()) {
					max = sequences.get(j).getWeight();
					}
			}
		}
		return max;
	}
	
	public Vec2 getColorBounds() {
		//returns possivle bounds
		return new Vec2( sequences.getLength()*(Color.white.getRGB()/1000000), sequences.getLength()*(Color.BLACK.getRGB()/1000000));
	}
	
}
