package controllers

import com.google.inject.Inject
import models.User
import play.api.mvc.Results._
import play.api.mvc._
import services.AuthService

import scala.concurrent.{ ExecutionContext, Future }

case class UserRequest[T](user: User, request: Request[T]) extends WrappedRequest[T](request)

class SecuredUserAction @Inject() (authService: AuthService, parser: BodyParsers.Default)(implicit ec: ExecutionContext)
    extends ActionBuilder[UserRequest, AnyContent] {

  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] =
    extractBearerToken(request)
      .map { token =>
        authService.authUserByToken(token).flatMap {
          case Some(user) => block(UserRequest(user, request))
          case None       =>
            Future.successful(Redirect(routes.HomeController.signUp))
        }
      }
      .getOrElse(Future.successful(Redirect(routes.HomeController.signUp)))

  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.session.get("authToken")

  override def parser: BodyParser[AnyContent] = parser

  override protected def executionContext: ExecutionContext = ec
}
