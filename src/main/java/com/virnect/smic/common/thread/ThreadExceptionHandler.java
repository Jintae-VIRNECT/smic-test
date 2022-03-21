package com.virnect.smic.common.thread;

public class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

	private String handlerId;

	public ThreadExceptionHandler(String handlerId) {
		this.handlerId = handlerId;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.out.println(">>> "+ this + " caught Exception in Thread - \"" +t.getName() + " => "+ e);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "@"+ this.hashCode()+
			(handlerId == null || "".equals(handlerId) ? "" : "(\"" + handlerId+ "\"");
	}
}
