package com.virnect.smic.common.util;

import java.util.Arrays;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.ServerTypeNode;
import org.eclipse.milo.opcua.sdk.client.model.nodes.variables.ServerStatusTypeNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.enumerated.ServerState;
import org.eclipse.milo.opcua.stack.core.types.structured.ServerStatusDataType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpcUaServerConfigUtil {
    
    public static void getServerConfigInfo(OpcUaClient client) throws UaException {
		ServerTypeNode serverNode = (ServerTypeNode) client.getAddressSpace().getObjectNode(
			Identifiers.Server,
			Identifiers.ServerType
		);

		// Read properties of the Server object...
		String[] serverArray = serverNode.getServerArray();
		String[] namespaceArray = serverNode.getNamespaceArray();

		log.info("ServerArray={}", Arrays.toString(serverArray));
		log.info("NamespaceArray={}", Arrays.toString(namespaceArray));

		// Read the value of attribute the ServerStatus variable component
		ServerStatusDataType serverStatus = serverNode.getServerStatus();

		log.info("ServerStatus={}", serverStatus);

		// Get a typed reference to the ServerStatus variable
		// component and read value attributes individually
		ServerStatusTypeNode serverStatusNode = serverNode.getServerStatusNode();

		DateTime startTime = serverStatusNode.getStartTime();
		DateTime currentTime = serverStatusNode.getCurrentTime();
		ServerState state = serverStatusNode.getState();

		log.info("ServerStatus.StartTime={}", startTime);
		log.info("ServerStatus.CurrentTime={}", currentTime);
		log.info("ServerStatus.State={}", state);
	}
}
