$(document).ready(function(){
    function findElementInArray(array, elem) {
        var result = $.grep(array, function(e) { return e.name == elem; })
        if(result.length == 1) {
            return result[0]
        } else {
            return undefined
        }
    }

    function insert(path, parent, actorpath, level) {
        if (path.length == 0) { return; }
        else {
            var elem = path.shift();
            var node;
            if (parent.children) {
                node = findElementInArray(parent.children, elem);
            }
            if (!node) {
                node = {"name" : elem, "size": 1, "id": id++, "level" : level};
                if (!parent.children) {
                    parent.children = [];
                }
                parent.children.push(node);
            }
            if (path.length == 0) {
                node.actorpath = actorpath;
            }
            insert(path, node, actorpath, level + 1);
        }
    }

    function remove(path, parent){

        var parent_ = parent;

        while (path.length > 1) {
            var elem = path.shift();
            if (parent_ && parent_.children) {
                parent_ = findElementInArray(parent_.children, elem);
            }
        }

        if (parent_ && parent_.children) {
            var elem = findElementInArray(parent_.children, path[0]);
            if (elem) {
                var index = parent_.children.indexOf(elem);
                if (index > -1) {
                    parent_.children.splice(index, 1);
                }
            }
        }
    }

    function akkatree_onmessage(msg) {
        document.getElementById("log").innerHTML = JSON.stringify(msg);

        var path = msg.actorpath.replace(/akka:\/\//, msg.host + "/").split("/");
        if (msg.event.type == "started") {
            insert(path, root, msg.actorpath, 0);
        } if (msg.event.type == "terminated") {
            remove(path, root);
        }
        update();
    }


    function update() {
        var nodes = flatten(root),
            links = d3.layout.tree().links(nodes);

        // Restart the force layout.
        force
            .nodes(nodes)
            .links(links)
            .start();

        // Update the links…
        link = vis.selectAll("line.link")
            .data(links, function(d) { return d.target.id; });

        // Enter any new links.
        link.enter().insert("svg:line", ".node")
            .attr("class", "link")
            .attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

        // Exit any old links.
        link.exit().remove();

        // Update the nodes…
        node = vis.selectAll("circle.node")
            .data(nodes, function(d) { return d.id; })
            .style("fill", color);

        // Enter any new nodes.
        node.enter().append("svg:circle")
            .attr("class", "node")
            .attr("cx", function(d) { return d.x; })
            .attr("cy", function(d) { return d.y; })
            .attr("r", function(d) { return Math.sqrt((d.size + 1) * 100); })
            .style("fill", color)
            .on("click", click)
            .call(force.drag);

        // Exit any old nodes.
        node.exit().remove();

        $('svg circle').tipsy({
            gravity: 'w',
            html: true,
            title: function() {
                var d = this.__data__;
                return 'Path: ' + d.name + '';
            }
        });

    }

    function tick() {
        link.attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

        node.attr("cx", function(d) { return d.x; })
            .attr("cy", function(d) { return d.y; });
    }

    function color(d) {
        var colors = ["#1d4d70", "#3182bd", "#c6dbef", "#ffffff"];
        return d.name == "user" ? "#ff0000" : colors[d.level % colors.length];
    }

    //used to pin/unpin nodes. This allows to fix part of the tree from moving
    function click(d) {
        if(!d.fixed){
            d.fixed = true
        } else {
            d.fixed = false
        }
    }

// Returns a list of all nodes under the root.
    function flatten(root) {
        var nodes = [], i = 0;

        function recurse(node) {
            if (node.children) node.children.reduce(function(p, v) { return p + recurse(v); }, 0);
            nodes.push(node);
            return node.size;
        }

        recurse(root);
        return nodes;
    }

    function createRoot(w, h) {
        var root = {"name": "akka-tree", "size": 0, "id" : 0, "children" : [], "actorpath" : "Root" };
        root.fixed = true;
        root.x = w / 2;
        root.y = h / 2 - 80;
        return root
    }

    var w = 1200, h = 1200;

    var node, link;
    var id = 1;
    var root = createRoot(w, h)

    var force = d3.layout.force()
        .on("tick", tick)
        .charge(function(d) { return -500; })
        .linkDistance(function(d) { return 50; })
        .size([w, h - 160]);

    var vis = d3.select("#canvas").append("svg:svg")
        .attr("width", w)
        .attr("height", h);

    var eventSource = new EventSource("/events");
    eventSource.onmessage = function(event) {
        console.log("data: ", event.data)
        akkatree_onmessage(JSON.parse(event.data))
    };

    eventSource.onerror = function(alert) {
        console.log("alert: ", alert)
    }
})
