package controllers

import models.{ StudioUser, StudioUserPageInfo, User }
import play.api.mvc.{ AbstractController, Action, AnyContent, ControllerComponents }
import repository.UserRepository

import java.text.SimpleDateFormat
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.util.Try

class ManagerController @Inject() (
  securedManagerAction: SecuredManagerAction,
  cc: ControllerComponents,
  userRepository: UserRepository
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def studioUsers(pageNumber: String): Action[AnyContent] =
    securedManagerAction.async { request: ManagerRequest[AnyContent] =>
      val managerStudioId = request.user.studioId.getOrElse("")

      def toStudioUser(user: User): StudioUser =
        user match {
          case User(
                _,
                name,
                email,
                _,
                _,
                _,
                _,
                role,
                _,
                _,
                _
              ) =>
            // TODO: Change once we have createdOn in Users
            val randomValue          = 0L
            val df: SimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy")
            val date: String         = df.format(randomValue)
            StudioUser(name, email, date, role.toString)
        }

      val pageNumberInt = Try(pageNumber.toInt).getOrElse(0)

      val offset = pageNumberInt * 50

      userRepository.getUserByStudioId(managerStudioId, offset).map { users =>
        val studioUser: Seq[StudioUser] = users.map(toStudioUser)
        val studioUserPageInfo          = StudioUserPageInfo(
          request.user.name,
          request.user.role,
          studioUser,
          pageNumberInt
        )
        Ok(views.html.studioUsers("Studio Users", studioUserPageInfo))
      }
    }

}
