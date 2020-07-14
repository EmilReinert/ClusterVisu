package pixelvisu.base;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class VisuBase implements MouseListener,MouseMotionListener,MouseWheelListener{

	private int width, height; // width, height for diagram
	
	Data data;
	
	Color bg_color;
	int group_count =30; 
	int new_group_count=group_count;
	
	public VisuBase(int w, int h, Data d, Color bg_c) {
		width = w;
		height = h;
		data = d;
		bg_color= bg_c;
		
	}

	/// GET AND SETTER ///
	public int getHeight() {
		return height;
	}
	
	/// GEOMETRIC FUNCTIONS ///

	public int vec2Int(Vec2 v) {
		return (int) ((v.x-1)*width+v.y);
	}
	
	public Vec2 int2Vec(int i) {
		int x =(i-i%width)/width;
		int y= i%width;
		return new Vec2(x, y);
	}

	public Vec2 getLinFunction(Vec2 first , Vec2 second) {
		// return m and t for a linear function f = m*x+t with  function a-> b
		//Greradenfunktion
		double m; //STEIGUNG
		if(first.y>second.y)m=-1;
		else m = 1;
		
		if(second.x-first.x!=0)
			m*=Math.abs((second.y-first.y)/(second.x-first.x));
		double t= first.y-first.x*m; //x verschiebung		
		return new Vec2(m, t);
	}
	
	
	/// DRAW FUNCTIONS ///	
	public int mixColors(int ca, int cb, float ratio) {
		    float iRatio = 1.0f - ratio;

		    double weight0 = ratio;
	    	double weight1 = iRatio;

		    Color c0 = new Color(ca);
		    Color c1 = new Color(cb);

		    double r = weight0 * c0.getRed() + weight1 * c1.getRed();
		    double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
		    double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();

		    if(r>255) r=255;
		    if(g>255) g=255;
		    if(b>255) b=255;
		    
		    return new Color((int) r, (int) g, (int) b,1).getRGB();
	}
	public int addColors(int ca, int cb ) {
	    Color c0 = new Color(ca);
	    Color c1 = new Color(cb);

	    double r = c0.getRed() +c1.getRed();
	    double g =c0.getGreen() + c1.getGreen();
	    double b =  c0.getBlue() +  c1.getBlue();

	    if(r>255) r=255;
	    if(g>255) g=255;
	    if(b>255) b=255;
	    
	    return new Color((int)r, (int) g, (int) b,1).getRGB();
}
	
	public void drawPoint(int[] pixels,Vec2 a, int color) {
		drawPoint(pixels,vec2Int(a),color);
	}
	
	
	public void drawPoint(int[] pixels, int a, int color) {
		// draws 6*6 pixels point
		if(a>width+1)pixels[a-width-1]=pixels[a-width]=pixels[a-width+1]=color;
		if(a>1)pixels[a-1]=pixels[a]=pixels[a+1]=color;
		if(a+width+1<pixels.length&&a>width)pixels[a+width-1]=pixels[a+width]=pixels[a+width+1]=color;
	}
	
	public void drawDot(int[] pixels, int a, int color) {pixels[a]=pixels[a+1]=color;
		pixels[a]=color;
	}
	

	public void drawLine(int[] pixels,int a, int b, int color,float alpha ) {
		// draw line for int values
		Vec2 vec_a = int2Vec(a);
		Vec2 vec_b = int2Vec(b);
		drawLine(pixels,vec_a, vec_b, color,alpha);
	}
	
	public void drawLine(int[] pixels,Vec2 a, Vec2 b,int color, float alpha ) {
		//draws line on screen by manipulating pixel values
		Vec2 first,second;//System.out.println(a.x+" "+b.x);
		if (a.x<b.x) {first = a;second=b;}
		else {first=b;second=a;}
		
		//Greradenfunktion
		Vec2 mt = getLinFunction(first, second);
		double m = mt.x;
		double t = mt.y;
		float x,y,f_x;
		if(Math.abs(m)<1) {
			for(int n=0; n<pixels.length; n++) {
				x = (n-n%width)/width;
				y = n%width;
				f_x = (float) (m*x+t);
				//line starts
				if (x>=first.x&&x<=second.x) {
					if((int)y==(int)f_x) {pixels[n]=mixColors( color,pixels[n], alpha);}
				}
			}	
		}
		else {
			// inverse line drawing
			if (a.y<b.y) {first = a;second=b;}
			else {first=b;second=a;}
			//steigung
			if(first.x>second.x)m=-1;
			else m = 1;
			m*=Math.abs((second.x-first.x)/(second.y-first.y));
			t= first.x-first.y*m; //x verschiebung
			
			for(int n=0; n<pixels.length; n++) {
				x = (n-n%width)/width;
				y = n%width;
				f_x = (float) (m*y+t);
				if (y>=first.y&&y<=second.y) {
					if((int)x==(int)f_x) {pixels[n]=mixColors( color,pixels[n], alpha);}
				}
			}						 	
		}
	}
	
	public void drawBackground(int[] pixels) {
		//draw background
		for(int n=0; n<pixels.length; n++) {pixels[n] = bg_color.getRGB();}
	}
	
	
	// UPDATE ----------------------------------------------------
	
	public void update(int[] pixels, int w,int h,int off) {
		width=w; height = h-off;
		group_count=new_group_count;
		off = w*off;
		data.compress(group_count);
		
		drawBackground(pixels);
		
		drawCat(pixels,off,2*width*height/3+off);
		//System.out.println("cat drawn");
		//drawLine(pixels, new Vec2(0,0), new Vec2(2000,2000), Color.red.getRGB());
		drawCatDiagrams(pixels,off+width/4,width*height/3+off+width/4);
		//System.out.println("lines drawn");
		drawCompressed(pixels,off+width/2,2*width*height/3+off );
		return ;
	}
	
	public void drawCat(int[] pixels, int start, int end){
		//drawing both cat images
		int col_hold=0;
		for(int n=start; n<end; n++) {
			col_hold = data.getColor((int)int2Vec(n-start).x,(int)int2Vec(n-start).y);
			if(col_hold>=0)
				pixels[n] =col_hold;
		}
	}


	public void drawCatDiagrams(int[] pixels, int start, int end) {
		float span = (float) Math.abs(data.getColorBounds().y);
		int top_off =start ;
		int depth =data.getDepth();
		int graph_color =new Color(0, 165, 171).getRGB();
		
		
		
		float prev = (depth*(data.getWeight(0)/span)); 
		float hold=0;
		float prev_o = (depth*(data.getWeightO(0)/span)); 
		float hold_o;
		
		

		//drawing weight distributions
		/*
		for(int i=0; i<depth; i++) {
			// line from previous to weight data matching the frame space(height)
			hold =  (depth*(data.getWeight(i)/span));
			hold_o = (depth*(data.getWeightO(i)/span));
			//drawLine(pixels, width*(i-1)+(int)prev+1,width*i+(int)hold, Color.RED.getRGB(),0.3f);
			//drawLine(pixels, depth+ width*(i-1)+(int)prev_o+1,depth+width*i+(int)hold_o, Color.RED.getRGB(),0.3f);
			drawLine(pixels, width*i+(int)hold-1+top_off,width*i+top_off, graph_color,0.5f);
			drawLine(pixels, depth+ width*i+(int)hold_o-1+top_off,depth+width*i+top_off, graph_color,0.5f);
			prev = hold;
			prev_o=hold_o;
		}
		*/
		// filled distributions
		for(int n=top_off; n<end; n++) {
			int i =(int)((n-top_off)/width);
			//if(i>255)break;
			//System.out.println(i);
			hold =  (depth*(data.getWeight(i)/span));
			hold_o =  (depth*(data.getWeightO(i)/span));
			//System.out.println(hold);
			if(((n-top_off)%width)<=(int)hold-1) {
				pixels[n] = mixColors(graph_color,bg_color.getRGB(),0.5f);
			}
			if(((n-top_off)%width)<=(int)hold_o-1){
				pixels[n+width*height/3] = mixColors(graph_color,bg_color.getRGB(),0.5f);
			}
		}
		
		//drawing density distribution
				//AND
		//drawing sections
		ArrayList<Integer> sec = data.sequences.getSections(group_count);
		ArrayList<Float> den = data.compressed.densities;
		float sec_idx=0;
		float prev_idx=0;
		float density = top_off+1000;float density_hold =density;
		for(int i = 0; i<sec.size();i++) {
			sec_idx =sec.get(i);
			// drawing horizontal section
			drawLine(pixels,  (int) (width*sec_idx+(depth*(data.getWeight((int)sec_idx)/span))-1+top_off),
					(int) (width*sec_idx+top_off), graph_color,0.5f);

			//drawing density-graph line
			density =  width*(int)(prev_idx+(sec_idx-prev_idx)/2)+top_off+den.get((int)i)*width/6;//+height/3;//y value
			if(i>0)
			drawLine(pixels,(int)density+2,(int)density_hold+2,Color.red.getRGB(),1);
			prev_idx = sec_idx;
			density_hold = density;
		}
		drawLine(pixels,top_off+width*((height/3)-1)+2,(int)density_hold+2,Color.red.getRGB(),0.5f);
	
		ArrayList<Integer> sec_o = data.sequences_o.getSections(group_count);
		ArrayList<Float> den_o = data.compressed_o.densities;
 		prev_idx=0;
		density = top_off-width*30; density_hold =density;
		int seco_idx=0;
		for(int i = 0; i<sec_o.size();i++) {
			seco_idx =sec_o.get(i);
			// drawing horizontal section
			drawLine(pixels,  (int) (width*seco_idx+(depth*(data.getWeightO(seco_idx)/span))-1+top_off)+width*height/3,
					width*seco_idx+top_off+width*height/3, graph_color,0.5f);

			//drawing density-graph line
			density =width*height/3+ width*(int)(prev_idx+(seco_idx-prev_idx)/2)+top_off+den_o.get((int)i)*width/6;//+height/3;//y value
			if(i>0)
			drawLine(pixels,(int) density+2,(int)density_hold+2, Color.red.getRGB(),1);
			prev_idx = seco_idx;
			density_hold = density;
		}
		drawLine(pixels,top_off+2*width*((height/3)-1)+2,(int)density_hold+2, Color.red.getRGB(),0.5f);
	}
	
	public void drawSequences(int[] pixels, Sequences seqs, int start, int end) {
		double top_off = start;
		int col_hold =0;
		int density_color =0;
		int value=0;
		for(int n=(int) top_off; n<end; n++) {
			if(n%width==0)n+=width/2; // jumping over couple of nodes		
			int pos = (int) (n-top_off);
			col_hold = data.getData(seqs,(int)int2Vec(pos).x,(int)int2Vec(pos).y);
			
			if(col_hold>=0) {
				pixels[n] =col_hold;
				}
		}
		top_off=width/4+start;//+width*(((end-start)/2)/width);
		//drawPoint(pixels, (int)top_off, 0);
		for(int n=(int)top_off; n<end; n++) {
			
			if(n%width==0)n+=3*width/4; // jumping over couple of nodes		
			int pos = (int) (n-top_off);
			col_hold = data.getData(seqs,(int)int2Vec(pos).x,(int)int2Vec(pos).y);
			
			if(col_hold>=0) {
				value = seqs.getSeqValue((int)((pos)/width))*group_count/3;
				if((n%width)-4<3*width/4) {col_hold=0;value =value*2+10;} // for small reference line
				if(value>255)value = 255; //max color threshold
				density_color = new Color(100,value,0).getRGB();
				col_hold=addColors(density_color,col_hold);
				pixels[n] =col_hold;
				}
			
		}
	}
	
	public void drawCompressed(int[] pixels, int start, int end) {
		int step = (end-start)/2+width/4;

		drawSequences(pixels, data.compressed, start, start+step);
		drawSequences(pixels, data.d_compressed, start+step/2,start+step);

		drawSequences(pixels, data.compressed_o, start+step, start+step/2+step);
		drawSequences(pixels, data.d_compressed_o, start+step/2+step, start+2*step);
	/*	
		top_off+=width*height/6;
		for(int n=top_off; n<end/4 +top_off; n++) {
			if(n%width>=3*width/4)n+=3*width/4; // jumping over couple of nodes		
			int pos = n-top_off;
			col_hold = data.getCompO((int)int2Vec(pos).x,(int)int2Vec(pos).y);
			
			if(col_hold>=0) {
				density_color = new Color(data.compressed_o.getSeqValue((int)((pos)/width))*2,0,0).getRGB();
				col_hold=addColors(density_color,col_hold);
				pixels[n] =col_hold;
				}
			else pixels[n] = bg_color.getRGB();
		}
		top_off+=width*height/6;
		for(int n=top_off; n<end; n++) {
			if(n%width>=3*width/4)n+=3*width/4; // jumping over couple of nodes		
			int pos = n-top_off;
			col_hold = data.getCompO((int)int2Vec(pos).x,(int)int2Vec(pos).y);
			
			if(col_hold>=0) {
				pixels[n] =col_hold;
				}
			else pixels[n] = bg_color.getRGB();
		}
	
			
	*/
		
		
	}
	
	public void drawTopLine(Graphics g2) {
		g2.setColor(Color.green);
	    g2.fillRect(0, 0,width, height);
		
		g2.setColor(Color.BLACK);
	}
	
	
	
	//INTERACTIONS----------------------------------------------------------------------------
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		if((new_group_count-e.getPreciseWheelRotation()>0)&&(new_group_count-e.getPreciseWheelRotation()<255))
			new_group_count -=e.getPreciseWheelRotation();
		System.out.println(new_group_count);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
