package com.example.testcases.contentful;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.ensure.Ensure;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.questions.RestQueryFunction;
import org.assertj.core.util.Files;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.apache.http.HttpStatus.SC_CREATED;

@RunWith(SerenityRunner.class)
public class ContentfulIT {


  private Actor actor;

  // ?access_token=9d5de88248563ebc0d2ad688d0473f56fcd31c600e419d6c8962f6aed0150599

  @Before
  public void setUp() {
    Cast cast = Cast.whereEveryoneCan(CallAnApi
        .at("https://graphql.contentful.com/content/v1/spaces/f8bqpb154z8p/environments/master"));
    actor = cast.actorNamed("rest client");
  }

  @Test
  public void query() {
    actor.attemptsTo(Post.to("/").with(query -> {
      query.queryParam("access_token", "9d5de88248563ebc0d2ad688d0473f56fcd31c600e419d6c8962f6aed0150599");
      query.contentType(ContentType.JSON);
      return query.body(
          Files.contentOf(
              Paths.get(this.getClass().getResource("query.json").getPath()).toFile(), "UTF-8"));
    }));
    actor.should(seeThatResponse("stuff is returned", response -> {
      response.statusCode(200);
    }));

    List<Object> items = SerenityRest.lastResponse().path("data.lessonCollection.items");
    actor.attemptsTo(Ensure.that(items.size()).isGreaterThan(0));

  }
}
