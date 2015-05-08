package nworks.js

import org.scalajs.d3.Layout.{GraphLinkForce, GraphNodeForce}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportNamed, JSExportAll, JSExport}

//{"actorpath":"akka://organization/user/ceo","host":"Nilanjans-MacBook-Air.local","event":"started"}
@JSExport
case class AkkaTreeEvent(actorpath: String, host: String, event: String) {
  def isStarted(): Boolean = event == "started"
  def isTerminated(): Boolean = event == "terminated"
}

@JSExport
class AkkaTreeNode(val name: String,
                        val size: Int,
                        val id: Int,
                        val level: Int = 0,
                        val fixed: Boolean = false,
                        val x: Int = 0,
                        val y: Int = 0,
                        val children: js.Array[AkkaTreeNode] = new js.Array(),
                        val collapsedChildren: js.Array[AkkaTreeNode] = new js.Array(),
                        val isNodeCollapsed: Boolean = false,
                        val actorpath: String = "root")

