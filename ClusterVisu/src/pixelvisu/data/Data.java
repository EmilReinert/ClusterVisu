package pixelvisu.data;

import java.awt.Color;
import java.io.IOException;

public class Data {
	SingleData data_main;
	SingleData data_compare; // secondary data to be compared structurally
	int group_count = 44;
	String section = "similarity";
	boolean contrast;
	Scale sc;
	Circuit circ;
	

	String maindata_path = "Data/memory_2.txt";
	String comparedata_path = "Data/memory_prom.txt";
	
	public Data(int width, int height, Scale s)  {
		circ = new Circuit();
		
		try {
			data_main = new SingleData(maindata_path, circ.getCircuit(), group_count);
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
			data_compare = new SingleData(comparedata_path,data_main.c, Color.orange);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	public void up() {
		circ.up();
	}
	public void down() {
		circ.down();
	}
	
	public double getValue(SingleData d, int row, int idx) {
		//for string
		return data_main.getValue(row, idx);
	}
	public double getOrValue(SingleData d,int sec_idx, int row, int idx) {
		return data_main.getOrValue(sec_idx, row, idx);
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
	
	
	
	
	
	
	
	public void order(String mode) {
		updateSection();
		data_main.order(mode);
		data_compare.order(mode);
		
	}
	
	public int getLength() {
		return data_main.getLength();
	}
	
	public void contrast() {
		if (contrast) {
			contrast = false;
			data_main.contrast = false;
			data_compare.contrast = false;
		}
		else {
			contrast = true;
			data_main.contrast = true;
			data_compare.contrast = true;
		}
	}


	public int getDiff(int dataRowIdx, int pos) {
		return data_main.c.flat_c.getDiff(dataRowIdx,sc.getScaleIdx(pos) );
	}

	public boolean isSelected(int i) {
		// TODO Auto-generated method stub
		return false;
	}

}
