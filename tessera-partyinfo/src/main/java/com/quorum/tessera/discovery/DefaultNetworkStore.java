package com.quorum.tessera.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

enum DefaultNetworkStore implements NetworkStore {

    INSTANCE;

    private final Set<ActiveNode> activeNodes = ConcurrentHashMap.newKeySet();

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultNetworkStore.class);

    @Override
    public NetworkStore store(ActiveNode activeNode) {

        activeNodes.removeIf(a -> a.getUri().equals(activeNode.getUri()));
        activeNodes.add(activeNode);

        LOGGER.debug("Stored node {}. Active node count {}",activeNode.getUri(),activeNodes.size());
        return this;
    }

    @Override
    public NetworkStore remove(NodeUri nodeUri) {
        activeNodes.removeIf(a -> a.getUri().equals(nodeUri));
        LOGGER.debug("Removed node {}. Active node count {}",nodeUri,activeNodes.size());
        return this;
    }

    @Override
    public Stream<ActiveNode> getActiveNodes() {
        return activeNodes.stream();
    }



}
