package org.ct.protocol;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {

	private static ThreadPoolExecutor protocolExecutor = null;
	private static ExecutorService downloadExecutor = null;
	private static ExecutorService installExecutor = null;

	static {
		protocolExecutor = new ThreadPoolExecutor(1, 30, 300, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(4),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		downloadExecutor = Executors.newSingleThreadExecutor();
		installExecutor = Executors.newSingleThreadExecutor();
	}

	public static void startProtocol(Thread thread) {
		thread.setPriority(Thread.MAX_PRIORITY);
		protocolExecutor.execute(thread);
	}

	public static void startDownload(Thread thread) {
		thread.setPriority(Thread.MIN_PRIORITY);
		downloadExecutor.execute(thread);
	}

	public static void startInstall(Thread thread) {
		thread.setPriority(Thread.MIN_PRIORITY);
		installExecutor.execute(thread);
	}

}
