package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class FixedDurationLoadSimulation extends Simulation{

  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept","application/json")

  def getAllVideoGames() = {
    exec(http("Get all videogames")
      .get("videogames")
      .check(status.is(200))
    )
  }

  def getSpecificGame()={
    exec(http("Get specific game")
      .get("videogames/2")//for example, 2nd videogame
      .check(status.is(200))
    )
  }

  val scn = scenario("Fixed duration Load Simulation")
    .forever() {
      exec(getAllVideoGames())
        .pause(5)
        .exec(getSpecificGame())
        .pause(4)
        .exec(getAllVideoGames())
    }


  setUp(
    scn.inject(
      nothingFor(5 seconds),
      atOnceUsers(10),
      rampUsers(50) during (30 second)
    ).protocols(httpConf.inferHtmlResources())
  ).maxDuration(1 minute)

}
