package com.virnect.smic.common.thread;

public class NamedExceptionHandlingThreadFactory extends NamedThreadFactory{

	public NamedExceptionHandlingThreadFactory(String name) {
		super(name);
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = super.newThread(r);

		t.setUncaughtExceptionHandler(new ThreadExceptionHandler("default"));

		return t;
	}
}
