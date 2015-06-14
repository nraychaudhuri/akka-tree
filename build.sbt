import com.typesafe.sbt.SbtAspectj._

name := "akka-tree"

val commonSettings = Seq(
  organization := "nworks",
  version := "0.3",
  scalaVersion := "2.11.5",
  scalacOptions += "-target:jvm-1.7",
  resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
  )
)

val aspectjSettingsX = aspectjSettings ++ Seq(
  fork in run := true,
  javaOptions in run <++= AspectjKeys.weaverOptions in Aspectj
)


lazy val aspectjPlugin = project.in(file("aspectj-plugin"))
                                .settings(commonSettings:_*)

lazy val simpleClient = project.in(file("example"))
                                .settings(commonSettings:_*)
                                .settings(aspectjSettingsX:_*)
                                .dependsOn(aspectjPlugin)

lazy val streamClient = project.in(file("example-stream"))
                                .settings(commonSettings:_*)
                                .settings(aspectjSettingsX:_*)
                                .dependsOn(aspectjPlugin)

lazy val clusterClient = project.in(file("cluster-example"))
                                .settings(commonSettings:_*)
                                .settings(aspectjSettingsX:_*)
                                .dependsOn(aspectjPlugin)


lazy val visualizer = project.in(file("visualizer"))
                                .settings(commonSettings:_*).enablePlugins(PlayScala)


lazy val root = project.in(file(".")).aggregate(aspectjPlugin, simpleClient, clusterClient, visualizer, streamClient)