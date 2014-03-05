package org.ct.java.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {

	private static ThreadPoolExecutor sProtocolExecutor = null;
	private static ExecutorService sIOExecutor = null;

	static {
		sProtocolExecutor = new ThreadPoolExecutor(1, 30, 300, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(4),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		sIOExecutor = Executors.newSingleThreadExecutor();
	}

	public static void startProtocol(Thread thread) {
		thread.setPriority(Thread.MAX_PRIORITY);
		sProtocolExecutor.execute(thread);
	}

	public static void startIO(Thread thread) {
		thread.setPriority(Thread.MIN_PRIORITY);
		sIOExecutor.execute(thread);
	}



}
