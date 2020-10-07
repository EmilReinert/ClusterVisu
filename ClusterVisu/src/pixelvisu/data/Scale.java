package pixelvisu.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;

public class Scale  {
	// Horizontal Scale for data visu
	Vec2 mouse = new Vec2(0,0);
	String hover_data = " ";
	Data data;
	int start=0, end=0;
	int hover = 0;
	
	Color bg = Color.white;
	int width, height;
	float s, e; // origninal start and end
	float start_idx, end_idx;
	int cuts=24; //seconds // vertical time cut intervals after specific instances
	
	public Scale(int with, int heit, Color bg) {
		resize(with, heit);
		start_idx=s=0;
		end_idx = e=12000;
		this.bg = bg;
//		zoom(1,new Vec2(0,0));
	}
	
	public void resize(int with, int heit) {
		width = with; height = heit-80;
	}
	
	public void setMax(float max) {
		e=end_idx=max;
	}
	
	public void dataClick( Vec2 mouse, double data) {
		this.mouse = mouse;
		hover_data = ((float)Math.round(data*100))/100+"";
	}
	
	public void zoom(double d, Vec2 mouse) {
		float scale = end_idx-start_idx;
//		System.out.println(scale);
		double zoom_strength=0;
		if(scale>=2500) {zoom_strength = d*80;}
		if(scale<2500) {zoom_strength = d*40;}
		if(scale<300) {zoom_strength = d*10;}
		if(scale<100) {zoom_strength = d*5;}

		
		float end_hold = end_idx;
		float start_hold = start_idx;
		
		double relativeX = mouse.y/width;
		end_hold += (1 - relativeX) * zoom_strength;
        start_hold -= relativeX * zoom_strength;
        
        if(end_hold-start_hold<10)return;
        
        if(end_hold<e) end_idx=end_hold;
        else end_idx=e;
        
        if(start_hold>0) start_idx=start_hold;
        else start_idx=0;
        
        
        scale = end_idx-start_idx;
		cuts =24;
		if(scale<100) {cuts =1;}
//		if(scale<50) {cuts =1;}
	}
	
	public void drag(Vec2 start,Vec2 end) {
		//drag left, right

		float span = end_idx-start_idx;
	        var relativeXA =start.y/width;// (a.x - offset) / (w - 2 * offset); //relative mouse value on timeline
	        if (relativeXA < 0) relativeXA = 0;
	        if (relativeXA > 1) relativeXA = 1;

	        var relativeXB = end.y/width;//(b.x - offset) / (w - 2 * offset); //relative mouse value on timeline
	        if (relativeXB < 0) relativeXB = 0;
	        if (relativeXB > 1) relativeXB = 1;

	        double deltaRel = (relativeXB - relativeXA) * span;


	        if (start_idx - deltaRel <= s|| end_idx - deltaRel >= e) {
	            return;
	        }
	        start_idx -= deltaRel;
	        end_idx -= deltaRel;
	    
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
		
		
		// top selection ( rect and line)
		g2d.setColor(new Color(0,0,0,0.2f));
		g2d.fillRect(getUnscaledIdx(start), 0,getUnscaledIdx(end)-getUnscaledIdx(start), 50);
				
		g2d.drawLine(hover, 0, hover, height);
				

		// drawing cuts
		if(bg==Color.black)g2d.setColor(new Color(1,1,1, 0.4f));
		else g2d.setColor(new Color(0,0,0, 0.4f));
		
		for(int i=(int) start_idx;i<end_idx;i++)
			if(i%cuts==0) {
               if(i%24==0) g2d.setStroke(new BasicStroke(2));
               else g2d.setStroke(new BasicStroke(1));
				g2d.drawLine(getUnscaledIdx(i), 0, getUnscaledIdx(i), 50);//System.out.println(i);
				if(i%24==0||end_idx-start_idx<15)
					if(cuts==24)
						g2d.drawString(i/24+"t", getUnscaledIdx(i)+3, 42);
					else
						g2d.drawString(i+"h", getUnscaledIdx(i)+3, 42);
				}

            else g2d.setStroke(new BasicStroke(3));
		
		// data names
		g2d.setColor(new Color(0,0,0,0.2f));
		g2d.fillRect((int)10-2, 80-22, 200, 16);
		g2d.setColor(new Color(0, 0, 0, 1f));
		g2d.drawString(data.data_main.dataname, 10,80-10);

		g2d.setColor(new Color(0,0,0,0.2f));
		g2d.fillRect((int)10-2, (int)(80+height/3)-22, 200, 16);
		g2d.setColor(new Color(0, 0, 0, 1f));
		g2d.drawString(data.data_compare.dataname, 10, (int)(80+height/3)-10);

		g2d.setColor(new Color(0,0,0,0.2f));
		g2d.fillRect((int)10-2, (int)(80+2*height/3)-22, 200, 16);
		g2d.setColor(new Color(0, 0, 0, 1f));
		g2d.drawString(data.data_compare_two.dataname, 10,(int)(80+2*height/3)-10);
		
		
		
		// hover data
		g2d.setColor(new Color(1, 1, 1,0.5f));
		g2d.fillRect((int)mouse.y-2, (int)mouse.x-22, hover_data.length()*7, 16);
		g2d.setColor(new Color(0, 0, 0, 1.0f));
		g2d.drawString(hover_data, (int)mouse.y, (int)mouse.x-10);
		
		
	}

	public void setBounds(int x, int y) {
		// TODO Auto-generated method stub
		start = getScaleIdx(x); end = getScaleIdx(y);
	}

	public void setSelect(int y) {
		// TODO Auto-generated method stub
		hover = y;
	}
}
