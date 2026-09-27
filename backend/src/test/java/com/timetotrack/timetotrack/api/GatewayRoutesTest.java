package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.config.ServicePorts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GatewayRoutesTest {

    private final GatewayVerticle gateway = new GatewayVerticle(8080, ServicePorts.DEFAULT, null);

    private int portFor(String path) {
        return gateway.routeFor(path).orElseThrow().targetPort();
    }

    @Test
    void routesByPrefixOnSegmentBoundaries() {
        assertEquals(8888, portFor("/api/users"));
        assertEquals(8888, portFor("/api/users/me"));
        assertEquals(8891, portFor("/api/time-entries/summary"));
        assertEquals(8893, portFor("/api/auth/login"));
        assertFalse(gateway.routeFor("/api/usersX").isPresent());
        assertFalse(gateway.routeFor("/").isPresent());
    }

    @Test
    void onlyAuthAndDocsArePublic() {
        assertTrue(gateway.routeFor("/api/auth/register").orElseThrow().isPublic());
        assertTrue(gateway.routeFor("/api/docs/openapi.yaml").orElseThrow().isPublic());
        assertFalse(gateway.routeFor("/api/customers").orElseThrow().isPublic());
    }
}
