package pixelvisu.data;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

public class SingleData {
	Group sequences;// unordered weights // image
	Cluster c;
	
	double min;
	double max;
	boolean contrast = false;
	String path = "";
	String dataname="error";
	int group_count = 0;
	Color mc= Color.green;
	Color hold;
	
	public SingleData(String path,String []circ, int gc) throws IOException {
		update(path,circ,gc);
		this.path = path;
	}
	
	public void update(String path,String []circ, int gc) throws IOException {

		this.group_count = gc;
		sequences = new Group(group_count);
		
		readData(path);

		dataname = path.substring(path.lastIndexOf("/") + 1);

		updateClustering(circ);
	}
	
	public void updateClustering( String []circ) {
		
		if(circ.length!=3)
			System.err.println("wrong circuit");
		try {
			System.out.println("clusering new circuit");
		c = new Cluster(sequences,circ[0],circ[1],circ[2],dataname,true);}
		catch(Exception e) {
			System.err.println("circuit not found");
		}
		
		
	}
	public SingleData(String path, Cluster other, Color col) throws IOException {
		// Compare Data
		mc = col;
		sequences = new Group(group_count);
		readData(path);
		this.path = path;
		dataname = path.substring(path.lastIndexOf("/") + 1);
		
		c = new Cluster(sequences, other);
		c.makeSections(group_count,other);
	}

	

	protected void readData(String path) throws IOException {
		try {
			sequences = serializeDataIn("save/data", path);
		} 
		catch (Exception e) {
			// reading data 
			dataname = path.substring(path.lastIndexOf("/")+1);
			System.out.println("READ");
			String file =new TxtReader().read(path);
			Pattern p = Pattern.compile("\"([^\"]*)\"");
			Matcher m = p.matcher(file);
			
			int count = 0;
			while (m.find()) {
				if(m.group(1).length()>100) {
	//				System.out.println(m.group(1));
					sequences.add(new Sequence(m.group(1),count));
					count++;
				}
			}
			//saving data
			serializeDataOut("save/data", path,sequences);
			
		
		}
		System.out.println("------READ------");
		
		// getting global min+max
		min = 10000000;
		max = -10000000;
		for(Sequence s:sequences.sequences) {
			if(s.getMin()<min) min = s.getMin();
			if(s.getMax()>max) max = s.getMax();
		}
	}
	
	
	public void section (int group_count,String mode) {
		this.group_count = group_count;
		System.out.println("Sectioning by "+mode+" Group Count = "+group_count+";");
		System.out.println(c.name);
		if(mode == "similarity")
			c.makeSectionsSim(group_count);
		if(mode =="size")
			c.makeSectionsSize(group_count);
		
	}
	
	public void order(String mode) {
		if(mode == "density")c.flat_c.densityOrder();
		if(mode == "weight")c.flat_c.weightOrder();
			
		
	}
	
	
	public Color getColor(double value) {
		// MAIN COLOR source
		double scale =255;
//		
		if(!contrast)value = ((value-min)/(max-min))*scale;
		
		if(value>=255)
			value = 255;
		if(value>=0) {
//			if(value<min||value>max) {return Color.red.getRGB();} ;
			hold = new Color((int)value,(int)value,(int)value);
			return combineColors(mc,hold,0.6f,0.4f);}
		return null;
	}
	public double getValue( int row, int idx) {
		double value =getData(sequences, row, idx);
		return value;
	}
	
	
	
	public Color getOrColor(int sec_idx, int row, int idx) {
	
		double value;
		if(contrast) {
			value= c.flat_c.getOriginalContrast(sec_idx, row, idx);//System.out.println(value);
		}
		else value = c.flat_c.getOriginal(sec_idx, row, idx);
		return getColor(value);/////
	}
	
	public double getOrValue(int sec_idx, int row, int idx) {
		return c.flat_c.getOriginal(sec_idx, row, idx);
	}

	
	
	
	public double getData(Group seqs, int row, int idx) {
		if(row>= seqs.getDepth()||idx>= seqs.getLength()) {
			return -1;
		}
		if(row<0||idx<0)
			return -1;
		if(!contrast)
			return seqs.get(row,idx);
		return seqs.getContrast(row,idx);
	}
	
	
	
	public void setColor(Color c) {
		mc=c;
	}
//	public static Color multiplyColors(Color color1, Color color2) {
//		float r1 = color1.getRed() / 255.0f;
//		float g1 = color1.getGreen() / 255.0f;
//		float b1 = color1.getBlue() / 255.0f;
//		float a1 = color1.getAlpha() / 255.0f;
//
//		float r2 = color2.getRed() / 255.0f;
//		float g2 = color2.getGreen() / 255.0f;
//		float b2 = color2.getBlue() / 255.0f;
//		float a2 = color2.getAlpha() / 255.0f;
//		float r3 = r1 * r2;if(r3>255)r3=255;
//		float g3 = g1 * g2;if(g3>255)g3=255;
//		float b3 = b1 * b2;if(b3>255)b3=255;
//		float a3 = a1 * a2;if(b3>255)b3=255;
//		Color color3 = new Color((float) r3 ,(float) g3 ,(float) b3 ,(float) a3 );
//		return color3;
//	}
	
	public static Color combineColors(Color color1, Color color2, float ra, float rb) {
		float r1 = color1.getRed() / 255.0f;
		float g1 = color1.getGreen() / 255.0f;
		float b1 = color1.getBlue() / 255.0f;
		float a1 = color1.getAlpha() / 255.0f;

		float r2 = color2.getRed() / 255.0f;
		float g2 = color2.getGreen() / 255.0f;
		float b2 = color2.getBlue() / 255.0f;
		float a2 = color2.getAlpha() / 255.0f;
		
		float r3 = r1 * r2;//if(r3>255)r3=255;
		float g3 = g1 * g2;//if(g3>255)g3=255;
		float b3 = b1 * b2;//if(b3>255)b3=255;
		float a3 = a1 * a2;//if(a3>255)a3=255;
		
		float r4 = ra*r3 +rb* r2;//if(r4>255)r3=255;
		float g4 = ra*g3 +rb* g2;//if(g4>255)g3=255;
		float b4 = ra*b3 +rb* b2;//if(b4>255)b3=255;
		float a4 = ra*a3 +rb* a2;//if(a4>255)a3=255;
		Color color4 = new Color((float) r4 ,(float) g4 ,(float) b4 ,(float) a4  );
		return color4;
	}
	public int getLength() {
		return sequences.get(0).getLength();
	}
	public int getWeight(int i) {
		return sequences.get(i).getWeight();
	} 
	
	
	public static void serializeDataOut(String savepath,String name, Group ish) throws IOException {
		if(name.contains("/")) name = name.split("/")[name.split("/").length-1];
		
		FileOutputStream fos = new FileOutputStream(savepath+"/"+name);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(ish);
		oos.close();
	}

	public static Group serializeDataIn(String savepath, String name) throws IOException, ClassNotFoundException {
		if(name.contains("/")) name = name.split("/")[name.split("/").length-1];
		FileInputStream fin = new FileInputStream(savepath+"/"+name);
		BufferedInputStream bis = new BufferedInputStream(fin);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Group loadRoot = (Group) ois.readObject();
		ois.close();
		return loadRoot;
	}
}
