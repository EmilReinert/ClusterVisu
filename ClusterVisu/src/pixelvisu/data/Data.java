package pixelvisu.data;

import java.awt.Color;
import java.io.IOException;

public class Data {
	SingleData data_main;
	SingleData data_compare; // secondary data to be compared structurally
	SingleData data_compare_two;
	int group_count = 44;
	String section = "similarity";
	boolean contrast;
	Scale sc;
	ColorMapping cm;
	Circuit circ;
	int start, end; 
//	TreePanel p;

	String maindata_path = "Data/4w_14_9_1h/node_memory_Active_bytes.txt";
	String comparedata_path = "Data/4w_14_9_1h/node_memory_active_file_bytes.txt";
	String comparedata_two = "Data/4w_14_9_1h/node_memory_Cached_bytes.txt";
	
	public Data(int width, int height, Scale s, ColorMapping m)  {
		circ = new Circuit();
//		p = new TreePanel();
		cm = m;
		start = 0; end = 100000000;
		try {
			data_main = new SingleData(maindata_path, circ.getCircuit(), group_count,Color.green,cm,  start, end);

			data_compare = new SingleData(comparedata_path, group_count, Color.cyan, cm);
			data_compare_two = new SingleData(comparedata_two, group_count, Color.orange, cm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateSection();
		sc =s;
	}

	public void updateClustering() {
		data_main.updateClustering(circ.getCircuit(), group_count,start,end);
		updateSection();
		
	}
	
	public void updateSection() {
		data_main.section(group_count,section);
		data_compare.update(data_main.c);
		data_compare_two.update(data_main.c);
		
//		p.update(data_main.c, group_count);
		contrast=!contrast;
		contrast();
		
	}
	public void up() {
		circ.up();
	}
	public void down() {
		circ.down();
	}
	
	public double getValue(SingleData d,int dataRowIdx, int pos) {
		//for string
		return d.c.flat_c.get(dataRowIdx,sc.getScaleIdx(pos) );
	}
	public double getOrValue(SingleData d,int sec_idx, int row, int idx) {
		return d.getOrValue(sec_idx, row, sc.getScaleIdx(idx));
	}
	
	public Color getColor(SingleData d,int dataRowIdx, int pos) {
		if(d==null)return null;
		if(d.c.flat_c==null)return null;
		return d.getColor(d.c.flat_c.get(dataRowIdx,sc.getScaleIdx(pos) ));
	}

	public Color getOrColor(SingleData d,int sec_idx, int row, int idx) {
		return d.getOrColor(sec_idx, row, sc.getScaleIdx(idx));
	}
	
	

	public int getDiff(SingleData d,int dataRowIdx, int pos) {
		return d.c.flat_c.getDiff(dataRowIdx,sc.getScaleIdx(pos) );
	}
	
	
	
	
	public void order(String mode) {
		updateSection();
		data_main.order(mode);
		data_compare.order(mode);
		data_compare_two.order(mode);
	}
	
	public int getLength() {
		return data_main.getLength();
	}
	
	public void contrast() {
		if (contrast) {
			contrast = false;
			data_main.contrast = false;
			data_compare.contrast = false;
			data_compare_two.contrast = false;
		}
		else {
			contrast = true;
			data_main.contrast = true;
			data_compare.contrast = true;
			data_compare_two.contrast = true;
		}
	}



	public boolean isSelected(int i) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setBounds(int x, int y) {
		// TODO Auto-generated method stub
		start = x; end = y;
		sc.setBounds(x,y);
	}

}
