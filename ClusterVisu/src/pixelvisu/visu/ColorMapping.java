package pixelvisu.visu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ColorMapping extends JPanel implements MouseListener, MouseMotionListener{
// Mapping of 255 Data Values onto new 255 long color values defined as linear function
	int w, h;
	Data data;
	JFrame f;
	int size = 255;
	
	ArrayList<Vec2> points; // Break points for color function
	double[] map;// Finalized Mapping through full color length
	
	public ColorMapping () {
		w =h = size;
		


		f = new JFrame("Color");
		f.setSize(200, 290);
		f.add(this);
		f.setVisible(true);
		f.setResizable(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addMouseListener(this);
		addMouseMotionListener(this);
		points = new ArrayList<Vec2>();
		points.add(new Vec2(0,0));
//		points.add(new Vec2( 61.0 , 12.0 ));
//		points.add(new Vec2 ( 119.0 , 35.0 ) );
		points.add(new Vec2(size, size));
		makeMap();
	}

	public void paint(Graphics g_) {
		Graphics2D g = (Graphics2D) g_;

		g.setColor(Color.white);
		g.fillRect(0, 0, w,h);
		

		makeMap();
		
		// draws background color scale
		g.setStroke(new BasicStroke(2));
		for(int i=0;i<size;i++) {
			try {
				g.setColor(data.colorScale(i));
			}
			catch (Exception e) {
//				System.out.println("cant get color at: "+i);
				continue;
			}
			g.drawLine(i, 0, i, size);
		}

		// draws value distribution
		g.setStroke(new BasicStroke(1));
		g.setColor(new Color(0,0,0, 0.4f));
		int value_cnt;
		try{
			value_cnt = data.getLength()*data.getMain().sequences.getDepth();

			for(int i=0;i<size;i++) {
				
				g.drawLine(i, 0, i, data.getMain().values[i]/30);
			}
			
		
		}catch(Exception e) {
			value_cnt = 1;
		}
		// draws transfer function

		g.setColor(Color.red);
		double count =0;Vec2 prev = new Vec2(0,0);
		for(double val: map) {
			Vec2 iside =null;
			for(Vec2 p:points)
				if(p.x ==count)
					iside=p;
			if(iside!=null) {
				g.fillRect((int)iside.x-1,(int)iside.y-1, 3,3);
				
			}else {
			g.drawLine((int)prev.x, (int)prev.y, (int)count, (int)val);
			prev = new Vec2(count,val);}
			count++;
		}
//		Vec2 prev = new Vec2(0,0);
//		for(Vec2 p : points) {
//			g.drawLine((int)prev.x,(int) prev.y, (int)p.x,(int) p.y);
//			prev = p;
//		}
//		g.drawLine((int)prev.x,(int) prev.y, size,size);
			
	}
	
	private void makeMap() {
		map = new double[255];
		for (int i = 0; i<size;i++) 
			map[i] = i;
		if(points.size()<1) { return;}

		Vec2 prev = new Vec2(0,0);
		Vec2 current = new Vec2(0,0);
		int pointer =0;
		for(int i =0; i<size; i++) {
//			if(pointer>=points.size()) {
//				map[ i] = getColor(current, new Vec2(size,size),i);
//
//			}
//			else {
//				current = points.get(pointer);
//				
//				if((int)current.x ==i) {
//					prev = new Vec2(current);
//					pointer++;
//					i--;
//				}
//				else {
//					map[i] = getColor(prev,current,i);
//				}
//
//			}
			map[i] = getLagrange(i);
		}
	}
	
	public int color(int idx) {
		if (idx<0)return 0;
		if(idx>=255)return 255;
		return (int) map[idx];
	}
	
	private double getColor(Vec2 a, Vec2 b, double i) {
//		if(i<a.x||i>b.x) {System.err.println("something went wrong in color mapping");return 0;}
		
		// linear fuction
		Vec2 f= getLinFunction(a, b);
		return i*f.x+f.y;
		
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
	
	public double getLagrange(int xPoint) {
	    double sum = 0;
	    // Peforming Arithmatic Operation
	    for (int i = 0; i < points.size(); i++) {
		    double productU = 1;
		    double productL = 1;
	        for (int j = 0; j < points.size(); j++) {
	            if (j != i) {
	                productU *= (xPoint - points.get(j).x);
	                productL *= (points.get(i).x- points.get(j).x);
	            }
	        }
	        sum +=productU/productL * points.get(i).y;
	    }

	    return sum;

	    // End of the Program
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getModifiers() == MouseEvent.BUTTON3_MASK && e.getClickCount() == 1)
			{points = new ArrayList<Vec2>();

			points.add(new Vec2(0,0));
			points.add(new Vec2(size, size));
			}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

		points.add(new Vec2(e.getX(),e.getY()));
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

		System.out.println(points.get(points.size()-1).toString());
		points.sort(new Comparator<Vec2>() {
			
			@Override
			public int compare(Vec2 o1, Vec2 o2) {
				// TODO Auto-generated method stub
				return (int) (o1.x-o2.x);
			}
		});
		repaint();
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
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

		points.set(points.size()-1,new Vec2(e.getX(),e.getY()));

		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
