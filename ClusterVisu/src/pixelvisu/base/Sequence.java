package pixelvisu.base;

import java.awt.Color;
import java.util.ArrayList;

public class Sequence {
	float start;
	float end;
	float step;
	ArrayList<Integer> data;

	public Sequence(){
		data = new ArrayList<>();
	}
	
	public Sequence(Sequence s){
		this.start = s.start;
		this.end = s.end;
		this.step = s.step;
		data = new ArrayList<>();
		for (int i = 0; i< s.getLength();i++) {
			add(s.get(i));
		}
	}
	
	public void add(int d) {
		data.add(d);
	}
	
	public int get(int i) {
		return data.get(i);
	}
	
	public String toString() {
		return data.toString();
	}
	public int getLength() {
		return data.size();
	}
	//Math
	public int getWeight() {
		double weight = 0;
		for(int i = 0; i<getLength();i++) {
			weight+= get(i)/1000000;
		}
		return (int) Math.abs(weight);
	}
	
	
}
