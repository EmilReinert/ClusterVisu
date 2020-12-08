package pixelvisu.visu;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

public class Controls extends JFrame  {
	
	JFrame f;
	int w,h;
	Data data;
	Circuit circ;
	int bound_hold =0;
	
	// Progress Boxes

    JProgressBar progressBar = new JProgressBar(0, 100);

    
    		
	
	public Controls(Data d) {
		w =300; h = 400;
		data = d;
		
		setSize(w	,h); 
	    setLayout(null);
		setResizable(true);
		setVisible(true);
		
		setTitle("Controls");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		update(0);
	}
	
	

	public void update(int status) {
		
		bound_hold = 0;
		// Data Switch
		JComboBox j_data = new JComboBox<String>(data.paths);
	    j_data.setBounds(getBound());  
	    j_data.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	System.out.println("We do a switch");
	        	data.setDataPath((String)j_data.getSelectedItem());
	        }
	    });
	    add(j_data);
		    JTextArea tex = new JTextArea("Dataset:");
		    tex.setBounds(getBoundnt());
		    add(tex);
	    getBound();
	    
	    //Circuit List
	    JComboBox jsl = new JComboBox<String>(data.circ.linkage);
	    jsl.setBounds(50,bound_hold,100,20);  
	    jsl.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	data.circ.l_idx=data.circ.getIndexOf((String)jsl.getSelectedItem());
	        }
	    });
	    add(jsl);
	    
	    JComboBox jss = new JComboBox<String>(data.circ.similarity);
	    jss.setBounds(150,bound_hold,100,20);  
	    jss.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	data.circ.s_idx=data.circ.getIndexOf((String)jss.getSelectedItem());
	        }
	    });
	    add(jss);

		    JTextArea tex1 = new JTextArea("Linkage and Metric:");
		    tex1.setBounds(getBoundnt());
		    add(tex1);
	    
	    // Cluster Progress
//	    this.progressBar.setBounds(getBound());
//	    progressBar.setStringPainted(true);
//	    progressBar.setValue(status);
//	    add(progressBar);
	    
	    
	    //Cluster Activation
	    JButton bs2=new JButton("cluster");  
	    bs2.setBounds(getBound());  
	    bs2.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	progressBar.setValue(0);
	        	data.updateClustering();
	        }
	    });
	    add(bs2); 
	    
	  //SLIDER
	    JSlider s = new JSlider(JSlider.HORIZONTAL, 0, 50, 10);
	    s.setBounds(getBound());
	    s.setValue(data.group_count);
	    s.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				data.group_count=s.getValue();
				data.updateSection();
			}
	    });
	    add(s); 
	    JTextArea tex12 = new JTextArea("Sectioning:");
	    tex12.setBounds(getBoundnt());
	    add(tex12);
		    
		 getBound();

	    JButton b2=new JButton("BaseColor");  
	    b2.setBounds(50,bound_hold,100,20);  
	    b2.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	           data.contrast();
	        }
	    });
	    add(b2);  

	    
	    JButton b5=new JButton("Min Max");  
	    b5.setBounds(150,bound_hold,100,20);  
	    b5.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	           data.minmax();
	        }
	    });
	    add(b5);  

	    JTextArea tex2 = new JTextArea("Color Options:");
	    tex2.setBounds(getBoundnt());
	    add(tex2);
	   
	    getBound();
	    
	    
	    JButton bo=new JButton("size");  
	    bo.setBounds(50,bound_hold,100,20);    
	    bo.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	           data.order("density");
	        }
	    });
	    add(bo); 
	    JButton bo2=new JButton("activity");  
	    bo2.setBounds(150,bound_hold,100,20);  
	    bo2.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	           data.order("weight");
	        }
	    });
	    add(bo2); 
	    
	    JTextArea tex22 = new JTextArea("Sort by:");
	    tex22.setBounds(getBoundnt());
	    add(tex22);
	    
	    
	    
	    setVisible(true);
	    repaint();
	}
	
	Rectangle getBound() {
		bound_hold += 40;
		return new Rectangle(50,bound_hold,200,20);
		
	}
	
	Rectangle getBoundnt() {
		return new Rectangle(50,bound_hold-18,200,20);
		
	}
	
	
}
