package me.imdanix.things.utils;

import java.util.concurrent.ThreadLocalRandom;

public class Rnd {

	public static double nextDouble(double d) {
		return ThreadLocalRandom.current().nextDouble(d);
	}

	public static double nextDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}

	public static int nextInt(int d) {
		return ThreadLocalRandom.current().nextInt(d);
	}

	public static int nextInt(int d, int i) {
		return ThreadLocalRandom.current().nextInt(d, i);
	}

	public static boolean nextBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}
}
