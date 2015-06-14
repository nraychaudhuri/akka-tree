$(document).ready(function() {
  var nodes, edges, network;

  init()

  var eventSource = new EventSource("/events");
  eventSource.onmessage = function(event) {
    handle_event(JSON.parse(event.data))
  };
  eventSource.onerror = function(alert) {
    console.log("alert: ", alert)
  }

  function check_node(path) {
    if (!nodes.get(path)) {
      nodes.add({
        id: path,
        value: 0,
        label: path.substring(path.lastIndexOf("/") + 1),
        title: path
      })
    }
    return nodes.get(path)
  }

  function check_edge(from, to) {
    var id = from + "-" + to
    if (!edges.get(id)) {
      edges.add({
        id: id,
        value: 0,
        from: from,
        to: to
      })
    }
    return edges.get(id)
  }

  function handle_event(message) {
    if (message.event.type === "message") {
      console.log(message)
      var fromNode = check_node(message.from)
      var toNode = check_node(message.to)
      var edgeNode = check_edge(message.from, message.to)
      nodes.update({
        id: fromNode.id,
        value: fromNode.value + 1
      })
      nodes.update({
        id: toNode.id,
        value: toNode.value + 1
      })
      edges.update({
        id: edgeNode.id,
        value: edgeNode.value + 1
      })
    } else if (message.event.type === "started") {
      console.log("start")
    } else if (message.event.type === "terminated") {
      console.log("end")
    } else {
      console.log("Error Unhandeled", message)
    }
  }

  function init() {
    // create an array with nodes
    nodes = new vis.DataSet();
    // create an array with edges
    edges = new vis.DataSet();

    // create a network
    var container = document.getElementById('canvas');
    var data = {
      nodes: nodes,
      edges: edges
    };
    var options = {
      configure: {
        enabled: true,
        container: document.getElementById("dialog")
      },
      edges: {
        arrows: {
          to: {
            enabled: true,
            scaleFactor: 0.25
          }
        },
        scaling: {
          min: 1,
          max: 14
        },
        smooth: {
          enabled: true,
          type: "curvedCW"
        }
      },
      nodes: {
        shape: "dot"
      }
    };
    network = new vis.Network(container, data, options);

  }
})