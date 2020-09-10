package pixelvisu.data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;

public class Scale  {
	// Horizontal Scale for data visu
	Vec2 mouse = new Vec2(0,0);
	String hover_data = " ";
	
	int width, height;
	float start_idx, end_idx;
	float max_len;
	int cuts=200; //seconds // vertical time cut intervals after specific instances
	
	public Scale(int with, int heit) {
		width = with; height = heit;
		start_idx=0;
		end_idx = 12000;
	}
	public void setMax(float max) {
		max_len=end_idx=max;
	}
	
	public void dataClick( Vec2 mouse, String data) {
		this.mouse = mouse;
		hover_data = data;
	}
	
	public void zoom(double d, Vec2 mouse) {
		float scale = end_idx-start_idx;
//		System.out.println(scale);
		double zoom_strength=0;
		if(scale>2500)zoom_strength = d*600;
		else zoom_strength = d*60;
		if(scale<300)zoom_strength = d*60;
		if(scale<100)zoom_strength = d*10;
		
		float end_hold = end_idx;
		float start_hold = start_idx;
		
		double relativeX = mouse.y/width;
		end_hold += (1 - relativeX) * zoom_strength;
        start_hold -= relativeX * zoom_strength;
        
        
        if(end_hold<start_hold)return;
        
        if(end_hold<max_len) end_idx=end_hold;
        else end_idx=max_len;
        
        if(start_hold>0) start_idx=start_hold;
        else start_idx=0;
	}
	
	public int getScaleIdx(int idx) {
		// returns data at idx for scaled data
		
		float scale = end_idx-start_idx;
		float pos = ((float)idx)/(width);
//		System.out.println(pos);		System.out.println(idx+" to "+(int)(start_idx+pos*width));
		return (int)(start_idx+pos*scale);
	}
	
	public int getUnscaledIdx(int idx) {

		float scale = end_idx-start_idx;
		return (int)(((idx-start_idx)/scale)*width);
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		// paints scale over data
//		g.drawLine(0, 0, width, height);
		g2d.setColor(new Color(1, 0, 0, 0.4f));
		// drawing cuts
		for(int i=(int) start_idx;i<end_idx;i++)
			if(i%cuts==0) {
				g2d.drawLine(getUnscaledIdx(i), 0, getUnscaledIdx(i), height);//System.out.println(i);
				g2d.drawString(i+" ", getUnscaledIdx(i)+2, 40);
				}

		g2d.setColor(new Color(1, 0, 0, 1.0f));
		g2d.drawRect((int)mouse.y-1, (int)mouse.x-1,1,1);
		g2d.drawString(hover_data, (int)mouse.y, (int)mouse.x-10);
		
		
	}
}
