package org.graniteds.traderfeed;

import java.util.Date;


public class Stock {
	
	public String code;
	public double last;
	public double high;
	public double low;
	public double change;
	public long timestamp;
	
	public Stock(String code, double init) {
		this.code = code;
		this.last = init;
		this.change = 0.0;
		this.high = this.last;
		this.low = this.last;
		this.timestamp = new Date().getTime();
	}
	
	public void apply(double percent) {
		last = last * (1.0 + percent/100.0);
		if (last < high/10.0)
			last = high/10.0;
		change = percent;
		if (last > high)
			high = last;
		if (last < low)
			low = last;
		timestamp = new Date().getTime();
	}
	
	public void touch() {
		change = 0.0;
		timestamp = new Date().getTime();
	}
}