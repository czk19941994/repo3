package cn.itcast.demo.node;

public class TestTree {
    public static void main(String[] args) {
        //创建二叉树
        Node node5=new Node(5,null,null);
        Node node4=new Node(4,null,node5);
        Node node7=new Node(7,null,null);
        Node node6=new Node(6,null,node7);
        Node node3=new Node(3,null,null);
        Node node2=new Node(2,node3,node6);
        Node node1=new Node(1,node4,node2);
        LinkedBinaryTree linkedBinaryTree=new LinkedBinaryTree(node1);
        //判断二叉树是否为空
        linkedBinaryTree.isEmpty();//根节点为空则为true
        //先续遍历
        linkedBinaryTree.preOrderTraverse();
        //中序遍历
        linkedBinaryTree.inOrderTraverse();
        //后续遍历
        System.out.println("后续遍历");
        linkedBinaryTree.postOrderTraverse();
        //中序遍历非递归  .借助栈
        //按照层次遍历    借助队列
        //二叉树的查找
        //二叉树的高度
        System.out.println("二叉树的高度是"+linkedBinaryTree.getHeight());
        //二叉树的节点数量
        System.out.println("二叉树的节点数是"+linkedBinaryTree.size());
        //查找某个节点
        System.out.println();
        System.out.println(linkedBinaryTree.findKey(2));
        //借助队列进行层次遍历
        System.out.println();
        linkedBinaryTree.levelByStack();
        //借助栈进行中序遍历
        System.out.println();
        linkedBinaryTree.inOrderByStack();
    }
}
