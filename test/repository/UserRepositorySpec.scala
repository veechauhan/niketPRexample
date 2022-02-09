package repository

import models.{ User, UserRole }
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.mvc.Results
import play.test.WithApplication
import utils.CommonSpec

import java.util.UUID

class UserRepositorySpec extends CommonSpec with GuiceOneAppPerTest with Results {

  def userRepository: UserRepository = Application.instanceCache[UserRepository].apply(app)

  "User Repository" must {
    "Get a user by id" in new WithApplication() {
      userRepository.getUserById("aa6e9e04-bd2b-4ff3-8397-570987dbcaef").futureValue.value must be
      User(
        Some("01"),
        "Average Joe",
        "average.joe@email.com",
        None,
        Some("123456789"),
        None,
        None,
        UserRole.USER,
        None,
        None,
        0L
      )
    }

    "not get a user if invalid id provided" in new WithApplication() {
      userRepository.getUserById("INVALID-ID").futureValue must be(None)
    }

    "create a new user" in new WithApplication() {
      val uuid: String              = UUID.randomUUID().toString
      val expectedUserCreated: User = User(Some(uuid), "New Joe", "new.joe@email.com")
      userRepository.createNewUser(uuid, "new.joe@email.com", "New Joe").futureValue must be(1)
      userRepository.getUserById(uuid).futureValue.value must be(expectedUserCreated)
    }

  }

}
