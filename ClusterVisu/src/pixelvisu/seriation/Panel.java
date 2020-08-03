package pixelvisu.seriation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Panel extends JPanel{
	boolean running;
	Cluster c;
	JFrame f;
	int w, h;
	
	public Panel() {
		w =1200; h = 600;
		f = new JFrame("Tree");
		f.setSize(w	,h);
		f.setResizable(true);
		f.add(this);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	


	public void update(Cluster c) {
		this.c = c;
		repaint();
        try
        {
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            f.paint(graphics2D);
            ImageIO.write(image,"png", new File("img/"+c.name+".png"));
        }
        catch(Exception exception)
        {
            //code
        }
	}

	@Override
	public void paint(Graphics g_) {
		Graphics2D g = (Graphics2D) g_;

		g.setColor(Color.white);
		g.fillRect(0, 0, (int) (w*2), h*2);
		if (c != null) {
			paintCluster(g);
}
	}

	
	public void paintCluster( Graphics2D g) {
		// will only be called if cluser is defined
		int depth = c.treeorder.getDepth();
		System.out.println("Painting Cluster "+c.name);
		double height_ratio = (0.6*h)/c.treeorder.branches.get(0).similarity;
		double width_ratio = (0.6*w)/c.treeorder.length;
		ArrayList<Node> plane = c.treeorder.branches;
		for(int i = 0; i<100000;i++) {
			ArrayList<Node> hold = new ArrayList<Node>();
			boolean lastleaf =true;
			for(Node cc: plane) {for(Node c:cc.branches) {
				if(!c.isLeaf)lastleaf=false;
			}}
			
			for(Node cc: plane) {for(Node c:cc.branches) {
				int top_off = 0;
				int left_off=15;
				if(!c.isLeaf) hold.add(c);
				g.setColor(Color.black);
//				// DRAW BRANCHES Lines
//				g.drawLine((int)(cc.x_pos*width_ratio),
//						(int) (cc.similarity*height_ratio),
//						(int) (c.x_pos*width_ratio),
//						(int) (c.similarity*height_ratio));
			
				g.drawLine((int)(left_off+top_off+cc.x_pos*width_ratio),
						(int) (top_off+cc.similarity*height_ratio),
						(int) (left_off+top_off+c.x_pos*width_ratio),
						(int) (top_off+cc.similarity*height_ratio));
				g.drawLine((int)(left_off+top_off+c.x_pos*width_ratio),
						(int) (top_off+cc.similarity*height_ratio),
						(int) (left_off+top_off+c.x_pos*width_ratio),
						(int) (top_off+c.similarity*height_ratio));
				if(c.isLeaf)
					g.drawLine((int)(left_off+top_off+c.x_pos*width_ratio),
						(int) (top_off+cc.similarity*height_ratio),
						(int) (left_off+top_off+c.x_pos*width_ratio),
						(int) (0));
				
			}
			
			// DRAW BRANCHES
//			double smallx =0;
//			double bigx = 0;
//			if(cc.branches.get(0).x_pos>cc.branches.get(1).x_pos) {
//				smallx = 5+cc.branches.get(1).x_pos;
//				bigx = 5+cc.branches.get(0).x_pos;
//			}
//			else {smallx = 5+cc.branches.get(0).x_pos;
//			bigx = 5+cc.branches.get(1).x_pos;
//				
//			}
//			g.setColor(Color.white);
//			g.fillRect((int) (smallx*width_ratio), -10,
//					(int)((bigx-smallx)*width_ratio), 
//					(int)(cc.similarity*height_ratio)+3+2*depth);
//
//			g.setColor(Color.black);
//			g.drawRect((int) (smallx*width_ratio), -10,
//					(int)((bigx-smallx)*width_ratio), 
//					(int)(cc.similarity*height_ratio)+3+2*depth);
//			
			
			}
			depth--;
			plane =hold;
			if(lastleaf) {

		        
				return ;}
		}
	}

	
}
