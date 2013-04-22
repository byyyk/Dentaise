import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "dentaise-web"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    "org.springframework" % "spring-context" % "3.1.3.RELEASE",
    "org.springframework.data" % "spring-data-jpa" % "1.2.0.RELEASE",
    "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "org.hibernate" % "hibernate-ehcache" % "4.1.8.Final",
    "org.hibernate" % "hibernate-entitymanager" % "4.1.9.Final"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
  	ebeanEnabled := false 
    // Add your own project settings here      
  )

}
