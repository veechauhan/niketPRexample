/*
package controllers

import org.junit.Ignore
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.http.Status.OK
import play.api.libs.ws.WSClient
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test._
import repository._

import scala.language.postfixOps

class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with MockitoSugar {

  "HomeController Get" should {
    "index" in {
      val contributionRepository: ContributionRepository = mock[ContributionRepository]
      val ws: WSClient                                   = mock[WSClient]
      val securedUserAction: SecuredUserAction           = mock[SecuredUserAction]
      val studioRepository: StudioRepository             = mock[StudioRepository]
      val userRepository: UserRepository                 = mock[UserRepository]
      val cc                                             = mock[ControllerComponents]
      val controller                                     =
        new HomeController(
          ws,
          securedUserAction,
          cc,
          userRepository,
          studioRepository,
          contributionRepository
        )
//      when(contributionRepository.getContributionsByUserId())
      val home                                           = controller.index().apply(FakeRequest(GET, "/"))
      println("--------->" + home)
      status(home) mustBe OK
    }
  }

}
*/
