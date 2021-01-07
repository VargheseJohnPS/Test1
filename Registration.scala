package computerdatabase
import scala.util.Random
import io.gatling.core.Predef.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class Registration extends Simulation {

  val httpProtocol = http
    .baseUrl("https://demo.nopcommerce.com")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")



  object Homepage {
    val LoadingHomepage = scenario("register")
      .exec(http("homepage")
        .get("/")
        .check(status.is(200)))
      .pause(3)
     }

  object Register{

    val Email = Iterator.continually(Map("Email" -> (Random.alphanumeric.take(20).mkString + "@foo.com")))
    val Firstname = Iterator.continually(Map("FirstName" -> (Random.toString.take(20).mkString )))
    val Lastname = Iterator.continually(Map("LastName" -> (Random.toString.take(20).mkString)))
    val Password = Iterator.continually(Map("Password" -> (Random.alphanumeric.take(10).mkString )))
    val Company = Iterator.continually(Map("Company" -> (Random.toString.take(20).mkString )))

    val Test1feeder = csv("data/Test1.csv").batch.circular

    val Registration = scenario("register")
      .feed(Test1feeder)
      .exec(http("Registration")
      .get("/register?returnUrl=%2F")
      .check(css("input[name=__RequestVerificationToken]", "value").ofType[String].saveAs("ResponseBody")))
    .pause(2)
    .exec(http("Register")
      .post("/register?returnUrl=%2F")
      .formParam("Gender", "F")
      .formParam("FirstName", feed(Firstname))
      .formParam("LastName", feed(Lastname))
      .formParam("DateOfBirthDay","${DD}")
      .formParam("DateOfBirthMonth", "${MM}")
      .formParam("DateOfBirthYear", "${YYYY}")
      .formParam("Email", feed(Email))
      .formParam("Company", feed(Company))
      .formParam("Newsletter", "true")
      .formParam("Password", feed(Password))
      .formParam("ConfirmPassword", feed(Password))
      .formParam("register-button", "Register")
      .formParam("__RequestVerificationToken", "${ResponseBody}")
      .formParam("Newsletter", "false"))
  }


  val UserRegistration = scenario("UserRegistration").repeat(4) {
    exec(Homepage.LoadingHomepage, Register.Registration)
  }
  setUp(UserRegistration.inject(constantConcurrentUsers(5)during(120.seconds))).maxDuration(480).protocols(httpProtocol)
}



