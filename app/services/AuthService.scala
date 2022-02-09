package services

import com.google.inject.{ Inject, Singleton }
import models.{ GoogleToken, User, UserRole }
import play.api.libs.ws.WSClient
import utils.JsonExtension.JsonOps
import models.GoogleToken._
import repository.UserRepository

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class AuthService @Inject() (ws: WSClient, userRepository: UserRepository)(implicit ec: ExecutionContext) {

  def authUserByToken(authToken: String): Future[Option[User]] = {
    val result = ws.url(s"https://oauth2.googleapis.com/tokeninfo?id_token=$authToken").get()
    result.map(_.body.fromJson[GoogleToken]).flatMap {
      case Some(token) =>
        userRepository.getUserById(token.sub).map {
          case Some(user @ User(_, _, _, _, _, _, _, _, _, _, 0L)) => Some(user)
          case _                                                         => None
        }
      case None        =>
        Future.successful(None)

    }
  }

  def authManagerByToken(authToken: String): Future[Option[User]] = {
    val result = ws.url(s"https://oauth2.googleapis.com/tokeninfo?id_token=$authToken").get()

    result.map(_.body.fromJson[GoogleToken]).flatMap {
      case Some(token) =>
        userRepository.getUserById(token.sub).map {
          case Some(user @ User(_, _, _, _, _, _, _, UserRole.STUDIO_MANAGER, _, _, 0L)) =>
            Some(user)
          case _                                                                               =>
            None
        }
      case None        => Future.successful(None)

    }
  }

}
