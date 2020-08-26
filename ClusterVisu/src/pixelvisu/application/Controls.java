package pixelvisu.application;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Controls implements MouseListener{
	
	
	int w,h;
	boolean hi = false;
	Color bg = Color.gray;
	
	public Controls(int wi, int he) {
		w = wi; h = he;
	}
	
	

	public void paint(Graphics g_) {
		Graphics2D g = (Graphics2D) g_;
		paintSettings(g);

	}

	public void paintSettings(Graphics2D g){
		g.setColor(bg);
		g.drawRect(0, 0, w, h);
		
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		hi = true;
		
		System.out.println("hi");
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
