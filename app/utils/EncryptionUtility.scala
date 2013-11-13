package utils

import java.security.MessageDigest
import org.apache.commons.lang3.RandomStringUtils

object EncryptionUtility {

  /**
   * Generate HexString For Password Encryption
   * @param messageDigest is the byteArray that will return a hex-string
   */
  def getHexString(messageDigest: Array[Byte]): String = {
    val hexString: StringBuffer = new StringBuffer
    messageDigest foreach { digest =>
      val hex = Integer.toHexString(0xFF & digest)
      if (hex.length == 1) hexString.append('0')
      else hexString.append(hex)
    }
    hexString.toString
  }

  /**
   * Password Hashing Using Message Digest Algorithm
   * @param password that needs to be encrypted
   */
  def encryptPassword(password: String): String = {
    val algorithm: MessageDigest = MessageDigest.getInstance("SHA-256")
    val defaultBytes: Array[Byte] = password.getBytes
    algorithm.reset
    algorithm.update(defaultBytes)
    val messageDigest: Array[Byte] = algorithm.digest
    getHexString(messageDigest)
  }

  /**
   * Generate Random Alphanumeric String of Length 10 For Password
   */
  def generateRandomPassword: String = {
    RandomStringUtils.randomAlphanumeric(10)
  }

}