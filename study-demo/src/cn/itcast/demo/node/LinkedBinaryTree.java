package cn.itcast.demo.node;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class LinkedBinaryTree implements BinaryTree {
    private Node root;//根节点

    public LinkedBinaryTree(Node root) {
        this.root = root;
    }
    public LinkedBinaryTree(){};
    @Override
    public boolean isEmpty() {
        return root==null;
    }

    @Override
    public int size() {
        return this.size(root);
    }
    private int size(Node node){
        if (node==null){
            return 0;
        }
        //左节点的节点数
        int lsize=this.size(node.lefrNode);
        //右节点的节点数
        int rsize=this.size(node.rightNode);
        return lsize+rsize+1;
    }
    @Override
    public int getHeight() {
        return this.getHeight(root);
    }
    private int getHeight(Node node){
        if (node==null){
            return 0;
        }
        //左子树高度
        int nl=this.getHeight(node.lefrNode);
        //柚子树高度
        int nr=this.getHeight(node.rightNode);
        //返回大的加一
        return nl>nr?nl+1:nr+1;
    }

    /**
     * 查找某个元素
     * @param value
     * @return
     */
    @Override
    public Node findKey(int value) {
        return this.findKey(value,root);
    }
    private Node findKey(Object value,Node node){
        if (node==null){
            return null;
        }else if (node!=null&&node.data.equals(value)){
            return node;
        }else {
            Node node1=this.findKey(value,node.lefrNode);
            Node node2=this.findKey(value,node.rightNode);
            if (node1!=null&&node1.data.equals(value)){
                return node1;
            }
            if (node2!=null&&node2.data.equals(value)){
                return node2;
            }
        }
        return null;
    }
    @Override
    public void preOrderTraverse() {
        //输出根节点的值
        if (root!=null){
            System.out.print(root.data+"  ");
        }
        //左子树进行先续遍历
        if (root!=null){
            LinkedBinaryTree leftBinaryTree=new LinkedBinaryTree(root.lefrNode);
            leftBinaryTree.preOrderTraverse();
        }
        //又字数进行先续遍历
        if (root!=null){
            LinkedBinaryTree rightBinaryTree=new LinkedBinaryTree(root.rightNode);
            rightBinaryTree.preOrderTraverse();
        }
    }

    @Override
    public void inOrderTraverse() {
        if (root!=null){
            this.inOrderTraverse(root);
        }
    }
    private void inOrderTraverse(Node root){
        if (root!=null){
            //遍历左子树
            this.inOrderTraverse(root.lefrNode);
            //输出根植
            System.out.println(root.data+"  ");
            //遍历右子树
            this.inOrderTraverse(root.rightNode);
        }
    }
    @Override
    public void postOrderTraverse() {
        //后续遍历
        System.out.println("后续遍历");
        this.postOrderTraverse(root);
    }
    @Override
    public void postOrderTraverse(Node node) {
        if (node!=null){
            //遍历左子树
            this.postOrderTraverse(node.lefrNode);
            //遍历右子树
            this.postOrderTraverse(node.rightNode);
            //输出根
            System.out.print(node.data+"  ");
        }
    }

    /**
     * 非递归中序遍历
     */
    @Override
    public void inOrderByStack() {
        this.inOrderByStack(root);
    }
    private void inOrderByStack(Node node){
        if (node!=null){
            Node current=node;
            Deque<Node> deque=new LinkedList<>();
            while (current!=null||!deque.isEmpty()){
                while (current!=null){
                    deque.push(current);
                    current=current.lefrNode;
                }
                if (!deque.isEmpty()){
                    current=deque.pop();
                    System.out.print(current.data);
                    current=current.rightNode;
                }
            }
        }
    }
    @Override
    public void postOrderByStack() {

    }

    @Override
    public void preOrderByStack() {

    }

    /**
     * 二叉树的层次遍历
     */
    @Override
    public void levelByStack() {
        this.levelByStack(root);
    }
    private void levelByStack(Node node){
        if (node!=null){
            Queue<Node> queue=new LinkedList<>();
            queue.add(node);
            while (queue.size()!=0){
                int len=queue.size();
                for (int i = 0; i < len; i++) {
                    Node temp=queue.poll();
                    System.out.print(temp.data);
                    if (temp.lefrNode!=null){
                        queue.add(temp.lefrNode);
                    }
                    if (temp.rightNode!=null){
                        queue.add(temp.rightNode);
                    }
                }
            }
        }
        return;
    }
}
