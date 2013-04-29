package org.graniteds.traderfeed;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.granite.gravity.Gravity;
import org.granite.gravity.GravityManager;

import flex.messaging.messages.AsyncMessage;

public class RandomStockPublisher implements ServletContextListener, Runnable {

	private Gravity gravity;
	private Random random;
	private ScheduledExecutorService scheduler;
	
	private String[] codes = { "IBM", "ORCL", "GOOG", "MSFT", "AAPL", "ADBE", "EMC", "YHOO", "RHT", "FB", "INTC", "CSCO", "AMZN" };
	private Map<String, Stock> stocks = new HashMap<String, Stock>(codes.length);
	
	
	public void contextInitialized(ServletContextEvent event) {
		try {
			gravity = GravityManager.start(event.getServletContext());
		} 
		catch (ServletException e) {
			throw new RuntimeException(e);
		}
		
		random = new Random();
		for (String code : codes)
			stocks.put(code, new Stock(code, 10.0 + 100.0*random.nextDouble()));
		
		scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this, 500, 250, TimeUnit.MILLISECONDS);
	}
	
	public void contextDestroyed(ServletContextEvent event) {
		scheduler.shutdownNow();
		try {
			scheduler.awaitTermination(10, TimeUnit.SECONDS);
		} 
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		gravity = null;
		random = null;
	}

	public void run() {
		try {
			gravity.initThread(null, null);
			
			for (Entry<String, Stock> e : stocks.entrySet()) {
				int change = random.nextInt(4);
				if (change == 0) {
					double percent = random.nextDouble()*4.0 - 2.0; // between -2 and +2%
					e.getValue().apply(percent);
				}
				else
					e.getValue().touch();
				
				AsyncMessage message = new AsyncMessage();
				message.setDestination("market-data-feed");
				message.setHeader(AsyncMessage.SUBTOPIC_HEADER, "stock");
				message.setHeader("code", e.getKey());
				message.setBody(e.getValue());
				gravity.publishMessage(message);
			}
		}
		finally {
			gravity.releaseThread();
		}
	}
}
