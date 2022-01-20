package ibd.rxjava.fortune;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FortuneService implements Runnable {
	protected ServerSocket socketServer = null;
	protected volatile boolean bRun;
	protected FortuneStream fortuneStream; 
	protected int port;
	protected int delay;
	

	public FortuneService(int port, int delay) throws IOException {
		this.port = port;
		this.delay = delay;
		this.fortuneStream = new FortuneStream("fortunes");
		this.fortuneStream.readFile();
	}
	
	public void stop() {
		try {
			if (bRun) {
				bRun = false;
				socketServer.close();
			}
		} 
		catch (IOException e) {
		}
	}

	@Override
	public void run() {
		try {	
			socketServer = new ServerSocket(this.port);
			System.out.println("Fortune service active");
			bRun = true;
			try {
				while (bRun) {
					Socket sock = socketServer.accept();
					System.out.println("Info socket:");
					System.out.println("Inet Address" + sock.getInetAddress());
					System.out.println("Inet Port" + sock.getPort());
					System.out.println("Local Address" + sock.getLocalAddress());
					System.out.println("Local Port" + sock.getLocalPort());
					
					FortuneServiceDialog dialog = new FortuneServiceDialog(sock);	
					Thread dialogThread = new Thread(dialog);
					dialogThread.start();
				}
			} catch (IOException e) {
				System.err.println("Stopping");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Fortune service has run");
	}
	
	public class FortuneServiceDialog implements Runnable {
		protected Socket socket;

		public FortuneServiceDialog(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				// String strTime = _formater.format(new Date());
				OutputStream os = this.socket.getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);
				while(true) {
					String fortune = fortuneStream.getFortune();
					pw.println(fortune);
					pw.println("%");
					try {
						Thread.sleep(delay);
					}
					catch(InterruptedException e) {
					}
				}
				// pw.close();
				// socket.close();
			} catch (IOException e) {
				System.out.println("Fortune Service Interrupted");
			}
		}
	}
	
	class FortuneStream {
		
		String filename;
		List<String> fortunes;
		Random random;
		
		FortuneStream(String filename) {
			this.filename = filename;
			this.fortunes = new LinkedList<String>();
			this.random = new Random();
		}
		
		public void readFile() throws IOException {
			FileInputStream fis = new FileInputStream(this.filename);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			
			boolean eof = false;
			
			while(!eof) {
				StringBuffer fortuneBuffer = new StringBuffer();
				boolean fortuneFound = false;
				do {
					String fortuneLine = br.readLine();
					if (fortuneLine != null) {
						if (fortuneLine.equals("%")) {
							String fortuneString = new String(fortuneBuffer);
							this.fortunes.add(fortuneString);
							fortuneFound = true;
							System.out.println(fortuneString);
						}
						else {
							fortuneBuffer.append(fortuneLine);
							fortuneBuffer.append("\n");
						}
					}
					else {
						eof = true;
						break;
					}

				} while(!fortuneFound);
			}
			
			br.close();
		}
		
		public String getFortune() {
			int size = fortunes.size();
			int randomIdx = this.random.nextInt(size);
			return this.fortunes.get(randomIdx);
		}
 	}
}
