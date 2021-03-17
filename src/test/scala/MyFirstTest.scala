package test.scala

import io.gatling.core.Predef._
import io.gatling.http.Predef._


class MyFirstTest extends Simulation{

  //1 Http conf
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept","application/json")

  //2 Scenario definition
  val scn = scenario("My First test")
    .exec(http("Get All Games")
      .get("videogames"))

  //3 Load scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
