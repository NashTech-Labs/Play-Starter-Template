name := "public-pulse"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.2"

org.scalastyle.sbt.ScalastylePlugin.Settings

libraryDependencies ++= Seq(
	"org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test",
	"org.mongodb" % "casbah_2.10" % "2.6.2",
	"com.novus" % "salat-core_2.10" % "1.9.2",
	"commons-codec" % "commons-codec" % "1.8",
	"net.liftweb" % "lift-webkit_2.10" % "2.5-M4",
	"commons-codec" % "commons-codec" % "1.8",
	"org.twitter4j" % "twitter4j-core" % "3.0.3",
	"com.restfb" % "restfb" % "1.6.9",
	"org.scribe" % "scribe" % "1.3.5",
	"com.typesafe.akka" %% "akka-actor" % "2.2.1",
    "com.typesafe.akka" %% "akka-remote" % "2.2.1"
    
)     

play.Project.playScalaSettings
