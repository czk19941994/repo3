package cn.itcast.demo.node;

public class Node {
     Object data;
     Node lefrNode;
     Node rightNode;

    public Node(Object data, Node lefrNode, Node rightNode) {
        super();
        this.data = data;
        this.lefrNode = lefrNode;
        this.rightNode = rightNode;
    }
    public Node(){};
    public Node(Object data){
        super();
        this.data=data;
    }

    @Override
    public String toString() {
        return "Node{" +
                "data=" + data +
                ", lefrNode=" + lefrNode +
                ", rightNode=" + rightNode +
                '}';
    }
}
