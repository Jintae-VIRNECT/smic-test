package com.virnect.smic.common.config.support;



public interface JobLoader {

	void load(com.virnect.smic.common.config.support.ApplicationContextFactory factory);

	void reload(com.virnect.smic.common.config.support.ApplicationContextFactory factory);

	void clear();
}
