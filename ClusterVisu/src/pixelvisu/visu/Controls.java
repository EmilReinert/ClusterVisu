package pixelvisu.visu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Controls extends JFrame implements MouseListener{
	
	JFrame f;
	int w,h;
	Data data;
	Circuit circ;
	
	
	public Controls(Data d) {
		w =220; h = 300;
		data = d;
		
		setSize(w	,h); 
	    setLayout(null);
		setResizable(true);
		setVisible(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addMouseListener(this);
		
		update();
	}
	
	

	public void update() {
		// BUTTON
		JButton bs=new JButton("simi");  
	    bs.setBounds(20,100,80,20);  
	    bs.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	           data.section ="similarity";
	           data.updateSection();
	        }
	    });
	    add(bs); 
	    JButton bs2=new JButton("cluster");  
	    bs2.setBounds(100,100,80,20);  
	    bs2.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	data.updateClustering();
	        }
	    });
	    add(bs2); 

	    JButton b2=new JButton("Color");  
	    b2.setBounds(50,130,100,20);  
	    b2.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	           data.contrast();
	        }
	    });
	    add(b2);  
	    

	    JComboBox j_data = new JComboBox<String>(data.paths);
	    j_data.setBounds(50,180,100,20);  
	    j_data.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	data.setDataPath((String)j_data.getSelectedItem());
	        }
	    });
	    add(j_data);
	    
	    
	    JButton bo=new JButton("density");  
	    bo.setBounds(20,160,80,20);  
	    bo.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	           data.order("density");
	        }
	    });
	    add(bo); 
	    JButton bo2=new JButton("activity");  
	    bo2.setBounds(100,160,80,20);  
	    bo2.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	           data.order("weight");
	        }
	    });
	    add(bo2); 
	    
	    
	    //SLIDER
	    JSlider s = new JSlider(JSlider.HORIZONTAL, 0, 50, 10);
	    data.group_count=s.getValue();
	    s.setBounds(50, 50, 200,20);
	    s.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				data.group_count=s.getValue();
			}
	    });
	    add(s);
	    
	    //Circuit List
	    JComboBox jsc = new JComboBox<String>(data.circ.clustering);
	    jsc.setBounds(0,10,70,20);  
	    jsc.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	data.circ.c_idx=data.circ.getIndexOf((String)jsc.getSelectedItem());
	        }
	    });
	    add(jsc);
	    JComboBox jsl = new JComboBox<String>(data.circ.linkage);
	    jsl.setBounds(80,10,70,20);  
	    jsl.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	data.circ.l_idx=data.circ.getIndexOf((String)jsl.getSelectedItem());
	        }
	    });
	    add(jsl);
	    JComboBox jss = new JComboBox<String>(data.circ.similarity);
	    jss.setBounds(160,10,70,20);  
	    jss.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	data.circ.s_idx=data.circ.getIndexOf((String)jss.getSelectedItem());
	        }
	    });
	    add(jss);
	    
	    setVisible(true);   
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
