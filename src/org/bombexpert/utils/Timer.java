package org.bombexpert.utils;

public class Timer {
	private long time;
	public Timer(long time) {
		this.time = time;
	}
	
	public Timer(long min, long sec) {
		time = min * 6000 + sec * 100;
	}
	
	public String next() {
		long temp = time%100;
		long sec = (time/100)%60;
		long min = (time/6000);
		
		String tempStr = (temp < 10) ? "0" + temp : temp + "";
		String secStr = (sec < 10) ? "0" + sec : sec + "";
		String minStr = (min < 10) ? "0" + min : min + "";
		
		time --;
		
		return minStr+":"+secStr+" "+tempStr;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public void setTime(long min, long sec) {
		time = min * 6000 + sec * 100;
	}
	
	public boolean isFinish() {
		if(time <= 0)
			return true;
		else
			return false;
	}
}
