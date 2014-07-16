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
    "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "org.hibernate" % "hibernate-ehcache" % "4.1.8.Final",
    "org.hibernate" % "hibernate-entitymanager" % "4.1.9.Final"
)
