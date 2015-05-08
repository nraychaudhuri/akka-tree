name := "akkatree-visualizer-client"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.8.0",
  "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
  "com.lihaoyi" %%% "upickle" % "0.2.8"
)

scalaJSStage in Global := FastOptStage

skip in packageJSDependencies := true

persistLauncher := true

persistLauncher in Test := false

unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)