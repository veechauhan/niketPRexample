package controllers

import com.typesafe.config.Config
import models.GoogleToken._
import models.UserRole.UserRole
import models._
import models.pageInfo.DashboardPageInfo
import play.api.libs.ws.WSClient
import play.api.mvc._
import repository.{ContributionRepository, StudioRepository, UserRepository}
import utils.JsonExtension._

import java.text.SimpleDateFormat
import java.util.UUID
import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@Singleton
class HomeController @Inject() (
  config: Config,
  ws: WSClient,
  securedUserAction: SecuredUserAction,
  cc: ControllerComponents,
  userRepository: UserRepository,
  studioRepository: StudioRepository,
  contributionRepository: ContributionRepository
) extends AbstractController(cc) {

  def index: Action[AnyContent] =
    securedUserAction.async { implicit request =>
      val session                                          = request.session
      val user                                             = request.user
      val User(Some(userId), _, _, _, _, _, _, _, _, _, _) = user
      val userAuthToken                                    = session.data.get("authToken")

      def toRecentContributions(contribution: Contribution): RecentContribution =
        contribution match {
          case Contribution(
                _,
                contributionName,
                _,
                createdOn,
                _,
                _,
                _,
                contributionType,
                status,
                _,
                _
              ) =>
            val df   = new SimpleDateFormat("dd-MM-yyyy")
            val date = df.format(createdOn)
            RecentContribution(contributionName, contributionType, date, status)

        }

      def getDashboardInfoByUserID(
        userId: String,
        loggedInUserName: String,
        userRole: UserRole
      ): Future[DashboardPageInfo] =
        contributionRepository.blogCountByUserId(userId).flatMap { blogCount =>
          contributionRepository.openSourceCountByUserId(userId).flatMap { openSourceCount =>
            contributionRepository.techHubTemplateCountByUserId(userId).flatMap { techHubCount =>
              contributionRepository.certificationCountByUserId(userId).flatMap { certificationCount =>
                contributionRepository.blogCountForCurrentMonthByUserId(userId).flatMap { blogCountForCurrentMonth =>
                  contributionRepository.openSourceCountForCurrentMonthByUserId(userId).flatMap {
                    openSourceCountForCurrentMonth =>
                      contributionRepository.techHubTemplateCountForCurrentMonthByUserId(userId).flatMap {
                        techHubTemplateCountForCurrentMonth =>
                          contributionRepository.certificationCountForCurrentMonthByUserId(userId).flatMap {
                            certificationCountForCurrentMonth =>
                              contributionRepository.getRecentContributionsByUserId(userId).map { recentContributions =>
                                DashboardPageInfo(
                                  loggedInUserName,
                                  techHubCount,
                                  blogCount,
                                  certificationCount,
                                  openSourceCount,
                                  blogCountForCurrentMonth,
                                  techHubTemplateCountForCurrentMonth,
                                  certificationCountForCurrentMonth,
                                  openSourceCountForCurrentMonth,
                                  userRole,
                                  recentContributions.map(toRecentContributions)
                                )

                              }
                          }
                      }
                  }

                }

              }
            }
          }
        }

      getDashboardInfoByUserID(userId, user.name, user.role).map { dashboardPageInfo =>
        if (userAuthToken.isDefined)
          Ok(views.html.dashboard("Dashboard", dashboardPageInfo))
        else
          Redirect(routes.HomeController.signUp)

      }

    }

  def contributions(pageNumber: Int = 0): Action[AnyContent] =
    securedUserAction.async { implicit request =>
      val user                                                                 = request.user
      val User(Some(userId), _, _, _, _, _, _, _, _, _, _)                     = user
      def toContributionDetail(contribution: Contribution): ContributionDetail =
        contribution match {
          case Contribution(
                _,
                contributionName,
                _,
                createdOn,
                _,
                _,
                _,
                contributionType,
                status,
                _,
                _
              ) =>
            val df   = new SimpleDateFormat("dd-MM-yyyy")
            val date = df.format(createdOn)

            ContributionDetail(contributionName, date, contributionType, status)
        }

      val maximumPageEntries = config.getInt("page-config.maximum-page-entries")

      val offset = pageNumber * maximumPageEntries

      contributionRepository.getContributionsCountByUserId(userId).flatMap { contributionCount =>
        val totalPages = Math.ceil(contributionCount.toDouble / maximumPageEntries).toInt

        contributionRepository.getContributionsById(userId, offset, maximumPageEntries).map { contributions =>
          val contributionDetail    = contributions.map(toContributionDetail)
          val contributionsPageInfo = ContributionPageInfo(
            user.name,
            user.role,
            contributionDetail,
            pageNumber
          )

          Ok(
            views.html.contributions(
              "Contribution",
              contributionsPageInfo,
              contributionsPageInfo.contributionDetail.size,
              totalPages
            )
          )
        }
      }
    }

  def addContribution(
                       contributionName:String,
                       contributionUrl:String,
                       contributionType:String,
                       status:String,
                       remark:String): Action[AnyContent] =
    securedUserAction { implicit request =>
      val remarkUuid = UUID.randomUUID().toString
      val remarks =List(Remark(remarkUuid,remark,0L))
      val contributionTypeInEnum = ContributionType.withName(contributionType)
      val statusInEnum = ContributionStatus.withName(status)
      val contributionId= UUID.randomUUID().toString
      val createdOn =System.currentTimeMillis()
      val lastUpdateOn =System.currentTimeMillis()
      println("--->"+createdOn)
      println("--->"+contributionTypeInEnum)
      println("----->"+statusInEnum)
      val contribution =Contribution(
        Some(contributionId),
        contributionName,
        contributionUrl,
        createdOn,
        0,
        request.user.id match {
          case Some(s:String)=>s
        },
        request.user.studioId match {
          case Some(s:String)=>s
        },
        contributionTypeInEnum,
        statusInEnum,
        remarks,
        0L
      )
      println(Await.result(contributionRepository.addNewContribution(contribution),Duration.Inf))
      Redirect(routes.HomeController.index)

    }



  def signUp: Action[AnyContent] =
    Action { implicit request =>
      Ok(views.html.signup("Sign Up")).withSession()
    }

  def signingIn(authToken: String): Action[AnyContent] =
    Action.async { implicit request =>
      val token  = authToken
      val result = ws.url(s"https://oauth2.googleapis.com/tokeninfo?id_token=$token").get()
      result.flatMap { r =>
        r.body.fromJson[GoogleToken] match {
          case Some(GoogleToken(sub, email, name, picture)) =>
            val updatedPictureLink = picture.split("=").headOption.getOrElse("")
            userRepository.getUserById(sub).flatMap {
              case Some(_) =>
                Future.successful(
                  Redirect(routes.HomeController.index)
                    .withSession("authToken" -> token, "picture" -> updatedPictureLink)
                )
              case None    =>
                userRepository
                  .createNewUser(sub, email, name)
                  .map(_ =>
                    Redirect(routes.HomeController.index)
                      .withSession("authToken" -> token, "picture" -> updatedPictureLink)
                  )
            }

          case None                                         =>
            Future.successful(Redirect(routes.HomeController.signUp).withNewSession)
        }
      }
    }

  def notifications: Action[AnyContent] =
    securedUserAction { request =>
      Ok(views.html.notifications("Notifications", request.user))
    }

  def profile: Action[AnyContent] =
    securedUserAction.async { implicit request =>
      val user = request.user
      user.studioId match {
        case Some(studioId) =>
          studioRepository.getStudioById(studioId).map {
            case Some(studioInfo: Studio) =>
              val profile = Profile(
                user.name,
                user.email,
                studioInfo.studioName,
                user.bio.getOrElse("Bio not set"),
                user.mobileNumber.getOrElse("Mobile Number not set"),
                user.role
              )
              Ok(views.html.profile("Profile", profile))
            case None                     =>
              val profile = Profile(
                user.name,
                user.email,
                "Studio Not Found",
                user.bio.getOrElse("Bio not set"),
                user.mobileNumber.getOrElse("Mobile Number not set"),
                user.role
              )
              Ok(views.html.profile("Profile", profile))
          }
        case None           =>
          val profile = Profile(
            user.name,
            user.email,
            "Studio Not Set",
            user.bio.getOrElse("Bio not set"),
            user.mobileNumber.getOrElse("Mobile Number not set"),
            user.role
          )
          Future.successful(Ok(views.html.profile("Profile", profile)))
      }

    }
}
