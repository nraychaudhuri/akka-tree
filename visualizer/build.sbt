name := """akkatree-visualizer"""

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

fork := true

javaOptions += "-Djava.net.preferIPv4Stack=true"
