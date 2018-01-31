package com.hp.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

/**
 * 线程辅助类
 * 
 *
 */
public final class ThreadPoolHelper {

	private final static Logger logger = Logger.getLogger(ThreadPoolHelper.class);

	private final static ThreadPoolHelper newInstance = new ThreadPoolHelper();

	private final static int THREAD_POOL_SIZE = 5;

	private final static int QUEUE_SIZE = 20;

	/**
	 * 创建线程池,CPU数+1
	 */
	private final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	/**
	 * 创建容量个数的阻塞队列
	 */
	private final BlockingQueue<Future<Object>> queue = new LinkedBlockingDeque<Future<Object>>(QUEUE_SIZE);

	/**
	 * 实例化CompletionService
	 */
	private final CompletionService<Object> completionService = new ExecutorCompletionService<Object>(threadPool, queue);

	private ThreadPoolHelper() {
	}

	public static final ThreadPoolHelper getInstance() {
		return newInstance;
	}

	/**
	 * 执行线程
	 * 
	 * @param command
	 */
	public void execute(Runnable command) {
		threadPool.execute(command);
	}

	/**
	 * 提交Callable线程
	 * 
	 * @param task
	 * @return
	 */
	public <T> Future<T> submit(Callable<T> task) {
		return threadPool.submit(task);
	}

	/**
	 * 在队列中提交Callable线程
	 * 
	 * @param task
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> Future<T> submitByQueue(Callable<T> task) {
		return (Future<T>) completionService.submit((Callable<Object>) task);
	}
	
	/**
	 * 处理Callable线程返回的Future,在方法submitByQueue后执行
	 * 
	 * @param future
	 * @return
	 */
	public <T> T processByQueue(Future<T> future) {
		try {
			//Future<String> f = completionService.take();
			//从队列要删除
			T result = future.get();
			queue.remove(future);
			return result;
		} catch (Exception e) {
			logger.info(e);
			throw new RuntimeException(e);
		}
	}
}
