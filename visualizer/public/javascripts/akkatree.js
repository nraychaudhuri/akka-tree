$(document).ready(function(){
    function findElementInArray(array, elem) {
        var result = $.grep(array, function(e) { return e.name == elem; })
        if(result.length == 1) {
            return result[0]
        } else {
            return undefined
        }
    }

    function children(node) {
        if(node.isNodeCollapsed)
            return node.collapsed_children;
        else
            return node.children;
    }

    function insert(path, parent, actorpath, level) {
        if (path.length == 0) { return; }
        else {
            var elem = path.shift();
            var node;
            if (children(parent)) {
                node = findElementInArray(children(parent), elem);
            }
            if (!node) {
                node = {
                    "name" : elem,
                    "size": 1,
                    "id": id++,
                    "level" : level,
                    "children" : [],
                    "collapsed_children" : [],
                    "isNodeCollapsed" : false
                };
                children(parent).push(node);
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
            if (parent_ && children(parent_)) {
                parent_ = findElementInArray(children(parent_), elem);
            }
        }

        if (parent_ && children(parent_)) {
            var elem = findElementInArray(children(parent_), path[0]);
            if (elem) {
                var index = children(parent_).indexOf(elem);
                if (index > -1) {
                    children(parent_).splice(index, 1);
                }
            }
        }
    }

    function akkatree_onmessage(msg) {
        showDialogInfo(msg)
        var path = msg.actorpath.replace(/akka:\/\//, msg.host + "/").split("/");
        if (msg.event.type == "started") {
            insert(path, root, msg.actorpath, 0, root.isNodeCollapsed);
        }

        if (msg.event.type == "terminated") {
            remove(path, root);
        }
        update();
    }

    function showDialogInfo(msg) {
        $("#dialog-path").html("<h4>Path</h4>" + msg.actorpath);
        $("#dialog-host").html("<h4>Host</h4>" + msg.host);
        $("#dialog-event").html("<h4>Event</h4>" + msg.event.type);
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
        link = vis.selectAll(".link")
                 .data(links, function(d) { return d.target.id; });

        // Enter any new links.
        link.enter().insert("line", ".node")
            .attr("class", "link")
            .attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

        // Exit any old links.
        link.exit().remove();

        // Update the nodes…
        node = vis.selectAll(".node")
            .data(nodes, function(d) { return d.id; })

        // Exit any old nodes.
        node.exit().remove();

        var nodeEnter = node.enter().append("g")
            .attr("class", "node")
            .on("click", click)
            .call(force.drag);

        nodeEnter.append("circle")
            .attr("r", function(d) { return Math.sqrt((d.size + 1) * 100); })
            .on('contextmenu', d3.contextMenu(menu));

        nodeEnter.append("text")
            .attr("dy", "-1.75em")
            .text(function(d) { return d.name; });

        node.select("circle")
            .style("fill", color);
    }

    function tick() {
        link.attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

        node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
    }

    function color(d) {
        if(d.isNodeCollapsed) { return "#ff0000"; }

        var colors = ["#1d4d70", "#3182bd", "#c6dbef", "#ffffff"];
        return colors[d.level % colors.length];
    }

    //used to pin/unpin nodes. This allows to fix part of the tree from moving
    function click(d) {
        if (d3.event.defaultPrevented) return; // ignore drag
        d.fixed = !d.fixed
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


    function collapse(d) {
        if(d.isNodeCollapsed) { return;} //already collapsed

        d.isNodeCollapsed = true;
        d.collapsed_children = d.children;
        d.children = [];
        update()
    }

    function expand(d) {
        if(!d.isNodeCollapsed) { return; }

        d.isNodeCollapsed = false;
        d.children = d.collapsed_children;
        d.collapsed_children = [];
        update()
    }

    function createRoot(w, h) {
        var root = {
            "name": "akka-tree",
            "size": 0,
            "id" : 0,
            "children" : [],
            "collapsed_children" : [],
            "isNodeCollapsed" : false,
            "actorpath" : "Root"
        };

        root.fixed = true;
        root.x = w / 2;
        root.y = h / 2;
        return root
    }

    var w = window.innerWidth;
    var h = window.innerHeight;

    var menu = [
        {
            title: 'Collapse',
            action: function(elm, d, i) {
                console.log("collapse " + d.name);
                collapse(d)
            }
        },
        {
            title: 'Expand',
            action: function(elm, d, i) {
                console.log("Expand " + d.name);
                expand(d)
            }
        }
    ]

    var node, link;
    var id = 1;
    var root = createRoot(w, h)

    var force = d3.layout.force()
        .on("tick", tick)
        .charge(function(d) { return -500; })
        .linkDistance(function(d) { return 50; })
        .size([w - 100, h - 100]);

    var vis = d3.select("#canvas").append("svg:svg")
        .attr("width", w)
        .attr("height", h);

    var eventSource = new EventSource("/events");
    eventSource.onmessage = function(event) {
        //console.log("data: ", event.data)
        akkatree_onmessage(JSON.parse(event.data))
    };

    eventSource.onerror = function(alert) {
        console.log("alert: ", alert)
    }
})
