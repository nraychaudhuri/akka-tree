import com.typesafe.sbt.SbtAspectj._

name := "akkatree-example-client"

libraryDependencies ++= Seq(
    "com.typesafe.akka"   %% "akka-actor"             % "2.3.10"
  )

aspectjSettings

fork in run := true

javaOptions in run <++= AspectjKeys.weaverOptions in Aspectj