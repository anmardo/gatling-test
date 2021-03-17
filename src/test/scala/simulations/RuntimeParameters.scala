package simulations


import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._


class RuntimeParameters extends Simulation {

  //mvn gatling:test -Dgatling.simulationClass=simulations.RuntimeParameters -DUSERS=10 -DRAMP_DURATION=5 -DTEST_DURATION=30

  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  def userCount: Int = getProperty("USERS", "5").toInt

  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt

  def testDuration: Int = getProperty("TEST_DURATION", "60").toInt

  before {
    println(s"Running test with ${userCount} users" )
    println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total test duration: ${testDuration} seconds")
  }


  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept","application/json")

  def getAllVideoGames()={
    exec(http("Get all videogames")
    .get("videogames")
    .check(status.is(200))
    )
  }


  val scn = scenario("Get all videogames")
    .forever(){
        exec(getAllVideoGames())
    }

  setUp(
    scn.inject(
      nothingFor(5 seconds),
      rampUsers(userCount) during (rampDuration)
    )
  ).protocols(httpConf.inferHtmlResources()).maxDuration(testDuration)


}
