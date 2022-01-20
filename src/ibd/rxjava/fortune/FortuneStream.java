package ibd.rxjava.fortune;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import io.reactivex.Observable;

public class FortuneStream {
	
	public static Observable<FortuneData> cfrom(String ip, int port) {
		return Observable.create(subscriber -> 
			new Thread(() -> {
				InetAddress addr = null;
				Socket sock = null;
				try {
					addr = InetAddress.getByName(ip);
					sock = new Socket(addr, port);
					InputStream is = sock.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
			
					String line;
					while(!subscriber.isDisposed()) {
						StringBuffer fortuneBuffer = new StringBuffer();
						boolean fortuneFound = false;
						do {
							String fortuneLine = br.readLine();
							if (fortuneLine != null) {
								if (fortuneLine.equals("%")) {
									String fortuneString = new String(fortuneBuffer); 
									FortuneData fortuneData = new FortuneData(fortuneString, new Date()); 
									subscriber.onNext(fortuneData);
									fortuneFound = true;
								}
								else {
									fortuneBuffer.append(fortuneLine);
									fortuneBuffer.append("\n");
								}
							}
							else {
								break;
							}
						} while(!fortuneFound); 
					}
					subscriber.onComplete();
					sock.close();
				} catch (Exception e) {
					subscriber.onError(e);
				}
			}).start()
		);
	}

	public static Observable<FortuneData> from(String ip, int port) {
		return Observable.create(subscriber -> {
			InetAddress addr = null;
			Socket sock = null;
			try {
				addr = InetAddress.getByName(ip);
				sock = new Socket(addr, port);
				InputStream is = sock.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
			
				String line;
				while(!subscriber.isDisposed()) {
					StringBuffer fortuneBuffer = new StringBuffer();
					boolean fortuneFound = false;
					do {
						String fortuneLine = br.readLine();
						if (fortuneLine != null) {
							if (fortuneLine.equals("%")) {
								String fortuneString = new String(fortuneBuffer); 
								FortuneData fortuneData = new FortuneData(fortuneString, new Date()); 
								subscriber.onNext(fortuneData);
								fortuneFound = true;
							}
							else {
								fortuneBuffer.append(fortuneLine);
								fortuneBuffer.append("\n");
							}
						}
						else {
							break;
						}
					} while(!fortuneFound); 
				}
				subscriber.onComplete();
				sock.close();
			} catch (Exception e) {
				subscriber.onError(e);
			}
		});
	}
	
}
