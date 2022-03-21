package com.virnect.smic.common.config.connection;

import org.eclipse.milo.opcua.sdk.client.api.UaClient;

public interface ConnectionPool {
  
    UaClient getConnection();
    <T extends UaClient> boolean releaseConnection(T client);
}
