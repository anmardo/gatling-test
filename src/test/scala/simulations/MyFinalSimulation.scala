package simulations


import java.time.LocalDate

import io.gatling.core.feeder.Feeder
import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.Http

import scala.concurrent.duration.DurationInt
import scala.util.Random

class MyFinalSimulation extends Simulation {


  /** Before & After */

  before{
    println("============================")
    println("STARTING THE SUITE")
    println("============================")
  }

  after{
    println("============================")
    println("FINISHING THE SUITE")
    println("============================")
  }

  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept","application/json")


  /** Variables */
  var myGameId:String="???"
  var idNumbers = (20 to 1000).iterator


  /** Custom Feeder */
  val myFeeder =
      Iterator.continually(Map(
        "gameId" -> idNumbers.next(),
        "name"-> "Sonic 2",
        "releaseDate"-> (LocalDate.now()).toString(),
        "reviewScore"-> "96",
        "category"-> "Plattforms",
        "rating"-> "A"
      ))



  //** HTTP CALLS **//

  def getAllGames() = {
    println("\tGetting details of all games...")
    exec(http("Get All Games")
      .get("videogames")
      .check(status.is(200))
    )
  }


  def createGame() = {

    println("\tCreating my new game...")

    feed(myFeeder)
      .exec(http("Create new game")
        .post("/videogames").body(ElFileBody("bodies/MyGameTemplate.json")).asJson
        .check(status.is(200))
      )

  }

  def removeMyGame() = {

    println("\tRemoving my game from DB...")

    exec(http("removing game")
      .delete(s"videogames/${myGameId}"))
  }

  def getDetailsOfMyGame() ={

    println("\tGetting details of my game...")

    exec(http("get game by id")
    .get(s"videogames/${myGameId}")
    .check(status.is(200)))
  }

  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  /** SCENARIO DESIGN */

  // using the http call, create a scenario that does the following:
  // 1. Get all games
  // 2. Create new Game
  // 3. Get details of that single
  // 4. Delete the game


  var scn = scenario("final test")
    .exec(getAllGames())
    .pause(2 seconds)
    .exec(createGame())
    .pause(2 seconds)
    .exec(getDetailsOfMyGame())
    .pause(2 seconds)
    .exec(removeMyGame())


  /** SETUP LOAD SIMULATION */
  def userCount: Int = getProperty("USERS", "5").toInt
  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt
  def testDuration: Int = getProperty("TEST_DURATION", "60").toInt

  setUp(
    scn.inject(
      nothingFor(5 seconds),
      rampUsers(userCount) during (rampDuration seconds)
    )
  ).protocols(httpConf.inferHtmlResources()).maxDuration(testDuration seconds)



}
