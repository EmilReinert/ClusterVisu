package pixelvisu.application;

import java.io.IOException;

public class Data {
	SingleData data_main;
	SingleData data_compare; // secondary data to be compared structurally
	int group_count = 44;
	String section = "similarity";
	boolean contrast;
	
	public Data() throws IOException {
		data_main = new SingleData("Data/memory.json", group_count);
		data_compare = new SingleData("Data/cpu.json",data_main.c);
	}
	
	public void update() {
		data_main.section(group_count,section);
		try {data_compare = new SingleData(data_compare,data_main.c);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public void order(String mode) {
		update();
			data_main.order(mode);
			data_compare.order(mode);
		
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
}
