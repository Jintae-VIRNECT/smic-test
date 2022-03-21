package com.virnect.smic.common.config.connection;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PreDestroy;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.util.EndpointUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionPoolImpl implements ConnectionPool {

    private static Environment env;
    
    private static BlockingQueue<Optional<OpcUaClient>> connectionPool;
    private static BlockingQueue<OpcUaClient> usedConnection;

    private static class ConnectionPoolHolder {

        private static final ConnectionPoolImpl INSTANCE = create();
    }

    public static ConnectionPoolImpl getInstance(){
        return ConnectionPoolHolder.INSTANCE;
    }

    @Autowired
    private ConnectionPoolImpl(Environment env){
        this.env = env;
    }

    private static ConnectionPoolImpl create() {
        int initialPoolSize = Integer.parseInt(env.getProperty("opc-server.pool-size"));
        connectionPool = new LinkedBlockingDeque<>(initialPoolSize);
        usedConnection = new LinkedBlockingDeque<>(initialPoolSize);
        
        for (int i = 0; i < initialPoolSize; i++) {
            connectionPool.add(Optional.ofNullable(createConnection()));
        }

        log.info("static pool created");
        return new ConnectionPoolImpl(env);
    }

    @Override
    public OpcUaClient getConnection() {
        Optional<OpcUaClient> connection = connectionPool.poll();

        if(connection.isPresent()){
            OpcUaClient client = connection.get();
            usedConnection.offer(client);
            logConnectionStatus("getConnection");
            return client;
        }else {
            // if(usedConnection.size() < INITIAL_POOL_SIZE){
            //     log.warn("addtional connection created after startup");
            //     OpcUaClient client = createConnection();
            //     connectionPool.add(Optional.ofNullable(client));
            //     return getConnection();
            // } else{
                throw new NoConnectionAvailableException("no connection available.");
            // }
        }
    }

    @Override
    public <T extends UaClient> boolean releaseConnection(T connection) {
        connectionPool.offer(Optional.ofNullable((OpcUaClient)connection));
        boolean removed = usedConnection.remove(connection);
        logConnectionStatus("releaseConnection");
        return removed;
    }

    // public boolean releaseConnection(OpcUaClient connection) {
    //     connectionPool.offer(Optional.ofNullable(connection));
    //     return usedConnection.remove(connection);
    // }

    public void shutdown() {
        usedConnection.forEach(this::releaseConnection);
        for ( Optional<OpcUaClient> client : connectionPool) {
            client.ifPresent(c-> c.disconnect());
        }
        
        connectionPool.clear();
        System.out.println("connection all cleared");
    }

    private static OpcUaClient createConnection() {

         final String SERVER_HOSTNAME = env.getProperty("opc-server.host");
         final int SERVER_PORT = Integer.parseInt(env.getProperty("opc-server.port"));

        try {
            List<EndpointDescription> endpoints
                = DiscoveryClient.getEndpoints("opc.tcp://"+ SERVER_HOSTNAME + ":" + SERVER_PORT).get();
            EndpointDescription configPoint = EndpointUtil.updateUrl(endpoints.get(0), SERVER_HOSTNAME, SERVER_PORT);

            OpcUaClientConfigBuilder cfg = new OpcUaClientConfigBuilder();
            cfg.setEndpoint(configPoint);

            OpcUaClient client = OpcUaClient.create(cfg.build());
            client.connect().get();

            return client;

        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException("Connection not available", t);
        }
    }

    private void logConnectionStatus(String methodName){
        int initialPoolSize = Integer.parseInt(env.getProperty("opc-server.pool-size"));
        log.info(methodName + "called. connectionPool's available capacity : " 
            + (initialPoolSize - connectionPool.remainingCapacity()));
        log.info(methodName + "called. usedPool's current capacity : " 
            + ( initialPoolSize - usedConnection.remainingCapacity()));
    }

    @PreDestroy
    public void destroy() throws Exception {
        shutdown();
    }

}
