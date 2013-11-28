package controllers

import play.api.mvc.Controller
import org.scribe.oauth.OAuthService
import play.api.Play
import play.api.mvc.Action
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.GoogleApi
import org.scribe.model.Token
import play.api.Logger
import org.scribe.model.Verifier
import org.scribe.model.OAuthRequest
import org.scribe.model.Verb
import org.scribe.model.Response
import play.api.libs.json.Json
import models.UserModel
import utils.EncryptionUtility
import org.bson.types.ObjectId
import play.api.i18n.Messages



object Google extends Controller {
  val apiKey: String = "86006814210-qte4kti9s3m251fepttl2nhvdjs0.apps.googleusercontent.com"
  val apiSecret: String = "gry9NiXEkxlR7nYw8OFoS"
  var requestToken: Token = null
  val authorizationUrlGoogle: String = "https://www.google.com/accounts/OAuthAuthorizeToken?oauth_token="
  val protectedResourceUrl: String = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json"
  val scope: String = "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email"
  val currentUserId = "userId"

  /**
   * Get OAuthService Request
   */
  def getOAuthService: OAuthService = {
    var service: OAuthService = new ServiceBuilder()
      .provider(classOf[GoogleApi])
      .apiKey(apiKey)
      .apiSecret(apiSecret)
      .scope(scope)
      .callback("http://" + getContextUrl + "/google/callback")
      .build();
    service
  }

  /**
   * To get The root context from application.config
   */
  def getContextUrl: String = {
    Play.current.configuration.getString("contextUrl").get
  }

 def googleLogin: Action[play.api.mvc.AnyContent] = Action {
    try {
      requestToken = getOAuthService.getRequestToken();
      val authorizationUrl: String = authorizationUrlGoogle + requestToken.getToken()
      Redirect(authorizationUrl)
    } catch {
      case ex : Throwable => {
        Logger.error("Error During Login Through Google - " + ex)
        Ok(views.html.RedirectMain("", "failure"))
      }
    }
  }


  def googleCallback: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    try {
      getVerifier(request.queryString) match {
        case None => Ok("")
        case Some(oauth_verifier) =>
          val verifier: Verifier = new Verifier(oauth_verifier)
          val accessToken: Token = getOAuthService.getAccessToken(requestToken, verifier)
          val oAuthRequest: OAuthRequest = new OAuthRequest(Verb.GET, protectedResourceUrl)
          getOAuthService.signRequest(accessToken, oAuthRequest)
          oAuthRequest.addHeader("GData-Version", "3.0")
          val response: Response = oAuthRequest.send
          response.getCode match {
            case 200 =>
              val json = Json.parse(response.getBody)
              val userEmail = (json \ "email").asOpt[String]
              UserModel.findUserByEmail(userEmail.get) match {
                case None =>
                  val password = EncryptionUtility.generateRandomPassword
                  val user = UserModel(new ObjectId, userEmail.get, password)
                  val userOpt = UserModel.createUser(user)
                  userOpt match {
                    case None => Redirect("/").flashing("error" -> Messages("error"))
                    case Some(userId) =>
                      val userSession = request.session + ("userId" -> user.id.toString)
                      Ok(views.html.RedirectMain(user.id.toString, "success")).withSession(userSession)
                  }
                case Some(alreadyExistingUser) =>
                  val userSession = request.session + ("userId" -> alreadyExistingUser.id.toString)
                  Ok(views.html.RedirectMain(alreadyExistingUser.id.toString, "success")).withSession(userSession)
              }
            case 400 =>
              Logger.error("Error 400 :  During Login Through Google- " + response.getBody)
              Ok(views.html.RedirectMain("", "failure"))
            case _ =>
              Logger.error("Error " + response.getCode + " : During Login Through Google - " + response.getBody)
              Ok(views.html.RedirectMain("", "failure"))
          }
        }
    } catch {
      case ex:Throwable => {
        Logger.error("Error During Login Through Google - " + ex)
        Ok(views.html.RedirectMain("", "failure"))
      }
    }
  }

  def getVerifier(queryString: Map[String, Seq[String]]): Option[String] = {
    val seq = queryString.get("oauth_verifier").getOrElse(Seq())
    seq.isEmpty match {
      case true => None
      case false => seq.headOption
    }
  }
}