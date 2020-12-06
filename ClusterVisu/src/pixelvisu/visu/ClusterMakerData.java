package pixelvisu.visu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;


public class ClusterMakerData extends JFrame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int WIDTH = 300*3;//TODO make adjustable
	public static int OFF = 30;
	public static int HEIGHT = 600+OFF;	
//	public static int HEIGHT_controls = HEIGHT ;//+30;	
	public Color bg_color = Color.WHITE;
	
		
		private Thread thread;
		private boolean running;
		private BufferedImage image;
		public int[] pixels;
		public byte[]pixels_b;
		
		public Controls controls;
		public VisuData visu; 
		public Scale sc;
		public ColorMapping cm;
//		public VisuSeriation visu;
		
		SingleData data;
		SingleData data_compare;
		boolean mousemoved = true;
		public ClusterMakerData(){

			sc = new Scale(getWidth(), getHeight(),bg_color);
			ColorMapping cm = new ColorMapping();
			Data data= new Data(WIDTH, HEIGHT,sc,cm);
			cm.data =data;
			sc.data = data;
			sc.setMax(data.getLength());
			Controls c = new Controls(data);
//			String path ="Data/Memory usage.json";
//			Data d = new Data(path);
			
			controls = c;
			thread = new Thread(this);
			image = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_RGB);
			pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
			
			
			visu = new VisuData(WIDTH,HEIGHT,data,bg_color,sc,0);
			visu.setSize(WIDTH, HEIGHT, OFF, 0, 1);
//			visu = new VisuSeriation(WIDTH,HEIGHT,d, bg_color);

			addMouseListener(visu);addMouseMotionListener(visu);addMouseWheelListener(visu);
			addKeyListener(visu);
			
			setSize(WIDTH, HEIGHT);
			setTitle("Pixel Clustering");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBackground(Color.red);
			setLocationRelativeTo(null);		
			
			addComponentListener(new ComponentAdapter() 
			{  
			        public void componentResized(ComponentEvent evt) {
			        	HEIGHT = getHeight();
			        	WIDTH = getWidth();
			        	sc.resize(getWidth(), getHeight());
			        	
							visu.upSize( WIDTH,HEIGHT,OFF,2);
							
							image =new BufferedImage(getWidth(),getHeight()/2-OFF, BufferedImage.TYPE_INT_RGB);
							pixels= ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

			    		
			        }
			});
			
			
			setVisible(true);
			start();
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
		
		
		public void render(int ti) {

			
			BufferStrategy bs = getBufferStrategy();
			if(bs == null) {
				createBufferStrategy(3);
				return;
			}
			Graphics g = bs.getDrawGraphics();
			// bg
			g.setColor(bg_color);
			g.fillRect(0, 0, getWidth(), getHeight()/2);
			
			//draws info box
//			controls.paint(g);
			
			//draws diagram image
			g.drawImage(image, 0,visu.getOff(),image.getWidth(),image.getHeight(), null);
			//only draw coords every 10th frame
			
				sc.paint(g,ti);
			bs.show();
			
		}
		
		
		
		public void run() {
			long lastTime = System.nanoTime();
			double delta = 0;
			requestFocus();
			int ti = 0;
			while(running) {
				long now = System.nanoTime();
				delta = delta + ((now-lastTime) / (1000000000.0/4));//60 = FPS
				lastTime = now;
				while (delta >= 1)//Make sure update is only happening 60 times a second
				{
					
					//handles all of the logic restricted time
//					visu.setSize( WIDTH,HEIGHT,OFF);
							visu.update(pixels);
					delta--;
				}
				render(ti);//displays to the screen unrestricted time
//				controls.update();

				ti++;
			}
		}
		public static void main(String[] args)  {
			@SuppressWarnings("unused")
			ClusterMakerData clum = new ClusterMakerData();
		}
		
	}
