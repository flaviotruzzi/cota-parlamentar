import sbt.Keys._
import sbt._
import spray.revolver.RevolverPlugin.Revolver
import com.zavakid.sbt._

object CotaParlamentarBuild extends Build {

  lazy val root = Project(base = file("."), id = "cota-parlamentar").enablePlugins(SbtOneLog)

  override val settings = super.settings ++ Seq(
    name := "cota-parlamentar",
    version := "0.01",
    scalaVersion := "2.11.6",
    resolvers ++= Resolvers.resolvers,
    libraryDependencies ++= Dependencies.libraryDependencies,
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-Xlint",
      "-Ywarn-dead-code",
      "-language:_",
      "-target:jvm-1.7",
      "-encoding", "UTF-8",
      "-feature"
    ),
    javaOptions += "-XX:MaxPermSize=1024m",
    parallelExecution in Test := false
  )

}


object Dependencies {

  lazy val libraryDependencies = Seq(
    "com.typesafe.akka" %% "akka-slf4j" % "2.3.9" % "compile",
    "com.typesafe.akka" %% "akka-testkit" % "2.3.9" % "compile",
    "com.typesafe" % "config" % "1.2.1",
    "joda-time" % "joda-time" % "2.7",
    "org.joda" % "joda-convert" % "1.7",
    "io.spray" %%  "spray-json" % "1.3.1",
    "io.spray" %% "spray-can" % "1.3.2",
    "io.spray" %% "spray-routing" % "1.3.2",
    "io.spray" %% "spray-http" % "1.3.2",
    "io.spray" %% "spray-client" % "1.3.2",
    "org.json4s" %% "json4s-native" % "3.2.11",
    "org.json4s" %% "json4s-ext" % "3.2.11",
    "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "com.sksamuel.elastic4s" %% "elastic4s" % "1.4.13"
  )
}

object Resolvers {

  lazy val resolvers = Seq(
    Resolver.sonatypeRepo("releases"),
    "spray repo"         at "http://repo.spray.io",
    "spray nightlies"    at "http://nightlies.spray.io",
    "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    "Typesafe Repo"      at "http://repo.typesafe.com/typesafe/releases/",
    "Websudos releases"  at "http://maven.websudos.co.uk/ext-release-local",
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )
}