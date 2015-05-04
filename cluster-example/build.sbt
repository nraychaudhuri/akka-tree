name := "akkatree-example-cluster-client"

val akkaVersion = "2.3.10"

libraryDependencies ++= Seq(
  "com.typesafe.akka"   %% "akka-actor"   % akkaVersion,
  "com.typesafe.akka"   %% "akka-actor"   % akkaVersion,
  "com.typesafe.akka"   %% "akka-contrib" % akkaVersion,
  "com.typesafe.akka"   %% "akka-cluster" % akkaVersion
)

addCommandAlias("node1", "runMain Main -Dakka.remote.netty.tcp.port=2551 -Dakkatree.hostname=node1 name=raju count=5")

addCommandAlias("node2", "runMain Main -Dakka.remote.netty.tcp.port=2552 -Dakkatree.hostname=node2 name=mannu count=5")

addCommandAlias("node3", "runMain Main -Dakka.remote.netty.tcp.port=2553 -Dakkatree.hostname=node3 name=nima count=5")
