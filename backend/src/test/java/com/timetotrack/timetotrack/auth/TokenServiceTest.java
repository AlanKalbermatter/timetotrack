package com.timetotrack.timetotrack.auth;

import com.timetotrack.timetotrack.error.UnauthorizedException;
import com.timetotrack.timetotrack.model.User;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.timetotrack.timetotrack.support.Await.await;
import static com.timetotrack.timetotrack.support.Await.awaitFailure;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class TokenServiceTest {

    private static final User ANA = new User(42, "ana", "ana@example.com", "Ana");
    private static Vertx vertx;

    @BeforeAll
    static void start() {
        vertx = Vertx.vertx();
    }

    @AfterAll
    static void stop() {
        await(vertx.close());
    }

    @Test
    void issuedTokensVerifyToTheUserId() {
        TokenService tokens = new TokenService(vertx, "secret-a");

        assertEquals(42, await(tokens.verify(tokens.issue(ANA))));
    }

    @Test
    void rejectsTokensSignedWithAnotherSecret() {
        String token = new TokenService(vertx, "secret-a").issue(ANA);

        Throwable failure = awaitFailure(new TokenService(vertx, "secret-b").verify(token));

        assertInstanceOf(UnauthorizedException.class, failure);
    }

    @Test
    void rejectsGarbage() {
        assertInstanceOf(UnauthorizedException.class, awaitFailure(new TokenService(vertx, "secret-a").verify("not-a-jwt")));
    }
}
