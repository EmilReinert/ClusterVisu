package pixelvisu.visu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Scale  {
	// Horizontal Scale for data visu
	Vec2 mouse = new Vec2(0,0);
	String hover_data = "";
	String name = ""; // name of hover node
	Data data;
	int start=0, end=0; // Start and end on pixels
	int hover = 0;
	public boolean paintit = true;
	ArrayList<String> b = new  ArrayList<String>();// contains bundle of selected nodes
	
	Color bg = Color.white;
	int width, height,off;
	float s, e; // origninal start and end
	float start_idx, end_idx; // start and end on data length
	int cuts=24; //seconds // vertical time cut intervals after specific instances
	
	public Scale(int with, int heit, Color bg) {
		resize(with, heit);
		start_idx=s=0;
		end_idx = e=12000;
		this.bg = bg;
//		zoom(1,new Vec2(0,0));
	}
	
	public void resize(int with, int heit) {
		this.off = 30;width = with; height = heit-off;
	}
	
	public void setMax(float max) {
		e=end_idx=max;
	}
	
	public void dataHover( Vec2 mouse, double data, String node) {
		// called upon click on data bar,,
		// updates click position and local data
		this.mouse = mouse;
		hover_data = ((float)Math.round(data*100))/100+" ";
		hover_data += node;
		name = node;
		paintit=true;
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
        
//        System.out.println("zooming"+ end_hold+" " +start_hold);
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
		 scale =(((idx-start_idx)/scale)*width);
		 
		 if(scale<0)return -1;
		 else return (int) scale;
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		// paints scale over data
//		g.drawLine(0, 0, width, height);
		
		
		// top selection ( rect and line)
		g2d.setColor(new Color(0,0,0,0.2f));
		g2d.fillRect(getUnscaledIdx(start), off-10,getUnscaledIdx(end)-getUnscaledIdx(start), 20);
				
		g2d.drawLine(hover, off, hover, height*2);
				

		// drawing cuts
		if(bg==Color.black)g2d.setColor(new Color(1,1,1, 0.4f));
		else g2d.setColor(new Color(0,0,0, 0.4f));
		
		for(int i=(int) start_idx;i<end_idx;i++)
			if(i%cuts==0) {
               if(i%24==0) g2d.setStroke(new BasicStroke(2));
               else g2d.setStroke(new BasicStroke(1));
				g2d.drawLine(getUnscaledIdx(i), off-10, getUnscaledIdx(i), off+20-10);//System.out.println(i);
				if(i%24==0||end_idx-start_idx<15)
					if(cuts==24)
						g2d.drawString(i/24+"d", getUnscaledIdx(i)+3, 12+40-10);
					else
						g2d.drawString(i+"h", getUnscaledIdx(i)+3, 12+40-10);
				}

            else g2d.setStroke(new BasicStroke(3));
		
		// data names
//		int count= 0;
//		for(SingleData d: data.data) {
//			g2d.setColor(new Color(0,0,0,0.2f));
//			g2d.fillRect((int)10-2, (int)(off+20+count*height/3)-22, 200, 16);
//			g2d.setColor(new Color(0, 0, 0, 1f));
//			g2d.drawString(d.dataname, 10,(int)(off+20+count*height/3)-10);
//			count++;
//		}
//		
		// hover data
		g2d.setColor(new Color(1, 1, 1,0.5f));
		g2d.fillRect((int)mouse.y-2, (int)mouse.x-22, hover_data.length()*7, 16);
		g2d.setColor(new Color(0, 0, 0, 1.0f));
		g2d.drawString(hover_data, (int)mouse.y, (int)mouse.x-10);
		
		
		if(paintit) {
//			g2d.setColor(new Color(1, 1, 1,1f));
//			g2d.fillRect(0, height/2, width, height);
			
			//relative steps
			double stepY = (double)height/ (2*data.getMain().max);
			
			//step amount for index limit
			double stepIDX = 10*(end_idx-start_idx)/data.getLength();
			
			if(stepIDX<1)stepIDX=1;
			// graphs
			g2d.setColor(new Color(0, 0, 0,0.2f));
			g2d.setStroke(new BasicStroke(1));

			double i = 0;
			for(Sequence s:data.getMain().sequences.sequences) {
				if(!b.contains(s.name))
					g2d.setColor(new Color(0, 0, 0,0.2f));
				else g2d.setColor(new Color(0, 0.2f, 1,0.2f));
				
				Vec2 prev = new Vec2(0,height);Vec2 curr = new Vec2(0,height);
				for(int j = 0; j<width;j+=5) {
					i = getScaleIdx((int) j);
					if (i>= s.getLength()||i<0) continue;
//						System.out.println(j+" Start end:"+start_idx+" "+end_idx+" results in "+i);
					curr.x = j; curr.y= (s.get( (int) i)*stepY+(float)height/2+off/2);
					g2d.drawLine((int)curr.x, (int) curr.y,(int) (prev.x), (int)(prev.y));
					prev.x=curr.x+1;
					prev.y =curr.y;
					}
			}

//			
//			g2d.setColor(new Color(1, 0, 0,1.0f));
//			for(Sequence s:data.getMain().sequences.sequences) {
//				if(name!=s.name) continue;
//				Vec2 prev = new Vec2(0,height);Vec2 curr = new Vec2(0,height);
//				for(int j = -1; j<width;j+=stepIDX) {
//					i = getUnscaledIdx((int) j);
//					if (i>= s.data.size()||i<0) continue;
//					curr.x = i*stepX; curr.y= (s.get( (int) i)*stepY+(float)height/2+off/2);
//					g2d.drawLine((int)curr.x, (int) curr.y,(int) (prev.x), (int)(prev.y));
//					prev.x=curr.x+1;
//					prev.y =curr.y;
//				}
//			}
		
		}
	}

	public void setBounds(int x, int y) {
		// TODO Auto-generated method stub
		start = getScaleIdx(x); end = getScaleIdx(y);
	}

	public void setSelect(int y) {
		// TODO Auto-generated method stub
		hover = y;
	}

	public void setBundle(ArrayList<String> bundle) {
		// TODO Auto-generated method stub
		b = bundle;
	}
}
