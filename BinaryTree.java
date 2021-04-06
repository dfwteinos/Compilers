/* Class containing left and right child of current node and key value */
class Node {

    String rule;
    String key = null;
    Node left, right;

    public Node(String item){
        rule = item;
        left = right = null;
    }
}

class BinaryTree {

    /* Root of Binary Tree */
    Node root; 

    /* Constructor */
    BinaryTree(String key)
    {
        root = new Node(key);
    }

    BinaryTree()
    {
        root = null;
    }

}