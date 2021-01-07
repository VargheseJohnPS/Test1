package computerdatabase

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import scala.util.Random

class Signin extends Simulation {

  val httpProtocol = http
    .baseUrl("https://demo.nopcommerce.com")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .doNotTrackHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0")


  object Homepage {
    val LoadingHomepage = scenario("register")
      .exec(http("homepage")
        .get("/")
        .check(status.is(200)))
      .pause(3)
  }

  object Login {
    val Email = Iterator.continually(Map("Email" -> (Random.alphanumeric.take(20).mkString + "@foo.com")))
    val Password = Iterator.continually(Map("Password" -> (Random.alphanumeric.take(10).mkString )))

    val LoggingIn = scenario("LoggingIn")

    .exec(http("login")
      .get("/login?returnUrl=%2F")
      .check(css("input[name=__RequestVerificationToken]", "value").ofType[String].saveAs("ResponseBody")))
      .pause(4)
      .exec(http("login")
        .post("/login?returnurl=%2F")
        .formParam("Email", feed(Email))
        .formParam("Password", feed(Password))
        .formParam("__RequestVerificationToken", "${ResponseBody}")
        .formParam("RememberMe", "false"))
      .pause(4)
  }

  object AddProducts {
    val AddingProducts = exec("AddingProducts")
    .exec(http("product1")
      .get("/software"))
      .pause(3)
      .exec(http("product1added")
        .post("/addproducttocart/catalog/12/1/1"))
      .pause(3)
      .exec(http("product2")
        .get("/cell-phones"))
      .pause(2)
      .exec(http("product2added")
        .post("/addproducttocart/catalog/18/1/1"))
  }

  val UserLoginandAddingProducts = scenario("UserActivity")
    .exec(Homepage.LoadingHomepage, Login.LoggingIn, AddProducts.AddingProducts)

  setUp(UserLoginandAddingProducts.inject(constantConcurrentUsers(5).during(120.seconds))throttle(reachRps(2)in(1.seconds),holdFor(120))).protocols(httpProtocol)


}