package pixelvisu.data;

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
	
	public Data(int width, int height, Scale s)  {
		circ = new Circuit();
		
		try {
			data_main = new SingleData(maindata_path, circ.getCircuit(), group_count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data_compare = data_main;
//		data_compare = new SingleData("Data/cpu.json",data_main.c);
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
		try {data_compare = new SingleData(data_compare,data_main.c);
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
	
	public double getValue(int row, int idx) {
		//for string
		return data_main.getValue(row, idx);
	}
	public double getOrValue(int sec_idx, int row, int idx) {
		return data_main.getOrValue(sec_idx, row, idx);
	}
	
	public int getColor(int dataRowIdx, int pos) {
		if(data_main==null)return -1;
		if(data_main.c.flat_c==null)return -1;
		if(!contrast)return data_main.getColor(data_main.c.flat_c.get(dataRowIdx,sc.getScaleIdx(pos) ));
		else return data_main.getColor(data_main.c.flat_c.getContrast(dataRowIdx,sc.getScaleIdx(pos) ));
	}

	public int getOrColor( int sec_idx, int row, int idx) {
		return data_main.getOrColor(sec_idx, row, sc.getScaleIdx(idx));
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

}
