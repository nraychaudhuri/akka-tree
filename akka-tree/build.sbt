name := "akka-tree"

organization := "nworks"

version := "0.2.1"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
    "com.typesafe.play"   %% "play-json"              % "2.3.0",
    "com.typesafe.akka"   %% "akka-actor"             % "2.3.10" % "provided",
    "org.aspectj"         %  "aspectjweaver"          % "1.8.4",
    "org.aspectj"         %  "aspectjrt"              % "1.8.4"
  )

javacOptions += "-g:vars"

publishArtifact in (Compile, packageDoc) := false

// The Typesafe repository
resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)
