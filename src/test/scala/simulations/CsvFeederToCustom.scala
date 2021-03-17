package simulations


import io.gatling.core.Predef._
import io.gatling.http.Predef._


class CsvFeederToCustom extends Simulation {

  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept","application/json")

  var idNumbers=(1 to 10).iterator

  val customFeeder = Iterator.continually(Map("gameID" ->idNumbers.next()))


  def getSpecificVideoGame()= {
    repeat(10){
      feed(customFeeder)
        .exec(http("Get specific videogame")
          .get("videogames/${gameID}")
          .check(status.is(200))
        ).pause(1)
    }
  }

  val scn = scenario("Csv Feeder test").exec(getSpecificVideoGame())


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
