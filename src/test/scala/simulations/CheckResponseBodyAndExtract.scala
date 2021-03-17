package simulations


import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseBodyAndExtract extends Simulation {

  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")


  val scn = scenario("Check JSON path")
    .exec(http("Get specific game")
      .get("videogames/1")
      .check(jsonPath("$.name").is("Resident Evil 4")))

    .exec(http("Get all videogames")
      .get("videogames")
      .check(jsonPath("$[1].id")
        .saveAs("gameId")))

    //debugging
    .exec{session=>println(session);session}

    .exec(http("Get specific game")
      .get("videogames/${gameId}")
      .check(status.is(200))
      .check(jsonPath("$.name").is("Gran Turismo 3"))
      .check(bodyString.saveAs("responseBody")))

    //debugging
    .exec{session=>println(session("responseBody").as[String]);session}

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
