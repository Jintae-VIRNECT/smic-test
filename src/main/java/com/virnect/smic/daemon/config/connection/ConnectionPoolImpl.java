package com.virnect.smic.daemon.config.connection;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.util.EndpointUtil;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionPoolImpl implements ConnectionPool {

    private static int INITIAL_POOL_SIZE = 1;
    private static Environment env;
    private static BlockingQueue<Optional<OpcUaClient>> connectionPool = new LinkedBlockingDeque<>(INITIAL_POOL_SIZE);
    private static BlockingQueue<OpcUaClient> usedConnection = new LinkedBlockingDeque<>(INITIAL_POOL_SIZE);

    private static class ConnectionPoolHolder {

        private static final ConnectionPoolImpl INSTANCE = create();
    }

    public static ConnectionPoolImpl getInstance(){
        return ConnectionPoolHolder.INSTANCE;
    }

    private ConnectionPoolImpl(Environment env){
        this.env = env;
    }

    private static ConnectionPoolImpl create() {
        log.info("************************ static pool created ************************ ");

        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            connectionPool.add(Optional.ofNullable(createConnection()));
        }
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
            throw new RuntimeException("no connection available.");
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
        System.out.println("********************** all cleared");
    }

    private static OpcUaClient createConnection() {
         final String SERVER_HOSTNAME = "222.98.122.50";
         final int SERVER_PORT = 4840;

        try {
            List<EndpointDescription> endpoints
                = DiscoveryClient.getEndpoints("opc.tcp://"+ SERVER_HOSTNAME + ":" + SERVER_PORT).get();
            EndpointDescription configPoint = EndpointUtil.updateUrl(endpoints.get(0), SERVER_HOSTNAME, SERVER_PORT);

            // List<EndpointDescription> endpoints
            //     = DiscoveryClient.getEndpoints("opc.tcp://"+ env.getProperty("opc-server.host") + ":" + env.getProperty("opc-server.port")).get();
            // EndpointDescription configPoint = EndpointUtil.updateUrl(endpoints.get(0),  env.getProperty("opc-server.host")
            //     , Integer.parseInt(env.getProperty("opc-server.port")));

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
        log.debug(methodName + "called ******************** connectionPool capacity : " + connectionPool.remainingCapacity());
        log.debug(methodName + "called ******************** usedPool capacity : " + usedConnection.remainingCapacity());
    }

}
