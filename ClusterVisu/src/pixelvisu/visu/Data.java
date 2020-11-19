package pixelvisu.visu;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import pixelvisu.pipeline.Cluster;

public class Data {		
	ArrayList<SingleData> data = new ArrayList<SingleData>();
	int lockedPointer;
	int group_count= 44;
	String section = "similarity";
	boolean contrast;
	Scale sc;
	ColorMapping cm;
	Circuit circ;
	int start, end; 
//	TreePanel p;

	String maindata_path ="Data/4w_14_9_1h/node_memory_Active_bytes.txt"; 
	String comparedata_path = "Data/4w_14_9_1h/node_memory_active_file_bytes.txt";
	String comparedata_two = "Data/4w_14_9_1h/node_memory_Cached_bytes.txt";
	
	
	// Compare Methods
	

	SingleData single_euclid;
	SingleData single_maximum;
	SingleData single_weight;
	SingleData single_trivial;
	SingleData single_manhattan;

	SingleData complete_euclid;
	SingleData complete_maximum;
	SingleData complete_weight;
	SingleData complete_trivial;
	SingleData complete_manhattan;
	
	SingleData average_euclid;
	SingleData average_maximum;
	SingleData average_weight;
	SingleData average_trivial;
	SingleData average_manhattan;
	
	public Data(int width, int height, Scale s, ColorMapping m)  {
		circ = new Circuit();
//		p = new TreePanel();
		cm = m;
		start = 0; end = 100000000;
		try {
			SingleData data_main = new SingleData(maindata_path, circ.getCircuit(), group_count,Color.green,cm,  start, end);
			SingleData data_compare = new SingleData(comparedata_path, group_count, Color.cyan, cm);
			SingleData data_compare_two = new SingleData(comparedata_two, group_count, Color.orange, cm);
			data.add(data_main);
			data.add(data_compare);
			data.add(data_compare_two);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateSection();
		sc =s;
		
	}

	public void updateClustering() {
		data.get(lockedPointer).updateClustering(circ.getCircuit(), group_count,start,end);
		updateSection();
		
	}
	
	public void updateSection() {
		for(int i = 0; i<data.size();i++)
			if(i==lockedPointer) 
				data.get(i).section(group_count,section);
			else data.get(i).update(data.get(lockedPointer).c);
		
		
//		p.update(data_main.c, group_count);
		contrast=!contrast;
		contrast();
		cm.repaint();
		
	}
	public void up() {
		circ.up();
	}
	public void down() {
		circ.down();
	}
	public SingleData getMain() {
		return data.get(lockedPointer);
	}
	
	
	
	public double getValue(SingleData d,int dataRowIdx, int pos) {
		//for string
		return d.c.flat_c.get(dataRowIdx,sc.getScaleIdx(pos) );
	}
	public double getOrValue(SingleData d,int sec_idx, int row, int idx) {
//		System.out.println(sc.getScaleIdx(idx));
		return d.getOrValue(sec_idx, row, sc.getScaleIdx(idx));
	}
	public String getOrNode(SingleData d,int sec_idx, int row) {
		return d.getOrNode(sec_idx, row);
	}
	
	public Color getColor(SingleData d,int dataRowIdx, int pos) {
		if(d==null)return null;
		if(d.c.flat_c==null)return null;
		return getColor(d.c.flat_c.get(dataRowIdx,sc.getScaleIdx(pos) ));
	}

	public Color getOrColor(SingleData d,int sec_idx, int row, int idx) {
		if(d.c==null&&d.c.flat_c==null)return null;
		double value = d.c.flat_c.getOriginal(sec_idx, row,sc.getScaleIdx(idx));
		return getColor(value);/////
	}
	
	

	
	public int getDiff(SingleData d,int dataRowIdx, int pos) {
		if(d.c==null&&d.c.flat_c==null)return -1;
			return d.c.flat_c.getDiff(dataRowIdx,sc.getScaleIdx(pos) );
		
	}

	public Color getColor(double value) {
		// MAIN COLOR source
		double scale =255;
		value = (value/data.get(lockedPointer).max)*scale; // GLOBAL normalizing
		
		Color hold = colorScale(value);
		return hold;//
			
	}
	
	public Color colorScale(double val) {
		int value = cm.color((int) val);
		int r,g,b;
		if(value<0)value=0;
		if(value>255)value=255;
		
		if (contrast) {
			if (value < 127.5) {
				b = 255;
				g = (int) (value + 127.5);
				r = (int) (2 * value);
			} else {
				b = (int) (-2 * value) + 255 * 2;
				g = (int) (-2 * value) + 255 * 2;
				r = 255;
			}
		} else {
			r=g=b= value;

		}
		
		if(r>255) r = 255;
		if(g>255) g = 255;
		if(b>255) b = 255;
		if(r<0)r=0;
		if(g<0)g=0;
		if(b<0)b=0;
//			Color hold = new Color(255-(int)value,(int)(-(1/184)*(value-125)*(value-125)+125),(int)value,255);
		return new Color(r,g,b,255);
	}
	
	
	
	
	
	
	public void order(String mode) {
		updateSection();
		for(SingleData d: data)
			d.order(mode);
	}
	
	public int getLength() {
		return data.get(lockedPointer).getLength();
	}
	
	public void contrast() {
		if (contrast) {
			contrast = false;
		}
		else {
			contrast = true;
		}
		cm.repaint();
	}


	public static Color combineColors(Color color1, Color color2, float ra) {
		float rb = 1-ra;
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

	public boolean isSelected(int i) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setBounds(int x, int y) {
		// TODO Auto-generated method stub
		start = x; end = y;
		sc.setBounds(x,y);
//		System.out.println("new bounds "+start+" "+end);
	}

}
