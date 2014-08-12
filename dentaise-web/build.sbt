import PlayKeys._

name := "dentaise-web"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava).settings(
  	ebeanEnabled := false 
)

libraryDependencies ++= Seq(
    javaCore,
    javaJpa,
    javaWs,
    "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "org.hibernate" % "hibernate-ehcache" % "4.1.8.Final",
    "org.hibernate" % "hibernate-entitymanager" % "4.1.9.Final",
    "org.hibernate" % "hibernate-annotations" % "3.5.6-Final",
    "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.0",
    "joda-time" % "joda-time-hibernate" % "1.3",
    "org.jadira.usertype" % "usertype.core" % "3.1.0.CR1",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-hibernate4" % "2.4.0"
)
