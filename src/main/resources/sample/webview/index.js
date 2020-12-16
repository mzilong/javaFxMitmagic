const width = window.innerWidth;
const height = window.innerHeight;
const descriptionDiv = document.createElement("div");
descriptionDiv.id = "discription";
document.getElementById("container").appendChild(descriptionDiv);

/**
 * 数据
 */
const data = {
    id: "台区路由终端",
    img: "icon/ic_terminal.png",
    type: "image",
    size: 25,
    children: [
        {
            id: "分支1",
            img: "icon/ic_branch.png",
            type: "image",
            size: 25,
            children: [
                {
                    id: "A相(0)",
                    collapsed: true,
                    img: "icon/ic_list.png",
                    size: 25,
                    type: "image"
                },
                {
                    id: "B相(0)",
                    collapsed: true,
                    size: 25,
                    img: "icon/ic_list.png",
                    type: "image"
                },
                {
                    id: "C相(0)",
                    collapsed: true,
                    size: 25,
                    img: "icon/ic_list.png",
                    type: "image"
                }
            ]
        },
        {
            id: "分支2",
            size: 25,
            img: "icon/ic_branch.png",
            type: "image",
            children: [
                {
                    id: "A相(10)",
                    collapsed: true,
                    size: 25,
                    img: "icon/ic_list.png",
                    type: "image",
                    children: [
                        { id: "000000000001" },
                        { id: "000000000002" },
                        { id: "000000000003" },
                        { id: "000000000004" },
                        { id: "000000000005" },
                        { id: "000000000006" },
                        { id: "000000000007" },
                        { id: "000000000008" },
                        { id: "000000000009" },
                        { id: "000000000010" }
                    ]
                },
                {
                    id: "B相(5)",
                    collapsed: true,
                    size: 25,
                    img: "icon/ic_list.png",
                    type: "image",
                    children: [
                        { id: "000000000011" },
                        { id: "000000000012" },
                        { id: "000000000013" },
                        { id: "000000000014" },
                        { id: "000000000015" }
                    ]
                },
                {
                    id: "C相(3)",
                    collapsed: true,
                    size: 25,
                    img: "icon/ic_list.png",
                    type: "image",
                    children: [
                        { id: "000000000016" },
                        { id: "000000000017" },
                        { id: "000000000018" }
                    ]
                }
            ]
        },
        {
            id: "分支3",
            size: 25,
            img: "icon/ic_branch.png",
            type: "image",
            children: [
                {
                    id: "A相(5)",
                    collapsed: true,
                    img: "icon/ic_list.png",
                    size: 25,
                    type: "image",
                    children: [
                        { id: "000000000019" },
                        { id: "000000000020" },
                        { id: "000000000021" },
                        { id: "000000000022" },
                        { id: "000000000023" }
                    ]
                },
                {
                    id: "B相(3)",
                    collapsed: true,
                    img: "icon/ic_list.png",
                    size: 25,
                    type: "image",
                    children: [
                        { id: "000000000024" },
                        { id: "000000000025" },
                        { id: "000000000026" }
                    ]
                },
                {
                    id: "C相(2)",
                    collapsed: true,
                    img: "icon/ic_list.png",
                    size: 25,
                    type: "image",
                    children: [{ id: "000000000027" }, { id: "000000000028" }]
                }
            ]
        }
    ]
};


// 创建 G6 图实例
const graph = new G6.TreeGraph({
    //图的  DOM 容器，可以传入该 DOM 的 id 或者直接传入容器的 HTML 节点对象
    container: "container",
    //指定画布宽度，单位为 'px'
    width,
    //指定画布高度，单位为 'px'
    height,
    //是否开启画布自适应。开启后图自动适配画布大小
    fitView: true,
    fitCenter: true,
    //最小缩放比例
    minZoom: 0.2,
    //最大缩放比例
    maxZoom: 10,
    //向 graph 注册插件
    plugins: [],
    //是否启用 stack，即是否开启 redo & undo 功能，该配置项 V3.6 及以上版本支持
    enabledStack: true,
    //设置画布的模式
    modes: {
        default: [{}, "drag-canvas", "zoom-canvas"]
    },
    //默认状态下节点的配置
    defaultNode: {
        type: "custom",
        anchorPoints: [[0, 0.5], [1, 0.5]]
        // style: {
        //     fill: "white",
        //     stroke: "#5B8FF9"
        // }
    },
    //默认状态下边的配置
    defaultEdge: {
        type: "hvh",
        style: {
            stroke: "#A3B1BF"
        }
    },
    //布局配置项
    layout: {
        type: "compactBox",
        direction: "LR",
        getId: function getId(d) {
            return d.id;
        },
        getHeight: function getHeight() {
            return 16;
        },
        getWidth: function getWidth() {
            return 16;
        },
        getVGap: function getVGap() {
            return 10;
        },
        getHGap: function getHGap() {
            return 50;
        }
    }
});

