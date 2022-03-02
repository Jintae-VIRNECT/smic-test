package com.virnect.smic.daemon.config.support;



public interface JobLoader {

	void load(com.virnect.smic.daemon.config.support.ApplicationContextFactory factory);

	void reload(com.virnect.smic.daemon.config.support.ApplicationContextFactory factory);

	void clear();
}
