name := "Server"
version := "1.0"
scalaVersion := "2.12.6"

val logbackVersion = "1.1.7"
val akkaVersion = "2.5.21"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka"           %%  "akka-actor"        % akkaVersion,
  "com.typesafe.akka"           %%  "akka-remote"       % akkaVersion,
  "com.typesafe.akka"           %%  "akka-testkit"      % akkaVersion,
  "com.typesafe.scala-logging"  %%  "scala-logging"     % "3.9.2",
  "ch.qos.logback"              %   "logback-classic"   % "1.2.3",
  "org.scalatest"               %%  "scalatest"         % "3.0.0" % "test"
)
