name := "studiomanager"

version := "0.0.1"

lazy val `studiomanager` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Akka Snapshot Repository".at("https://repo.akka.io/snapshots/")

scalaVersion := "2.13.5"

libraryDependencies ++= Seq(ehcache, ws, playTest % Test, guice)

libraryDependencies ++= Seq(
  "com.typesafe.play"    %% "play-slick"            % "5.0.0",
  "org.postgresql"        % "postgresql"            % "42.3.1",
  "com.typesafe.play"    %% "play-slick-evolutions" % "5.0.0",
  "com.github.jwt-scala" %% "jwt-play-json"         % "9.0.3"
)
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0"   % Test,
  "org.scalatestplus"      %% "mockito-3-3"        % "3.2.2.0" % Test,
  "com.h2database"          % "h2"                 % "2.1.210" % Test
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0"   % Test,
  "org.scalatestplus"      %% "mockito-3-3"        % "3.2.2.0" % Test,
  "com.h2database"          % "h2"                 % "2.1.210" % Test
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
