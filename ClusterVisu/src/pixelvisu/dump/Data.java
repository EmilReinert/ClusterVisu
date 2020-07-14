package pixelvisu.dump;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;




public class Data {
	Group sequences;// unordered weights // image
	Group sequences_w; //ordered weights
	Group sequences_diff; // ordered by similarity
	
	// COMPRESSION BY WEIGHT
	Bundle compressed;//compressed
	Bundle compressed_w;
	Bundle compressed_diff;
	
	Bundle d_compressed; // density ordered
	Bundle d_compressed_w;
	Bundle d_compressed_diff;
	
	//COMPRESSION BY SIMILARITY
	Bundle scompressed;//compressed
	Bundle scompressed_w;
	Bundle scompressed_diff;
	
	Bundle d_scompressed; // density ordered
	Bundle d_scompressed_w;
	Bundle d_scompressed_diff;
	
	int group_count = -1;
	
	public Data(String path) throws IOException {
		sequences = new Group(group_count);

		readData(path);
//		testCat();
		sequences_w = new Group(sequences);
		sequences_w.weightOrder();
		
		sequences_diff = new Group(sequences_w);
		sequences_diff.differOrder();
	}
	
	private void readData(String path) throws IOException {
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
	
	
	public int getColor(int value) {
		if(value>255)
			value = 255;
		if(value>=0)
			return new Color(0,(int)(value),(int)(value),1).getRGB();
		return Color.BLACK.getRGB();
	}

	public int getColor(Sequences seqs, int row, int idx) {
		
		int value =  getData(seqs, row, idx);
		return getColor(value);
	}
	
	public int getOrColor(Bundle seqs, int sec_idx, int row, int idx) {
	
	int value =  seqs.getOriginal(sec_idx, row, idx);
	return getColor(value);
}

	
	
	public void compress(int group_count) {
		if(this.group_count == group_count) {
			return;
		}
		
		//
		compressed = new Bundle(sequences,group_count,"w");
		compressed_w = new Bundle(sequences_w,group_count,"w");
		compressed_diff = new Bundle(sequences_diff,group_count,"w");

		
		d_compressed =  new Bundle(compressed);
		d_compressed.compressOrder();

		d_compressed_w =  new Bundle(compressed_w);
		d_compressed_w.compressOrder();
		
		d_compressed_diff =  new Bundle(compressed_diff);
		d_compressed_diff.compressOrder();
		
		
		//
		scompressed = new Bundle(sequences,group_count,"s");
		scompressed_w = new Bundle(sequences_w,group_count,"s");
		scompressed_diff = new Bundle(sequences_diff,group_count,"s");

		
		d_scompressed =  new Bundle(scompressed);
		d_scompressed.compressOrder();

		d_scompressed_w =  new Bundle(scompressed_w);
		d_scompressed_w.compressOrder();
		
		d_scompressed_diff =  new Bundle(scompressed_diff);
		d_scompressed_diff.compressOrder();
		

		this.group_count = group_count;
		
	}
	
	
	public int getData(Sequences seqs, int row, int idx) {
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
	
	
	public Vec2 getColorBounds() {
		//returns possivle bounds
		return new Vec2( sequences.getLength()*(Color.white.getRGB()/1000000), sequences.getLength()*(Color.BLACK.getRGB()/1000000));
	}
	
}
