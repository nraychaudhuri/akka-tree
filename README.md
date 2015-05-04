akka-tree
=========

Akka visualizer - Tool that allows you to view the actor tree as it evolves in your application

Running the visualizer
-----------
- Start the visualizer project
  ```export SBT_OPTS="-Djava.net.preferIPv4Stack=true"``` (Required for UDP multicast)
  
  ```sbt "project visualizer" run```
  
- Point your browser to ```http://localhost:9000```


Test with sample client
-----------

- Start the example project
  ```sbt "project simpleClient" run```
  
  
Test with sample Akka cluster client (3 node cluster)
--------

This sample uses Akka cluster sharding component. Each node starts Akka clustering sharding component with few entry actors. Take a look at the ```build.sbt``` under ```cluster-example``` project for details

- Start node1
  ```sbt "project clusterClient" node1```

- Start node2
  ```sbt "project clusterClient" node1```

- Start node3
  ```sbt "project clusterClient" node1```

  