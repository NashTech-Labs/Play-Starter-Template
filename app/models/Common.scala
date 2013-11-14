package models
import net.liftweb.json.JsonDSL
import net.liftweb.json.MappingException
import net.liftweb.json.TypeInfo
import net.liftweb.json.Formats
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.Serialization
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.NoTypeHints
import net.liftweb.json.CustomSerializer
import net.liftweb.json.JsonAST.JObject
import org.bson.types.ObjectId
import net.liftweb.json.JsonAST.JField
import net.liftweb.json.Serializer
import net.liftweb.json.JsonAST.JInt
import net.liftweb.json.TypeInfo
import play.api.Play
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * class to show alert
 * @param alertType the Alert Type
 * @param message the message on Alert
 */
case class Alert(alertType: String,
  message: String)

/**
 * class for creating Contact Us Form
 * @param name is mail sender name
 * @param emailAddress is sender email id
 * @param subject is mail subject
 * @param message is sender message
 */
case class ContactUsForm(name: String,
  emailAddress: String,
  subject: String,
  message: String)

object Common {

  val break = "<br/>"
  var alert: Alert = new Alert("", "")
  def setAlert(alert: Alert): Unit = this.alert = alert
  val style = """style="color: #2D81B6;
    font-size: 14px;
    font-weight: bold;""""

  /**
   * Set Content For Sending Mail For Daily Job Alert
   * @param jobs is the list of jobs that matches the jobseekere's skills
   * @param jobSeeker is the jobseeker with same the skills required for job
   */

}

/**
 * Override Object To get Object Id In json Response
 */
class ObjectIdSerializer extends Serializer[ObjectId] {
  private val Class = classOf[ObjectId]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), ObjectId] = {
    case (TypeInfo(Class, _), json) => json match {
      case JInt(s) => new ObjectId
      case JString(s) => new ObjectId(s)
      case x: Any => throw new MappingException("Can't convert " + x + " to ObjectId")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: ObjectId => JObject(JField("id", JString(x.toString)) :: Nil)

  }
}

object JobBy extends Enumeration {
  val ScalaJobz = Value(0, "ScalaJobz")
  val Indeed = Value(1, "Indeed")
  val SimplyHired = Value(2, "SimplyHired")
  val CareerBuilder = Value(3, "CareerBuilder")
}
