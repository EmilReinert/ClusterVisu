package pixelvisu.visu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.JFrame;







public class VisuData implements MouseListener,MouseMotionListener,MouseWheelListener,KeyListener{

	private int width, height, off; // width, height for diagram and global off
	public int localOffCount; // calculates position of visu
	private int localOff; // resulting pixel position
	public int visuNum; // amount of visus on display
	private int time =0;
	
	Data data;
	int datapointer = 0;
	Scale sc;
	
	Color bg_color;
	int length ;

	Vec2 mouse_click = new Vec2(0,0);
	Vec2 mouse_click_prev = new Vec2(0,0);
	Vec2 clicksquare = new Vec2(0,0);
	
	Vec2 mouse_hover = new Vec2(0,0);
	Vec2 mouse_pressed =new Vec2(0,0);
	Vec2 mouse_dragged =new Vec2(0,0);
	int click_cluster=0;
	boolean select = false;
	boolean dataswitch = true;
	boolean unpack_all =false;
	boolean denden =true;
	

	double hoverData = 0;
	String hoverNode = "";
	int posXY = 0;
	
	public VisuData(int w, int h,Data data, Color bg_c, Scale s,int pointer)  {
		datapointer = 0;
		width = w;
		height = h;
		bg_color= bg_c;

		
		this.data = data;
		
		length = data.getLength();
		sc =s;
		sc.setBounds(data.start, data.end);
		data.updateSection();
	}

	
	
	// UPDATE ----------------------------------------------------
	public void setSize(int w,int h,int off, int localoff,int sum) {
		// initial sizing
		this.localOffCount = localoff;
		upSize(w, h, localoff, sum);
	}
	public void upSize(int w,int h,int off, int sum) {
		// initial sizing
		this.localOff = 40+ localOffCount*(h-40-off)/sum;
		this.width=w; this.height = h-off;
		this.off = w*off;
	}
	public int getOff() {
		return localOff;
	}
	
	public void update(int[] pixels) {
		drawBackground(pixels);
		time++;
		hoverData =0; hoverNode = "";
		
		
		
		if(data.data.get(datapointer).c!=null&&data.data.get(datapointer).c.flat_c!=null) {
			drawBarsDen(pixels, data.data.get(datapointer), off, off+height/3*width);
		}
		
		
		return ;
	}	

	// ----------------------------------------------------
	

	
	public void drawSections(int[] pixels,Group seqs, int start, int end) {
		int top_off =start ;
		int graph_color = Color.red.getRGB();
		
		//drawing density distribution
				//AND
		//drawing sections
		ArrayList<Integer> sec = seqs.sections;
		float sec_idx=0;
		for(int i = 0; i<sec.size();i++) {
			sec_idx =sec.get(i);
			// drawing horizontal section
//			drawLine(pixels,  (int) (width*sec_idx+top_off),(int) (width*sec_idx+top_off)+getLength()-1, graph_color,0.8f);
//			drawPoint(pixels,  (int) (width*sec_idx+top_off)+getLength()-width, graph_color);
			drawLine(pixels,  (int) (width*sec_idx+top_off)+getLength(),(int) (width*sec_idx+top_off)+getLength()+10,graph_color,1.0f);
		}
		
	}

	public void drawBarsDen(int[] pixels,SingleData cl, int start, int end) {

		ArrayList<Integer> dens = cl.c.flat_c.densities;
		int jump =0;
		int jumping =0;
		int den = 0;
		boolean clicked = false;
		int pixelheight = 1; // height of a singular pixel

		sc.b = new ArrayList<String>();
		
		if(cl.c.flat_c==null)return;
		for(int i=0; i< cl.c.flat_c.getDepth();i++) {
			if(denden)den =  (dens.get(i)+30)/5;// 5; //for static sizes
			else den = 5;
			Vec2 square = new Vec2(start + (jump) * width, start + (jump + den) * width + 19*width/20);
			
			// HOVER BUNDLE
			if(isInsideSquare(mouse_hover, (int)square.x,(int)square.y)) {
				// hover over section and sends bundle to sc
				for (int n =0; n< dens.get(i);n++) {
						
							sc.b.add(data.getOrNode(cl, i,n));
						
					
				}
				
			}

			// OPEN BUNDLE
			if (clickSquare(mouse_click,square)){
				jumping = drawDataSec(pixels, cl, start + jump * width, i, pixelheight, dens.get(i));

				jumping += 2;
			}

			// REPRESENTATIVE AVERAGE
			else {
//					if(isInsideSquare(mouse_hover,start + (jump-1) * width, start + (jump + den+2) * width + getLength())) 	den*=3;
				drawDataBar(pixels,cl,  start + jump * width, i, den);
				jumping = den + 2;
			}
			// drawing density dots
//			drawDots2(pixels, start+jump*width+ getLength() , dens.get(i));
			jump += jumping;
		}

		
	}
	
