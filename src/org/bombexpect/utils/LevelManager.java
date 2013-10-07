package org.bombexpect.utils;

public class LevelManager {
	private static int level = 1;
	
	public static int getLevel() {
		return level;
	}
	
	public static void updateLevel() {
		level ++;
	}
}
