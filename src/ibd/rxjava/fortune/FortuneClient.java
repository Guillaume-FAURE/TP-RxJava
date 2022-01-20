package ibd.rxjava.fortune;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class FortuneClient {
	public static void main(String[] straArg) {
		if (straArg.length == 1) {
			fetchFortune(straArg[0]);
		} else {
			System.err.println("Usage : FortuneClient <domain>");
		}
	}

	public static void fetchFortune(String strServer) {
		InetAddress addr = null;
		Socket sock = null;
		try {
			addr = InetAddress.getByName(strServer);
			sock = new Socket(addr, 10000);
			InputStream is = sock.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			int count = 100;
			while(count > 0) {
				StringBuffer fortuneBuffer = new StringBuffer();
				boolean fortuneFound = false;
				do {
					String fortuneLine = br.readLine();
					if (fortuneLine != null) {
						if (fortuneLine.equals("%")) {
							String fortuneString = new String(fortuneBuffer); 
							FortuneData fortuneData = new FortuneData(fortuneString, new Date()); 
							System.out.println(fortuneData);
							fortuneFound = true;
						}
						else {
							fortuneBuffer.append(fortuneLine);
							fortuneBuffer.append("\n");
						}
					}
					else {
						count = 0;
						break;
					}
				} while(!fortuneFound); 
				count--;
			}
			sock.close();
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage() + " not known");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
