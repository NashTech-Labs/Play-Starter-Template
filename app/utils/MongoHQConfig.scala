package utils
import com.mongodb.casbah.MongoConnection
import play.api.Play
import com.mongodb.casbah.MongoDB

object MongoHQConfig {
  val mongoServer = Play.current.configuration.getString("mongoServer").get
  val mongoPort = Play.current.configuration.getString("mongoPort").get.toInt
  val databaseName = Play.current.configuration.getString("databaseName").get
  val dbUserName = Play.current.configuration.getString("dbUserName").get
  val dbUserPassword = Play.current.configuration.getString("dbUserPassword").get

  val mongoDB = MongoConnection(mongoServer, mongoPort)(databaseName)
  mongoDB.authenticate(dbUserName, dbUserPassword)
}