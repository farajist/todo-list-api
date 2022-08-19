name := """todo-list-api"""
organization := "dojo.todo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SwaggerPlugin)

scalaVersion := "2.13.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.webjars" % "swagger-ui" % "3.43.0"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.h2database" % "h2" % "1.4.200",
  "com.typesafe.play" %% "play-slick" % "5.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.2"
)

libraryDependencies ++= Seq(evolutions)
swaggerDomainNameSpaces := Seq("models")

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "dojo.todo.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "dojo.todo.binders._"