//自定义节点
G6.registerNode(
    "custom",
    {
        // 响应状态变化
        setState(name, value, item) {
            const group = item.getContainer();
            const shape = group.get("children")[0];
            if (name === "running") {
                if (value) {
                    //节点动画，自定义节点图标时无效
                    shape.animate(
                        {
                            r: 20
                        },
                        {
                            repeat: true,
                            duration: 1000
                        }
                    );
                    shape.attr("fill", "red");
                    shape.attr("stroke", "red");
                } else {
                    //结束动画
                    shape.stopAnimate();
                    shape.attr("r", 10);
                    shape.attr("fill", "white");
                    shape.attr("stroke", "#5B8FF9");
                }
            }
        }
    },
    "circle"
);

function updateNode(node, state) {
    if (node != null) {
        graph.setItemState(node, "running", state);
        const pEdge = node._cfg.edges[0];
        const pNode = node._cfg.parent;
        if (typeof pEdge != "undefined" && typeof pNode != "undefined") {
            graph.setItemState(pEdge, "active", state);
            // graph.setItemState(pNode, "running", state);
            updateNode(pEdge._cfg.source, state);
        }
    }
}

var tempNode = null;
function addActionHandler(nodeAddress) {
    var list = graph.cfg.itemMap;
    var nodeArray = Object.keys(list).map(function (key) {
        return {
            id: key,
            value: list[key]
        };
    });
    for (var i = 0; i < nodeArray.length; i++) {
        var children = nodeArray[i].value._cfg.model.children;
        if (typeof children != "undefined") {
            for (var j = 0; j < children.length; j++) {
                if (children[j].id === nodeAddress) {
                    updateNode(tempNode, false);
                    tempNode = nodeArray[i].value;
                    updateNode(nodeArray[i].value, true);
                }
            }
        }
    }
}

// 鼠标移动到上面 running，移出结束
// graph.on("node:mouseenter", ev => {
//     const node = ev.item;
//     updateNode(tempNode, false);
//     tempNode = node;
//     updateNode(node, true);
// });

var tempNode = null;
graph.on("node:click", ev => {
    const node = ev.item;
    updateNode(tempNode, false);
    tempNode = node;
    updateNode(node, true);
});

// graph.on("node:mouseleave", ev => {
//     const node = ev.item;
//     graph.setItemState(node, "running", false);
//     updateNode(node, false)
// });

//自定义曲线
G6.registerEdge("hvh", {
    // 绘制
    draw(cfg, group) {
        const startPoint = cfg.startPoint;
        const endPoint = cfg.endPoint;
        const shape = group.addShape("path", {
            attrs: {
                stroke: "#333",
                lineWidth: 2,
                path: [
                    ["M", startPoint.x, startPoint.y],
                    ["L", endPoint.x / 3 + (2 / 3) * startPoint.x, startPoint.y],
                    ["L", endPoint.x / 3 + (2 / 3) * startPoint.x, endPoint.y],
                    ["L", endPoint.x, endPoint.y]
                ]
            },
            // must be assigned in G6 3.3 and later versions. it can be any value you want
            name: "path-shape"
        });
        return shape;
    },
    // 响应状态变化
    setState(name, value, item) {
        const group = item.getContainer();
        const shape = group.get("children")[0]; // 顺序根据 draw 时确定
        if (name === "active") {
            if (value) {
                // 曲线动画
                // const length = shape.getTotalLength();
                // shape.animate(
                //     ratio => {
                //         const startLen = ratio * length;
                //         const cfg = {
                //             lineDash: [startLen, length - startLen]
                //         };
                //         return cfg;
                //     },
                //     {
                //         repeat: true,
                //         duration: 2000
                //     }
                // );

                shape.attr("stroke", "red");
                shape.attr("lineWidth", 3);
            } else {
                // 结束动画
                // shape.stopAnimate();
                shape.attr("lineWidth", 2);
                shape.attr("stroke", "#333");
            }
        }
    }
});
graph.node(function(node) {
    return {
        label: node.id,
        labelCfg: {
            offset: 10,
            position: node.collapsed != true ? "top" : "right"
        }
    };
});
// 读取数据
graph.data(data);
// 渲染图
graph.render();
graph.fitView();
