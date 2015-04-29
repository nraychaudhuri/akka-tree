name := "akka-tree"

val commonSettings = Seq(
  organization := "nworks",
  version := "0.3",
  scalaVersion := "2.11.5",
  resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
  )
)

lazy val akkaTreeAspectJ = project.in(file("aspectj-plugin"))
                                .settings(commonSettings:_*)

lazy val akkaTreeClient = project.in(file("example"))
                                .settings(commonSettings:_*)
                                .dependsOn(akkaTreeAspectJ)


lazy val akkaTreeVisualizer = project.in(file("visualizer"))
                                .settings(commonSettings:_*).enablePlugins(PlayScala)



lazy val root = project.in(file(".")).aggregate(akkaTreeAspectJ, akkaTreeClient, akkaTreeVisualizer)