	public void drawDataBar(int[] pixels, SingleData d, int startpos,int dataRowIdx, int lenght) {
		// draw a bar with SAME data
		Color col_hold;
		int diff_color = 0;
		int diff_fade =0;
		for (int n = startpos; n<startpos+lenght*width&&n<pixels.length;n++) {

			int pos = ((n%(width))-startpos%width);
			col_hold = data.getColor(d,dataRowIdx,pos);
			if(col_hold!=null) {
				diff_color =data.getDiff(d,dataRowIdx,pos);
				diff_fade =(int) Math.pow(diff_color*0.005,4);
				if(diff_color>255)diff_color =255;
				if(diff_fade>255)diff_fade =255;
				diff_color = new Color(diff_fade,0,0 ).getRGB();
				pixels[n] =mixColors(diff_color, col_hold.getRGB(), 0.1f);
				
				if(vec2Int(mouse_hover)==n)
					hoverData=data.getValue(d,dataRowIdx,pos);
				}
		}
	}
	
	public int drawDataSec(int[] pixels, SingleData d, int startpos,int sec_idx, int pixelheight, int length) {
		// draw an ORIGINAL section of data
		Color col_hold;
		// iterate over rows : start and end _idx
		// and then over individual data values

		sc.b = new ArrayList<String>();
		int step =0;
		for (int n =0; n<length;n++) {
			double pix= getFisheyeY(mouse_hover, int2Vec(startpos+(n+step)*width));
			for(int hei=0;hei<pix;hei++) {
				sc.b.add(data.getOrNode(d, sec_idx,n));
				
				
				for(int i =0;i<width;i++) {
					col_hold = data.getOrColor(d,sec_idx,n,i);
					
					//System.out.println(n+" "+i+" "+col_hold);
					if(col_hold!=null) {
						if(startpos+(n+step)*width+i<pixels.length)pixels[startpos+(n+step)*width+i] =col_hold.getRGB(); //System.out.println("hi");
	
						if(vec2Int(mouse_hover)==startpos+n*width+i) {
							hoverData=data.getOrValue(d,sec_idx,n,i);
							hoverNode = data.getOrNode(d,sec_idx,n);}
						}
				}
				step++;
			}
			step--;
		}
		return length+ step;
	}
	
	

	
	
	public void drawDots2(int[] pixels, int start, int den ) {
		int step = 3;
		int jump =0;
		int dotlength = 100;
		int pos = start+step/2;
		for (int i = 0; i<den; i++) {
			if((pos)%width>start%width+dotlength) {pos= start+step/2;jump++;}
			if( width*(jump*step)+pos< pixels.length-3*width)drawDot2(pixels, width*(jump*step)+pos, Color.gray.getRGB());
			pos += step;
		}
	}
	
	public void drawDots3(int[] pixels, int start, int den ) {
		int step = 4;
		int jump =0;
		int dotlength = 100;
		int pos = (1)*width+start+step/2;
		for (int i = 0; i<den; i++) {
			if((pos)%width>start%width+dotlength) {pos= (1)*width+start+step/2;jump++;}
			if( width*(jump*step)+pos< pixels.length-3*width)drawDot3(pixels, width*(jump*step)+pos, Color.gray.getRGB());
			pos += step;
		}
	}
	
	public double getFisheyeY(Vec2 a, Vec2 b) {
		float distance = getYDistancec(a, b);
		return 1+ 5*Math.pow(Math.E,-0.05*distance*distance);
	}
	
	public int getYDistancec(Vec2 a, Vec2 b) {
		return Math.abs((int)(a.x-b.x));
	}
	/// GET AND SETTER ///
	public int getHeight() {
		return height;
	}
	
	public int getLength() {
		return length;
	}
	/// GEOMETRIC FUNCTIONS ///

	public int vec2Int(Vec2 v) {
		return (int) ((v.x-1)*width+v.y);
	}
	
