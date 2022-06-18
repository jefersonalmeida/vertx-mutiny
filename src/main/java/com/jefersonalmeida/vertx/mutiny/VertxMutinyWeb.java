package com.jefersonalmeida.vertx.mutiny;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxMutinyWeb extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(VertxMutinyWeb.class);

  public static void main(String[] args) {
    Vertx.vertx()
      .deployVerticle(new VertxMutinyWeb())
      .subscribe().with(id -> LOG.info("Started: {}", id));
  }

  @Override
  public Uni<Void> asyncStart() {
    var router = Router.router(vertx);
    router.route().failureHandler(this::failureHandler);
    router.get("/users").respond(this::getUsers);

    return vertx.createHttpServer()
//      .requestHandler(req -> req.response().endAndForget("Hello!"))
      .requestHandler(router)
      .listen(8111)
      .replaceWithVoid();
  }

  private Uni<JsonArray> getUsers(RoutingContext context) {
    final var response = new JsonArray();
    response.add(new JsonObject().put("name", "Helen"));
    response.add(new JsonObject().put("name", "Jeferson"));
    return Uni.createFrom().item(response);
  }

  private void failureHandler(RoutingContext context) {
    context.response().setStatusCode(500).endAndForget("Something went wrong :(");
  }
}