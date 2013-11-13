package controllers

import play.api._
import play.api.i18n.Messages

import play.api.mvc._

object Application extends Controller {

  def index = Action { implicit request =>

    request.session.get("userId") match {
      case None => Ok(views.html.index())

      case Some(userId) =>
        val userSession = request.session + ("userId" -> userId.toString)
        Ok(views.html.index(request.session.get(userId).getOrElse("")))
    }
  }

  def home = Action { implicit request =>
    request.session.get("userId") match {
      case None => Redirect(routes.Application.index)

      case Some(userId) =>
        val userSession = request.session + ("userId" -> userId.toString)
        Ok(views.html.index(request.session.get(userId).getOrElse("")))
    }
  }

  /**
   * Redirect To Login Page When Login Failed Via Social Networks
   */

  def loginFailureViaSocialNetworks: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Redirect("/user/signIn").flashing("error" -> Messages("error"))
  }

  /**
   * JScript Routes
   */
  def javascriptRoutes = Action { implicit request =>

    Ok(
      Routes.javascriptRouter("jsRoutes")()).as("text/javascript")
  }
}
