package pixelvisu.visu;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;


public class Data {		
	ArrayList<SingleData> data = new ArrayList<SingleData>();
	
	int dataPointer=0;
	int clusterPointer =0;
	
	int group_count= 4;
	String section = "similarity";
	boolean contrast;
	Scale sc;
	ColorMapping cm;
	Circuit circ;
	int start, end; 
//	TreePanel p;

	
//	String[] paths = new String[] {"Data/4w_14_9_1h/node_memory_Active_bytes.txt","Data/4w_14_9_1h/node_memory_active_file_bytes.txt","Data/4w_14_9_1h/node_memory_Cached_bytes.txt"};
//	String base_path = "Data/ipmi_1w_1.12_1h/";
	
//	String[] paths = new String[] {
//			"current.txt",
//			"fan_speed.txt",
//			"power_supply_status.txt",
//			"temperatures.txt",
//			"voltages.txt"};

	String base_path = "Data/1w_14_9_1h/";
	String [] paths = new String[] {
		"Active_bytes.txt",
		"active_file_bytes.txt",
		"Cached_bytes.txt"
		
	};
	
	// Compare Methods
	

	
	public Data(int width, int height, Scale s, ColorMapping m)  {
		circ = new Circuit();
//		p = new TreePanel();
		cm = m;
		sc =s;
		setBounds(0, 10000);

//		setBounds(388,72);
		try {
			for(String p:paths) {
				
			SingleData data_main = new SingleData(base_path+p, group_count,Color.orange,cm);

			data.add(data_main);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateClustering();
		
	}

	public void updateClustering() {
		clusterPointer= dataPointer;
		data.get(dataPointer).updateClustering(circ.getCircuit(), group_count,start,end);
		updateSection();
		cm.repaint();
		
	}
	
	public void updateSection() {
		// updates locked section and then projection on the others
		data.get(clusterPointer).section(group_count,section);
		for(int i = 0; i<data.size();i++)
			if(i==clusterPointer) {}
			else data.get(i).update(data.get(clusterPointer).c);
		
		
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
	
	public void setDataPath(String path) {
		dataPointer = 0;
		for(int i =0;i<data.size();i++) {
//			System.out.println(data.get(i).path+" "+base_path+path);
			if(data.get(i).path.equals(base_path+path))
				dataPointer = i;
			
		}
	}
	public SingleData getMain() {
		return data.get(dataPointer);
	}
	public SingleData getClustering() {
		return data.get(clusterPointer);
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
	


	public int getSectionSize(SingleData d, int dataRowIdx) {
		// TODO Auto-generated method stub
		return d.getSectionSize(dataRowIdx);
	}
	

	
	public int getDiff(SingleData d,int dataRowIdx, int pos) {
		if(d.c==null&&d.c.flat_c==null)return -1;
			return d.c.flat_c.getDiff(dataRowIdx,sc.getScaleIdx(pos) );
		
	}

	public Color getColor(double value) {
		// MAIN COLOR source
		double scale =255;
		value = (value/data.get(dataPointer).max)*scale; // GLOBAL normalizing
		
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
		return data.get(dataPointer).getLength();
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
