package pixelvisu.data;

import java.io.IOException;

public class Data {
	SingleData data_main;
	SingleData data_compare; // secondary data to be compared structurally
	int group_count = 44;
	String section = "similarity";
	boolean contrast;
	Scale sc;
	
	public Data(int width, int height, Scale s)  {
		try {
			data_main = new SingleData("Data/memory_2.txt", group_count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data_compare = data_main;
//		data_compare = new SingleData("Data/cpu.json",data_main.c);
		sc =s;
	}
	
	public void update() {
		data_main.section(group_count,section);
		try {data_compare = new SingleData(data_compare,data_main.c);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public double getValue(int row, int idx) {
		return data_main.getValue(row, idx);
	}
	public double getOrValue(int sec_idx, int row, int idx) {
		return data_main.getOrValue(sec_idx, row, idx);
	}
	
	public int getColor( int row, int idx) {
		return data_main.getColor(row, sc.getScaleIdx(idx));
	}

	public int getOrColor( int sec_idx, int row, int idx) {
		return data_main.getOrColor(sec_idx, row, sc.getScaleIdx(idx));
	}
	
	public void order(String mode) {
		update();
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

	public void updateClustering(String[] circuit) {
		data_main.updateClustering(circuit);
		update();
		
	}

	public int getDiff(int dataRowIdx, int pos) {
		return data_main.c.flat_c.getDiff(dataRowIdx,sc.getScaleIdx(pos) );
	}
}
