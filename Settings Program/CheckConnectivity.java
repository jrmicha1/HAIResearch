package networkDrive;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import org.joda.time.LocalTime;

public class CheckConnectivity implements Runnable {
	
	public Thread t;
	private String threadName;
	private boolean suspended = false;
	public String source, target;
	LocalTime morning1;
	LocalTime morning2;
	LocalTime afternoon1;
	LocalTime afternoon2;
	LocalTime afternoon3;
	LocalTime afternoon4;
	LocalTime evening1;
	LocalTime evening2;
	
	public CheckConnectivity(String name, String source, String target, String morning1, String morning2, 
			String afternoon1, String afternoon2, String afternoon3, String afternoon4, String evening1, String evening2) {
		threadName = name;
		this.source = source;
		this.target = target;
		this.morning1 = new LocalTime(morning1);
		this.morning2 = new LocalTime (morning2);
		this.afternoon1 = new LocalTime (afternoon1);
		this.afternoon2 = new LocalTime (afternoon2);
		this.afternoon3 = new LocalTime (afternoon3);
		this.afternoon4 = new LocalTime (afternoon4);
		this.evening1 = new LocalTime (evening1);
		this.evening2 = new LocalTime (evening2);
	}
	
	public void start() {
		t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		
		try {
			
			while(!suspended) {
				
				LocalTime now = LocalTime.now();
				if((now.isAfter(morning1)) && (now.isBefore(morning2)))
					new CheckPresses().run(this.source, this.target, true, true);
				else if((now.isAfter(afternoon1)) && (now.isBefore(afternoon2)))
					new CheckPresses().run(this.source, this.target, true, true);
				else if((now.isAfter(afternoon3)) && (now.isBefore(afternoon4)))
					new CheckPresses().run(this.source, this.target, true, true);
				else if((now.isAfter(evening1)) && (now.isBefore(evening2)))
					new CheckPresses().run(this.source, this.target, true, true);
				
				// Let the thread sleep for a while.
		        Thread.sleep(300);
		        synchronized(this) {
			        while(suspended) {
			           wait();
			        }
		        }
		        
			}
			
		} catch(InvalidPathException e1) {
			System.out.println("Source Path " + this.source + " may be invalid.");
			System.out.println("Target Path " + this.target + " may be invalid.");
			e1.printStackTrace();
		} catch(IOException e1) {
			System.out.println("Copying files failed.");
			e1.printStackTrace();
		} catch(InterruptedException e1) {
			System.out.println("Thread " +  threadName + " interrupted.");
			e1.printStackTrace();
		}
	}

	public void suspend() {
		suspended = true;
	}
	
	synchronized void resume() {
		suspended = false;
		notify();
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		t.stop();
	}
}