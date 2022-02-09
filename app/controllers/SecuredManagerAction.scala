package controllers

import com.google.inject.Inject
import models.{ User, UserRole }
import play.api.mvc.Results.Redirect
import play.api.mvc.{ ActionBuilder, AnyContent, BodyParser, BodyParsers, Request, Result, WrappedRequest }
import services.AuthService

import scala.concurrent.{ ExecutionContext, Future }

case class ManagerRequest[T](user: User, request: Request[T]) extends WrappedRequest[T](request)

class SecuredManagerAction @Inject() (authService: AuthService, parser: BodyParsers.Default)(implicit
  ec: ExecutionContext
) extends ActionBuilder[ManagerRequest, AnyContent] {

  override def invokeBlock[A](request: Request[A], block: ManagerRequest[A] => Future[Result]): Future[Result] =
    extractBearerToken(request)
      .map { token =>
        authService.authManagerByToken(token).flatMap {
          case Some(user @ User(_, _, _, _, _, _, _, UserRole.STUDIO_MANAGER, _, _, _)) =>
            block(ManagerRequest(user, request))
          case _                                                                        =>
            Future.successful(Redirect(routes.HomeController.index))
        }
      }
      .getOrElse(Future.successful(Redirect(routes.HomeController.signUp)))

  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.session.get("authToken")

  override def parser: BodyParser[AnyContent] = parser

  override protected def executionContext: ExecutionContext = ec
}
