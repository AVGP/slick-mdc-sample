lazy val root = (project in file(".")).
  settings(
    name := "slicmdc-sample",
    organization := "de.geekonaut",
    version := "1.0.0",
    scalaVersion := "2.11.4"
  )

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "Sonatype OSS Public" at "https://oss.sonatype.org/content/groups/public/"

libraryDependencies ++= Seq(
  "com.zaxxer"                          %  "HikariCP"                 % "2.4.6",
  "com.typesafe.slick"                  %% "slick"                    % "3.1.1",
  "org.xerial"                          %  "sqlite-jdbc"              % "3.7.2",
  "com.typesafe.scala-logging"          %% "scala-logging"            % "3.4.0",
  "ch.qos.logback"                      %  "logback-classic"          % "1.1.7",
  "org.slf4j"                           %  "log4j-over-slf4j"         % "1.7.21",
  "io.undertow"                         %  "undertow-core"            % "1.3.22.Final",
  "de.geekonaut"                        %% "slickmdc"                 % "1.0.0"
)
