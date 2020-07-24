package pixelvisu.seriation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Panel extends JPanel{
	boolean running;
	Cluster c;
	JFrame f;
	int w, h;
	
	public Panel() {
		w =800; h = 500;
		f = new JFrame("Tree");
		f.setSize(w	,h);
		f.setResizable(true);
		f.add(this);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	


	public void update(Cluster c) {
		this.c = c;
		System.out.println("repaint");
		repaint();
	}

	@Override
	public void paint(Graphics g_) {
		Graphics2D g = (Graphics2D) g_;
		g.clearRect(0, 0, (int) (w*1.2), h);
		if (c != null)
			paintCluster(g);

	}

	
	public void paintCluster( Graphics2D g) {
		// will only be called if cluser is defined
		System.out.println("Painting Cluster "+c.name);
		
		double height_ratio = 0.9*h/c.treeorder.branches.get(0).similarity;
		double width_ratio = w/(c.treeorder.branches.get(0).getDepth()*2);
		System.out.println("Max Sim = "+c.treeorder.branches.get(0).similarity+"; Max Depth = "+w/c.treeorder.branches.get(0).getDepth());
		
		ArrayList<Node> plane = c.treeorder.branches;
		for(int i = 0; i<100000;i++) {
			ArrayList<Node> hold = new ArrayList<Node>();
			boolean lastleaf =true;
			for(Node cc: plane) {for(Node c:cc.branches) {
				if(!c.isLeaf)lastleaf=false;
			}}
			
			for(Node cc: plane) {for(Node c:cc.branches) {
				
				if(!c.isLeaf) hold.add(c);
				/*
				// DRAW BRANCHES Lines
				g.drawLine((int)(cc.x_pos*width_ratio),
						(int) (cc.similarity*height_ratio),
						(int) (c.x_pos*width_ratio),
						(int) (c.similarity*height_ratio));
				*/
			}
			// DRAW BRANCHES
			double smallx =0;
			double bigx = 0;
			if(cc.branches.get(0).x_pos>cc.branches.get(1).x_pos) {
				smallx = 5+cc.branches.get(1).x_pos;
				bigx = 5+cc.branches.get(0).x_pos;
			}
			else {smallx = 5+cc.branches.get(0).x_pos;
			bigx = 5+cc.branches.get(1).x_pos;
				
			}
			g.clearRect((int) (smallx*width_ratio), -1,
					(int)((bigx-smallx)*width_ratio), 
					(int)(cc.similarity*height_ratio));
			
			g.drawRect((int) (smallx*width_ratio), -1,
					(int)((bigx-smallx)*width_ratio), 
					(int)(cc.similarity*height_ratio));
			
			}
			plane =hold;
			if(lastleaf)return ;
		}
	}

	
}
