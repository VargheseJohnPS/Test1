package computerdatabase
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.Predef.Simulation

import scala.concurrent.duration.DurationInt

class AddtoCart extends Simulation{
  val httpProtocol = http
    .baseUrl("http://demo.nopcommerce.com") // Here is the root for all relative URLs
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")



  object Homepage {
    val LoadingHomepage = scenario("register")
      .exec(http("homepage")
        .get("/")
        .check(status.is(200)))
      .pause(3)
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
      .pause(3)
      .exec(http("product2")
        .get("/accessories"))
      .pause(2)
      .exec(http("Product3added")
        .post("/addproducttocart/catalog/33/1/1"))
  }

val BrowsingandAdding = scenario("BrowsingandAdding")
  .exec(Homepage.LoadingHomepage,AddProducts.AddingProducts)

  setUp(BrowsingandAdding.inject(rampUsers(10).during(20 minutes)).protocols(httpProtocol))

}
