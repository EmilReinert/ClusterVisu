package pixelvisu.data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

public class ClusterMakerData extends JFrame implements Runnable {
	public static int WIDTH = 300*3;//TODO make adjustable
	public static int OFF = 30;
	public static int HEIGHT = 600+OFF;	
//	public static int HEIGHT_controls = HEIGHT ;//+30;	
	public Color bg_color = Color.WHITE;
	
	private Thread thread;
	private boolean running;
	private ArrayList<BufferedImage> images = new ArrayList<>();
	public ArrayList<int[]> pixelss= new ArrayList<>();
	
	public Controls controls;
	public ArrayList<VisuData> visus= new ArrayList<>();
	public Scale sc;
	public ColorMapping cm;
//	public VisuSeriation visu;
	
	SingleData data;
	SingleData data_compare;
	
	public ClusterMakerData(){

		sc = new Scale(WIDTH, HEIGHT,bg_color);
		ColorMapping cm = new ColorMapping();
		Data data= new Data(WIDTH, HEIGHT,sc,cm);
		cm.data =data;
		sc.data = data;
		sc.setMax(data.getLength());
		Controls c = new Controls(data);
//		String path ="Data/Memory usage.json";
//		Data d = new Data(path);
		
		controls = c;
		thread = new Thread(this);
		
		addVisu(new VisuData(WIDTH,HEIGHT,data,bg_color,sc,0),0);
		addVisu(new VisuData(WIDTH,HEIGHT,data,bg_color,sc,1),1);
		addVisu(new VisuData(WIDTH,HEIGHT,data,bg_color,sc,2),2);
		
		setSize(WIDTH, HEIGHT);
		setTitle("Pixel Clustering");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.black);
		setLocationRelativeTo(null);		
		
		addComponentListener(new ComponentAdapter() 
		{  
		        public void componentResized(ComponentEvent evt) {
		        	HEIGHT = getHeight();
		        	WIDTH = getWidth();
		        	sc.resize(WIDTH, HEIGHT);
		        	
		        	pixelss = new ArrayList<>();
		        	images = new ArrayList<>();
		    		for(int i =0;i<visus.size();i++) {
						visus.get(i).upSize( WIDTH,HEIGHT,OFF,visus.size());
						
						images.add(new BufferedImage(getWidth(),200, BufferedImage.TYPE_INT_RGB));
						pixelss.add( ((DataBufferInt) images.get(i).getRaster().getDataBuffer()).getData());

		    		}
		        }
		});
		

		setVisible(true);
		start();
	}
	
	private void addVisu(VisuData vi, int off) {
		vi.setSize( WIDTH,HEIGHT,OFF,off,visus.size()+1);
		addMouseListener(vi);addMouseMotionListener(vi);addMouseWheelListener(vi);
		addKeyListener(vi);

		visus.add(vi);
	}
	
	private synchronized void start() {
		running = true;
		thread.start();
	}	

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public void render() {

		
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		// bg
		g.setColor(bg_color);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		//draws info box
//		controls.paint(g);
		
		//draws diagram image
		for(int i=0; i<images.size();i++) 
			if(images.size()>i)
				g.drawImage(images.get(i), 0,visus.get(i).getOff(),images.get(i).getWidth(),images.get(i).getHeight(), null);
		sc.paint(g);
		bs.show();
		
	}
	
	
	
	public void run() {
		long lastTime = System.nanoTime();
		double delta = 0;
		requestFocus();
		while(running) {
			long now = System.nanoTime();
			delta = delta + ((now-lastTime) / (1000000000.0/10));//60 = FPS
			lastTime = now;
			while (delta >= 1)//Make sure update is only happening 60 times a second
			{
				
				//handles all of the logic restricted time
//				visu.setSize( WIDTH,HEIGHT,OFF);
				for(int i =0; i<visus.size();i++) 
					if(visus.size()>i&&pixelss.size()>i)
						visus.get(i).update(pixelss.get(i));
				delta--;
			}
			render();//displays to the screen unrestricted time
//			controls.update();
		}
	}
	public static void main(String[] args)  {
		ClusterMakerData clum = new ClusterMakerData();
	}
	
	
	
	
	
//	public static void serializeDataOut(String savepath, Data d) throws IOException {
//		FileOutputStream fos = new FileOutputStream(savepath);
//		ObjectOutputStream oos = new ObjectOutputStream(fos);
//		oos.writeObject(d);
//		oos.close();
//	}
//
//	public static Data serializeDataIn(String savepath) throws IOException, ClassNotFoundException {
//		System.out.println("loading full data");
//		FileInputStream fin = new FileInputStream(savepath);//System.out.println("file input stream done");
//		BufferedInputStream bis = new BufferedInputStream(fin);
//		ObjectInputStream ois = new ObjectInputStream(bis);//System.out.println("object input stream done");
//		Data loadRoot = (Data) ois.readObject();System.out.println("data transfer done");
//		ois.close();
//		return loadRoot;
//	}
}
