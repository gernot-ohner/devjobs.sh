package dev.ohner
package service


import cats._
import cats.effect.unsafe.implicits.global
import cats.implicits._
//import cats.instances._

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import sttp.client3.UriContext

import com.github.tomakehurst.wiremock.WireMockServer
//import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
//import com.github.tomakehurst.wiremock.core.WireMockConfiguration._

class CrawlerServiceTest extends AnyFunSuite with BeforeAndAfterEach {

  private val port = 8080
  private val hostname = "localhost"
  private val wiremockServer = new WireMockServer(options()
    .port(port))



  test("should be able to make an http request to") {

    val path = "$hostname:$port"
    val uri = uri"$hostname:$port"
//    wiremockServer.stubFor(
//      get(urlEqualTo(uri))
//    ).wil
    stubFor(get(urlEqualTo(path))
      .willReturn(
        aResponse()
          .withStatus(200)))
    // Sometime later
    val cs = new CrawlerService()
    val responseIO = cs.crawlTechnologies(uri)
    val response = responseIO.value.unsafeRunSync()

    // TODO migrate this to the cats way of testing.
    //   i guess in the cats way I might not even need wiremock?

    assert(response.isRight)


  }
  override protected def beforeEach(): Unit = wiremockServer.start()
  override protected def afterEach(): Unit = wiremockServer.stop()
}
