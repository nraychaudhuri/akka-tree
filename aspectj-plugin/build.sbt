name := "akkatree-aspectj-plugin"

libraryDependencies ++= Seq(
    "com.typesafe.play"   %% "play-json"              % "2.3.0",
    "com.typesafe.akka"   %% "akka-actor"             % "2.3.10" % "provided",
    "org.aspectj"         %  "aspectjweaver"          % "1.8.4",
    "org.aspectj"         %  "aspectjrt"              % "1.8.4"
  )

javacOptions += "-g:vars"

publishArtifact in (Compile, packageDoc) := false

