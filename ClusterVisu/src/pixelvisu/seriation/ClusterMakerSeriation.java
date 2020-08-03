package pixelvisu.seriation;

import java.awt.Color;
import java.awt.Graphics;
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

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ClusterMakerSeriation extends JFrame implements Runnable {
	public static int WIDTH = 270*4;//TODO make adjustable
	public static int OFF = 40;
	public static int HEIGHT = 270*3+OFF;	
	private Thread thread;
	private boolean running;
	private BufferedImage image;
	public int[] pixels;
	public byte[]pixels_b;
	public VisuSeriation visu; 
	public Data data;
	public Color bg_color = Color.WHITE;
	
	public ClusterMakerSeriation(Data d, String path) throws IOException {
		thread = new Thread(this);
		image = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		visu = new VisuSeriation(WIDTH,HEIGHT,d, bg_color);
		addMouseListener(visu);addMouseMotionListener(visu);addMouseWheelListener(visu);
		
		addKeyListener(visu);
		
		setSize(WIDTH, HEIGHT);
		setTitle("Pixel Clustering - Visualization /"+path+" IGNORE SINGLES");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.black);
		setLocationRelativeTo(null);		
		
		setVisible(true);
		start();
	}
	

	private synchronized void start() {
		running = true;
		
		visu.drawBackground(pixels);
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
		//draws diagram image
		g.drawImage(image, 0, 0,getWidth(), getHeight(), null);
		bs.show();
	}
	
	
	public void run() {
		long lastTime = System.nanoTime();
		double delta = 0;
		requestFocus();
		while(running) {
			long now = System.nanoTime();
			delta = delta + ((now-lastTime) / (1000000000.0/1));//60 = FPS
			lastTime = now;
			while (delta >= 1)//Make sure update is only happening 60 times a second
			{
				image = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);
				pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
				//handles all of the logic restricted time
				visu.update(pixels, getWidth(),getHeight(),OFF);
				delta--;
			}
			render();//displays to the screen unrestricted time
		}
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String path ="Data/Memory usage.json";
		
		Data d;
		
//		try {
//			d = serializeDataIn("save/data/data");
//		}
//		catch(Exception e) {
//			d = new Data(path);
//			serializeDataOut("save/data/data",d);
//		}
		

		d = new Data(path);
		ClusterMakerSeriation clum = new ClusterMakerSeriation(d, path);
	}
	
	
	
	
	
	public static void serializeDataOut(String savepath, Data d) throws IOException {
		FileOutputStream fos = new FileOutputStream(savepath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(d);
		oos.close();
	}

	public static Data serializeDataIn(String savepath) throws IOException, ClassNotFoundException {
		System.out.println("loading full data");
		FileInputStream fin = new FileInputStream(savepath);//System.out.println("file input stream done");
		BufferedInputStream bis = new BufferedInputStream(fin);
		ObjectInputStream ois = new ObjectInputStream(bis);//System.out.println("object input stream done");
		Data loadRoot = (Data) ois.readObject();System.out.println("data transfer done");
		ois.close();
		return loadRoot;
	}
}
