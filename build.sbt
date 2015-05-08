import com.typesafe.sbt.SbtAspectj._
import sbt.Project.projectToRef

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

val aspectjSettingsX = aspectjSettings ++ Seq(
  fork in run := true,
  javaOptions in run <++= AspectjKeys.weaverOptions in Aspectj
)


lazy val jsProjects = Seq(visualizerClient)

lazy val aspectjPlugin = project.in(file("aspectj-plugin"))
                                .settings(commonSettings:_*)

lazy val simpleClient = project.in(file("example"))
                                .settings(commonSettings:_*)
                                .settings(aspectjSettingsX:_*)
                                .dependsOn(aspectjPlugin)

lazy val clusterClient = project.in(file("cluster-example"))
                                .settings(commonSettings:_*)
                                .settings(aspectjSettingsX:_*)
                                .dependsOn(aspectjPlugin)


lazy val visualizer = project.in(file("visualizer"))
                                .settings(commonSettings:_*)
                                .settings(
                                    scalaJSProjects := jsProjects,
                                    pipelineStages := Seq(scalaJSProd))
                                .enablePlugins(PlayScala)
                                .aggregate(jsProjects.map(projectToRef): _*)

lazy val visualizerClient = project.in(file("visualizer-client"))
                                .settings(commonSettings:_*)
                                .enablePlugins(ScalaJSPlugin, ScalaJSPlay)


lazy val root = project.in(file(".")).aggregate(aspectjPlugin, simpleClient, clusterClient, visualizer, visualizerClient)