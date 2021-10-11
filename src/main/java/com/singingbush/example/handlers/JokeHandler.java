package com.singingbush.example.handlers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JokeHandler implements Handler<RoutingContext> {

  private static final Logger log = LoggerFactory.getLogger(JokeHandler.class);

  private final HttpRequest<JsonObject> request;

  public JokeHandler(final Vertx vertx) {
    this.request = WebClient.create(vertx) // (1)
      .get(443, "icanhazdadjoke.com", "/") // (2)
      .ssl(true)  // (3)
      .putHeader("Accept", "application/json")  // (4)
      .as(BodyCodec.jsonObject()) // (5)
      .expect(ResponsePredicate.SC_OK);  // (6)
  }

  @Override
  public void handle(RoutingContext rc) {
    this.request.send(asyncResult -> {
      if (asyncResult.succeeded()) {
        rc.response()
          .putHeader("content-type", "text/plain")
          .end(Json.encode(new JokeResponse(asyncResult.result().body().getString("joke"))));
      } else {
        log.error("Call to get jokes via http client failed");
        //System.err.println("Call to get jokes via http client failed");
        rc.fail(500);
      }
    });
  }

  @JsonRootName("joke")
  public static final class JokeResponse {

    @JsonProperty
    private String joke;

    public JokeResponse(String joke) {
      this.joke = joke;
    }

    @JsonGetter
    public String getJoke() {
      return joke;
    }

    public void setJoke(String joke) {
      this.joke = joke;
    }
  }
}
