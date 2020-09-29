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

//	TreePanel p;

	String maindata_path = "Data/4w_14_9_1h/node_memory_Active_bytes.txt";
	String comparedata_path = "Data/4w_14_9_1h/node_memory_active_file_bytes.txt";
	String comparedata_two = "Data/4w_14_9_1h/node_memory_Cached_bytes.txt";
	
	public Data(int width, int height, Scale s, ColorMapping m)  {
		circ = new Circuit();
//		p = new TreePanel();
		cm = m;
		
		try {
			data_main = new SingleData(maindata_path, circ.getCircuit(), group_count,cm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateSection();
		sc =s;
	}

	public void updateClustering() {
		try {
			data_main.update(maindata_path, circ.getCircuit(), group_count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateSection();
		
	}
	
	public void updateSection() {
		data_main.section(group_count,section);
		try {
			data_compare = new SingleData(comparedata_path,data_main.c, Color.blue, cm);
			data_compare_two = new SingleData(comparedata_two,data_main.c, Color.orange, cm);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		p.update(data_main.c, group_count);
		
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
		if(!contrast)return d.getColor(d.c.flat_c.get(dataRowIdx,sc.getScaleIdx(pos) ));
		else return d.getColor(d.c.flat_c.getContrast(dataRowIdx,sc.getScaleIdx(pos) ));
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

}
