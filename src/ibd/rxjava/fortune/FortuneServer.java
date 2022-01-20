package ibd.rxjava.fortune;

import java.io.IOException;

public class FortuneServer {
	
	public static void main(String[] args) throws Exception {
		if (args.length == 2) {
			int port = Integer.parseInt(args[0]);
			int delay = Integer.parseInt(args[1]);
			FortuneService service = new FortuneService(port, delay);
			Thread serviceThread = new Thread(service);
			serviceThread.start();
			System.out.println("Hit a key to stop Fortune server");
			try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			service.stop();
		} else {
			System.err.println("Usage : Fortune <port> <emit-delay ms>");
		}
	}
}
