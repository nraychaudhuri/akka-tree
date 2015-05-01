akka-tree
=========

Akka visualizer - Tool that allows you to view the actor tree as it evolves in your application


How to run
-----------

- Start the example project
  ```sbt "project akkaTreeClient" run```
  
- Start the visualizer project
  ```export SBT_OPTS="-Djava.net.preferIPv4Stack=true"``` (Required for UDP multicast)
  
  ```sbt "project akkaTreeVisualizer" run```
  
- Point your browser to ```http://localhost:9000```
  
  
TODO
-----

- Start in Akka cluster mode
  