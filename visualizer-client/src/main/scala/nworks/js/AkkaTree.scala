package nworks.js

import org.scalajs.d3.D3.Selection
import org.scalajs.d3.Layout.{ForceLayout, GraphNodeForce, GraphLinkForce}
import org.scalajs.dom.raw.{EventSource, MessageEvent, Event}

import scala.scalajs.js
import org.scalajs.{d3, dom}
import org.scalajs.jquery.jQuery

import scala.scalajs.js.{Function2, JSStringOps, JSApp, RegExp}
import scala.scalajs.js.annotation.JSExport
import upickle._
import JSStringOps._

object AkkaTree extends JSApp {

  val width = dom.innerWidth;
  val height = dom.innerHeight;

  var node: d3.D3.UpdateSelection = _
  var link: d3.D3.UpdateSelection = _
  var id = 1

  val root = createRoot(width, height)

  val force: ForceLayout = d3.d3.layout.force()
                  .on("tick", tick _)
                  .charge(-500)
                  .linkDistance(50)
                  .size(js.Array(width - 100.0, height - 100.0))

  val vis: Selection = d3.d3.select("#canvas").append(jQuery("#canvas"), "svg:svg")
    .attr("width", width)
    .attr("height", height)

  def tick(): Unit = {
    link.attr("x1", (d: GraphLinkForce, index: Double) => d.source.x)
        .attr("y1", (d: GraphLinkForce, index: Double) => d.source.y)
        .attr("x2", (d: GraphLinkForce, index: Double) => d.target.x)
        .attr("y2", (d: GraphLinkForce, index: Double) => d.target.y)

    node.attr("transform", (d: GraphNodeForce, index: Double) => "translate(" + d.x + "," + d.y + ")")
  }

  def makeNode(node: AkkaTreeNode): GraphNodeForce = {
    val g = new GraphNodeForce()
    g.index = node.id
    g.fixed = node.fixed
    g.x = node.x
    g.y = node.y
    g
  }
  def flattenNodes(node: AkkaTreeNode, accum: js.Array[GraphNodeForce] = new js.Array[GraphNodeForce]()): js.Array[GraphNodeForce] = {

    for(child <- node.children) {
      flattenNodes(child, accum :+ makeNode(child))
    }
    accum :+ makeNode(node)
  }

  def makeLinks(node: AkkaTreeNode, accum: js.Array[GraphLinkForce] = new js.Array[GraphLinkForce]()): js.Array[GraphLinkForce] = {
    for(child <- node.children) {
      val link = new GraphLinkForce()
      link.source = makeNode(node)
      link.target = makeNode(child)
      makeLinks(child, accum :+ link)
    }
    accum
  }



  def update(force: ForceLayout): Unit = {
    val nodes: js.Array[GraphNodeForce] = flattenNodes(root)
    val links = makeLinks(root)
    force
      .nodes(nodes)
      .links(links)
      .start()

      link = vis.selectAll(".link")
                .data(links){ (a: GraphLinkForce, index: Double) => a.target.index }

      link.enter().insert("line", ".node")
                .attr("x1", (d: GraphLinkForce, index: Double) => d.source.x)
                .attr("y1", (d: GraphLinkForce, index: Double) => d.source.y)
                .attr("x2", (d: GraphLinkForce, index: Double) => d.target.x)
                .attr("y2", (d: GraphLinkForce, index: Double) => d.target.y)

      link.exit().remove()

      node = vis.selectAll(".node")
                 .data(nodes) {(a: GraphNodeForce, index: Double) => a.index }

      // Exit any old nodes.
      node.exit().remove();

      val nodeEnter = node.enter().append("g")
        .attr("class", "node")
//        .on("click", click)
        .call(() => force.drag);

    nodeEnter.append(nodeEnter, "circle")
      .attr("r", 10)
      //.on('contextmenu', d3.contextMenu(menu));

    nodeEnter.append(nodeEnter, "text")
      .attr("dy", "-1.75em")
      .text((d: AkkaTreeNode, index: Double) => d.name)

    node.select("circle")
      .style[AkkaTreeNode]("fill", (d: AkkaTreeNode, index: Double) => color(d));

  }

  def color(d: AkkaTreeNode): String = {
    if(d.isNodeCollapsed) { return "#ff0000"; }

    val colors = Seq("#1d4d70", "#3182bd", "#c6dbef", "#ffffff");
    return colors(d.level.intValue() % colors.length);
  }

  def insert(path: Array[String], parent: AkkaTreeNode, actorpath: String, level: Int): Unit = {
    if(path.length != 0) {
      var elem = path.head;
      val existingNode = children(parent).filter(_.name == elem).headOption
      if(existingNode.isEmpty) {
        id = id + 1
        val node = new AkkaTreeNode(name = elem, size = 1, id = id, level = level)
        children(parent) += node
        insert(path.tail, node, actorpath, level + 1)
      } else {
        insert(path.tail, existingNode.get, actorpath, level + 1)
      }
    }
  }

  //uses isNodeCollapsed property to return appropriate array
  def children(node: AkkaTreeNode): js.Array[AkkaTreeNode] = {
    if(node.isNodeCollapsed)
      return node.collapsedChildren;
    else
      return node.children;
  }

  def remove(path: Array[String], parent: AkkaTreeNode): Unit = {
     def findParent(path: Array[String], node: Option[AkkaTreeNode]): Option[AkkaTreeNode] = {
       if(path.length == 1) node
       else {
         val nextNode = node.flatMap(n => children(n).filter(_.name == path.head).headOption)
         findParent(path.tail, nextNode)
       }
     }

    val immediateParent = findParent(path, Some(parent))
    immediateParent.foreach { p =>
      val elemToRemove = children(p).filter(_.name == path.last).headOption
      elemToRemove.foreach(children(p) -= _)
    }
  }

  def onMessage(msg: AkkaTreeEvent) = {
    showDialogInfo(msg)

    val regex = new RegExp("""akka://[^\/]+""")
    val path: Array[String] = msg.actorpath.jsReplace(regex, msg.host).split("/")
    if(msg.isStarted()) {
      println("found started event ")
      insert(path, root, msg.actorpath, 0);
    }
    if(msg.isTerminated()) {
      println("found terminated event ")
      if(path(path.length - 1) == "user") { //user actor is terminated, kill the host node
        remove(path.init, root)//removes the last element to get to the host path
      } else {
        remove(path, root)
      }
    }

    update(force)
  }

  @JSExport
  override def main(): Unit = {
    val es = new EventSource("/events")
    es.onmessage = {(event: MessageEvent) =>
      val json = event.data.toString
      val akkaTreeEvent: AkkaTreeEvent = read[AkkaTreeEvent](json)
      onMessage(akkaTreeEvent)
    }
    es.onerror = { (error: Event) =>
      println("alert: " + error)
    }
  }

  def createRoot(width: Int, height: Int): AkkaTreeNode = {
    val root = new AkkaTreeNode(name = "akka-tree", size = 0, id = 0, fixed = true, x = width / 2, y = height / 2 )
    root
  }

  def showDialogInfo(msg: AkkaTreeEvent) = {
    jQuery("#dialog-path").html("<h4>Path</h4>" + msg.actorpath);
    jQuery("#dialog-host").html("<h4>Host</h4>" + msg.host);
    jQuery("#dialog-event").html("<h4>Event</h4>" + msg.event);
  }

}
