package utils
import java.util.UUID
import javax.mail.internet.MimeMessage
import java.util.Properties
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.Message
import javax.mail.Transport
import play.api.Play
import play.api.i18n.Messages
import play.api.libs.concurrent.Akka
import scala.concurrent.{ Future, ExecutionContext }
import java.util.concurrent.Executors
import akka.util.Timeout
import scala.concurrent.duration._
import play.api.Logger

object MailUtil {

  def securityToken: String = {
    UUID.randomUUID().toString
  }

  val mailServer = Play.current.configuration.getString("mail_smtp_user").get
  val supportMailString = "knolknewsfeed"
  val serverProtocol = "smtp.gmail.com"
  val serverPort = "587"
  val email_password = Play.current.configuration.getString("mail_smtp_password").get
  val protocolName = "smtp"
  val break = "<br/>"

  private def setEmailCredentials = {
    val props = new Properties
    props.setProperty("mail.transport.protocol", protocolName)
    props.setProperty("mail.smtp.starttls.enable", "true")
    props.setProperty("mail.smtp.port", serverPort)
    props.setProperty("mail.smtp.host", serverProtocol)
    props.setProperty("mail.smtp.user", mailServer)
    props.setProperty("mail.smtp.password", email_password)
    val session = Session.getDefaultInstance(props, null)
    val mimeMessage = new MimeMessage(session)
    (mimeMessage, session)
  }: (MimeMessage, Session)

  /**
   * Send Email For Forgot Password Functionality
   * @param emailId to which email has to be sent
   * @param password is the random passowrd generated for that user
   */
  def sendEmailToRegeneratePassword(emailId: String, token: String) = {
    
    val authenticatedMessageAndSession = setEmailCredentials
    val recepientAddress = new InternetAddress(emailId)
    authenticatedMessageAndSession._1.setFrom(new InternetAddress(supportMailString, supportMailString))
    authenticatedMessageAndSession._1.addRecipient(Message.RecipientType.TO, recepientAddress);
    authenticatedMessageAndSession._1.setSubject("Recover Password");
    authenticatedMessageAndSession._1.setContent(
      "Hi" + break +
        "Your Password Is" + break +
        "<a href='" + getContextUrl + "user/regeneratePassword/" + token + "'> Click Here</a>"
        + break, "text/html");
    val transport = authenticatedMessageAndSession._2.getTransport(protocolName)
    transport.connect(serverProtocol, mailServer, email_password)
    transport.sendMessage(authenticatedMessageAndSession._1, authenticatedMessageAndSession._1.getAllRecipients)

  }

  def sendEmailToKnewsfeed(name: String, emailId: String, message: String) = {
    val authenticatedMessageAndSession = setEmailCredentials
    val recepientAddress = new InternetAddress("knolknewsfeed@gmail.com")
    authenticatedMessageAndSession._1.setFrom(new InternetAddress(supportMailString, supportMailString))
    authenticatedMessageAndSession._1.addRecipient(Message.RecipientType.TO, recepientAddress);
    authenticatedMessageAndSession._1.setSubject("knewsfeed Feedback");
    authenticatedMessageAndSession._1.setContent(
      contactUsMessage(name, emailId, message)
        + break, "text/html");
    val transport = authenticatedMessageAndSession._2.getTransport(protocolName)
    transport.connect(serverProtocol, mailServer, email_password)
    transport.sendMessage(authenticatedMessageAndSession._1, authenticatedMessageAndSession._1.getAllRecipients)
  }

  /**
   * Send Email Functionality
   * @param emailId to which email has to be sent
   * @param password is the random passowrd generated for that user
   */
  def sendEmail(name: String, emailId: String, message: String) = {
    val authenticatedMessageAndSession = setEmailCredentials
    val recepientAddress = new InternetAddress(emailId)
    authenticatedMessageAndSession._1.setFrom(new InternetAddress(supportMailString, supportMailString))
    authenticatedMessageAndSession._1.addRecipient(Message.RecipientType.TO, recepientAddress);
    authenticatedMessageAndSession._1.setSubject("knewsfeed Feedback");
    authenticatedMessageAndSession._1.setContent(
      "This is an acknowledgement mail from Knewsfeed. <br><br>" +
        "Thanks <b>" + name + "</b> for contacting us.<br>" + signature
        + break, "text/html");
    val transport = authenticatedMessageAndSession._2.getTransport(protocolName)
    transport.connect(serverProtocol, mailServer, email_password)
    transport.sendMessage(authenticatedMessageAndSession._1, authenticatedMessageAndSession._1.getAllRecipients)
  }

  def contactUsMessage(name: String, email: String, message: String): String = {
    "<b>" + name + "</b>" + " sent you a mail through Scala Geek contact us page <b>" +
      "<br><br><b> Sender Email Address is : </b>" + email + "<br><b> Message :" + message + " </b><br>"
  }

  def contactUsAckMail(name: String): String = {
    "This is an acknowledgement mail from Scala Geek. <br><br>" +
      "Thanks <b>" + name + "</b> for contacting us.<br>" + signature
  }

  def signature(): String = {
    "<br><b>Thanks & Regards !<br>Knewsfeed Support Team <br>http://www.knewsfeed.com<b>"
  }

  /**
   * To get The root context
   */
  def getContextUrl: String = {
    Play.current.configuration.getString("contextUrl").get
  }

  /**
   * Send Email For Forgot Password Functionality
   */
  def sendMailForForgotPassword(emailId: String, token: String) {
    val executorService = Executors.newCachedThreadPool
    implicit val executionContextExecutor = ExecutionContext.fromExecutorService(executorService)
    implicit val timeout = Timeout(400 seconds)
    Future { sendEmailToRegeneratePassword(emailId, token) }
  }

  /**
   * Send Email For Forgot Password Functionality
   */
  def sendMailFromContact(name: String, emailId: String, message: String) {
    val executorService = Executors.newCachedThreadPool
    implicit val executionContextExecutor = ExecutionContext.fromExecutorService(executorService)
    implicit val timeout = Timeout(400 seconds)
    Future { sendEmail(name, emailId, message) }
    Future { sendEmailToKnewsfeed(name, emailId, message) }
  }
}
