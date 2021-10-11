package com.singingbush.example;

import com.singingbush.example.handlers.JokeHandler;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.micrometer.PrometheusScrapingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class MainVerticle extends AbstractVerticle {

  private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

  private static final String[] GREETINGS = {
    "Hello world!",
    "Bonjour monde!",
    "Hallo Welt!",
    "Hola Mundo!"
  };

  private HttpRequest<JsonObject> request;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    request = WebClient.create(vertx) // (1)
      .get(443, "icanhazdadjoke.com", "/") // (2)
      .ssl(true)  // (3)
      .putHeader("Accept", "application/json")  // (4)
      .as(BodyCodec.jsonObject()) // (5)
      .expect(ResponsePredicate.SC_OK);  // (6)

    //vertx.setPeriodic(3000, id -> fetchJoke());

    vertx.eventBus().consumer("greetings", msg -> {
      // Simulate processing time between 20ms and 100ms
      long delay = ThreadLocalRandom.current().nextLong(80) + 20L;
      vertx.setTimer(delay, l -> {
        // Choose greeting
        String greeting = GREETINGS[ThreadLocalRandom.current().nextInt(GREETINGS.length)];
        msg.reply(greeting);
      });
    });

    vertx.createHttpServer()
      .requestHandler(configureRouter(vertx))
      .listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        log.info("HTTP server started on port 8888");
        //System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private Router configureRouter(final Vertx vertx) {
    final Router router = Router.router(vertx);

    router.get("/jokes").handler(new JokeHandler(vertx));

    router.get("/greeting").handler(rc -> {
      vertx.eventBus().<String>request("greetings", null)
        .map(Message::body)
        .onSuccess(greeting -> rc.response()
          .putHeader("content-type", "text/plain")
          .end(greeting)
        )
        .onFailure(throwable -> {
          throwable.printStackTrace();
          rc.fail(500);
        });
    });



    router.get("/joke").handler(rc -> {
      request.send(asyncResult -> {
        if (asyncResult.succeeded()) {
          rc.response()
            .putHeader("content-type", "text/plain")
            .end(asyncResult.result().body().getString("joke"));

//          System.out.println(asyncResult.result().body().getString("joke")); // (7)
//          System.out.println("🤣");
//          System.out.println();
        } else {
          log.error("Call to get jokes via http client failed");
          //System.err.println("Call to get jokes via http client failed");
          rc.fail(500);
        }
      });
    });

    router.get("/metrics").handler(PrometheusScrapingHandler.create());
    //router.route("/metrics").handler(PrometheusScrapingHandler.create());

    return router;
  }
}
