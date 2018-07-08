package de.mq.iot.state.support;


import java.net.InetAddress;
import java.util.stream.IntStream;

public class TestMain {
	
	public static void main(String[] args)  {
	
		IntStream.range(100, 111).forEach(address -> {
			try {
				System.out.println(address + ":" +  InetAddress.getByName("192.168.2." + address).getHostName());
					
				
			
			} catch (Exception e) {
				System.out.println("sucks:" + ("192.168.2." + address));
			}
			
		
		});
		
	
	}
		
	
}

