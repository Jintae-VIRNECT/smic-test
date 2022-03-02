package com.virnect.smic.daemon.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NamedThreadFactory implements ThreadFactory {

	private final String name;
	private static AtomicInteger count = new AtomicInteger();

	@Override
	public Thread newThread(@NotNull Runnable r) {
		count.incrementAndGet();
		return new Thread(r, name );
	}

	public int getCount(){
		return count.get();
	}
}
