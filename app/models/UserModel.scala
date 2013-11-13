package models


import play.api.Play
import play.api.Play.current
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import com.novus.salat.dao.SalatDAO
import com.novus.salat.global.ctx
import com.mongodb.casbah.Imports.WriteConcern
import com.mongodb.casbah.MongoConnection
import com.novus.salat.annotations.Key
import utils.MongoHQConfig
import java.util.Date
import mongoContext._


case class Login(emailId: String, password: String)

case class UserModel(
  @Key("_id") id: ObjectId,
  EmailId: String,
  Password: String)
  
  /**
   * Registration Form
   * @emailId: userId
   */
  case class RegistrationForm(
  EmailId: String,
  Password: String,
  ConfirmPassword: String)
  
object UserModel {
  /**
   * Insert User
   */
  def createUser(user: UserModel) = {
    UserModelDAO.insert(user)
  }: Option[ObjectId]

  /**
   * Authenticate User By Credentials Provided
   *
   * @param emailId is emailId of user to be searched
   * @param password is password of user to be searched
   */
  def authenticateUserForSignIn(emailId: String, password: String): Option[UserModel] = {
    val users = UserModelDAO.find(MongoDBObject("EmailId" -> emailId, "Password" -> password)).toList
    users.isEmpty match {
      case true => None
      case false => users.headOption
    }
  }

   /**
   * Find user by Email
   * @param emailId is emailId of the user to be searched
   */
  def findUserByEmail(email: String) = {
    val users = UserModelDAO.find(MongoDBObject("EmailId" -> email)).toList
    (users.isEmpty) match {
      case true => None
      case false => Some(users.head)
    }
  }: Option[UserModel]
  
  /**
   * Search User
   * @param userId is the id of the temp. user to be searched
   */
  def findUserById(userId: ObjectId) = {
    val users = UserModelDAO.findOneById(userId).toList
    (users.size != 0) match {
      case true => Option(users.head)
      case false => None
    }
  }: Option[UserModel]
}

object UserModelDAO extends SalatDAO[UserModel, ObjectId](collection = MongoHQConfig.mongoDB("user"))