	public Vec2 int2Vec(int i) {
		// x is downwards 
		// y is sidewards
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

	public boolean isInsideSquare(Vec2 mouse_,int start, int end) {
		// returns whether the mouse is inside a certain square
		// note start and end are not an array but int pointers to panel
		return isInsideSquare(mouse_, int2Vec(start),int2Vec(end));
		
	}
	public boolean clickSquare(Vec2 mouse_, Vec2 square) {

		if(clicksquare.equals(square)&&!isInsideSquare(mouse_, (int)square.x,(int)square.y))
			return true;
		
		if(isInsideSquare(mouse_, (int)square.x,(int)square.y)) {
			if(!clicksquare.equals(square)) {
				clicksquare =(square);
				mouse_click = new Vec2(0,0);
				return true;
			}
		}
		return false;
		
	}
	public boolean isInsideSquare(Vec2 mouse_, Vec2 start, Vec2 end) {
		// returns whether the mouse is inside a certain square
		// note start and end are not an array but int pointers to panel
		if(mouse_.y>start.y)
			if(mouse_.y<end.y)
				if(mouse_.x>start.x)
					if(mouse_.x<end.x)
						{return true;}
		return false;
		
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
	public int mixColors(Color c0, int cb, float ratio) {
	    float iRatio = 1.0f - ratio;

	    double weight0 = ratio;
    	double weight1 = iRatio;

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

	public int deSaturate(int ca, int gray) {
		// gray from 0 to 255 determines how gray

	    Color c0 = new Color(ca);
	    
	    // the grayer the closer to this max value
	    double max=0;
	    if(c0.getRed() >max)max = c0.getRed() ;
	    if(c0.getBlue() >max)max = c0.getBlue() ;
	    if(c0.getGreen() >max)max = c0.getGreen() ;
	    max*=0.8;
	    double satu = (gray/255);
	    
	    double r = max*satu+ c0.getRed()*(1-satu) ;
	    double g = max*satu+ c0.getGreen()  * (1-satu);
	    double b = max*satu+ c0.getBlue() * (1-satu);

	    if(r>255) r=255;
	    if(g>255) g=255;
	    if(b>255) b=255;
	    
	    return new Color((int) r, (int) g, (int) b,1).getRGB();
    }
	
	public void drawPoint(int[] pixels,Vec2 a, int color) {
		drawPoint(pixels,vec2Int(a),color);
	}

	public void drawPoint(int[] pixels, int a, int color) {
		if(a<pixels.length)
			pixels[a]=color;
	}
	
	
	public void drawDot3(int[] pixels, int a, int color) {
		// draws 6*6 pixels point
		if(a>width+1)pixels[a-width-1]=pixels[a-width]=pixels[a-width+1]=color;
		if(a>1)pixels[a-1]=pixels[a]=pixels[a+1]=color;
		if(a+width+1<pixels.length&&a>width)pixels[a+width-1]=pixels[a+width]=pixels[a+width+1]=color;
	}
	
	public void drawDot2(int[] pixels, int a, int color) {
		// draws 6*6 pixels point
		if(a>1)pixels[a]=pixels[a+1]=color;
		if(a+width+1<pixels.length&&a>width)pixels[a+width]=pixels[a+width+1]=color;
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
	
	
	

//	public void drawTopLine(Graphics g2) {
//		g2.setColor(Color.green);
//	    g2.fillRect(0, 0,width, height);
//		
//		g2.setColor(Color.BLACK);
//	}
	
	

	//INTERACTIONS----------------------------------------------------------------------------
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		sc.zoom(e.getPreciseWheelRotation(),mouse_hover);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

//		System.out.println(sc.start+" "+sc.end);
		if(mouse_pressed.x+localOff<60)
			select = true;
	
		if(select) {
			sc.setBounds((int)mouse_pressed.y, e.getX());
			data.setBounds((int)mouse_pressed.y, e.getX());
		}
		else {
			sc.drag(mouse_dragged,new Vec2(e.getY()-localOff, e.getX()));
			mouse_dragged = new Vec2(e.getY()-localOff, e.getX());
			mouseMoved(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		mouse_hover = new Vec2(e.getY(), e.getX());
		sc.dataHover(mouse_hover, hoverData, hoverNode);
		mouse_hover = new Vec2(e.getY()-localOff, e.getX());
//		System.out.println(hoverData +hoverNode);
		if(mouse_hover.x+localOff<60)
			sc.setSelect((int)mouse_hover.y);
		else
			sc.setSelect(0);
			
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getModifiers() == MouseEvent.BUTTON3_MASK && e.getClickCount() == 1) {}
		else
			mouse_click = new Vec2(e.getY()-localOff, e.getX());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getModifiers() == MouseEvent.BUTTON3_MASK && e.getClickCount() == 1)clicksquare = new Vec2(0,0);
		else {
			mouse_pressed =  new Vec2(e.getY()-localOff, e.getX());
			mouse_dragged=  new Vec2(e.getY()-localOff, e.getX());
		
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		//p.update{(click_cluster);
		if(select) {
			data.updateClustering();
		
		}
		select = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void keyPressed(KeyEvent e) {
		
		if(e.getKeyCode()==85) {
			if(unpack_all)unpack_all=false;
			else unpack_all=true;		
		}
		
		if(e.getKeyCode()==73) {
			if(denden)denden=false;
			else denden=true;		
		}
		
		if(e.getKeyCode()==39) {data.up();System.out.println("up");}
		
		if(e.getKeyCode()==37) {data.down();System.out.println("down");}
		
		
	}



	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
