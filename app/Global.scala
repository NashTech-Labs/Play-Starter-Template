import play.api._
import play.api.mvc.Results.InternalServerError
import play.api.Logger
import play.api.mvc.RequestHeader
import play.api.mvc.SimpleResult
import play.api.mvc.Result
import play.api.mvc.Results._
import play.api.mvc.Handler

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }
  
  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    if (!request.toString.contains("assets") && !request.toString.contains("javascriptRoutes")) {
      Logger.info("Request:" + request.toString)
    }
    super.onRouteRequest(request)
  }

}