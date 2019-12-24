package cn.itcast.demo.node;

public interface BinaryTree {
    /**
     *是否为空
     */
    public boolean isEmpty();
    /**
     * 大小
     */
    public int size();
    /**
     * 二叉树的高度
     */
    public int getHeight();
    /**
     * 查询指定节点
     */
    public Node findKey(int value);
    /**
     * 前序遍历
     */
    public void preOrderTraverse();
    /**
     * 中序遍历
     */
    public void  inOrderTraverse();
    /**
     * 后续遍历
     */
    public void postOrderTraverse();
    /**
     *
     */
    public  void postOrderTraverse(Node node);
    /**
     * 非递归操作
     */
    public void inOrderByStack();
    /**
     *
     */
    public  void  postOrderByStack();
    /**
     *
     */
    public void preOrderByStack();
    /**
     * 按照层次遍历
     */
    public void levelByStack();
}
