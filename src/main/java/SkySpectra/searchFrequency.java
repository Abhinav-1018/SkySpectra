package SkySpectra;

public class searchFrequency {

    public static class TreeNode {
        String word;
        int frequency;
        TreeNode left, right;

        public TreeNode() {
            this.word = null;
            this.frequency = 0;
            this.left = this.right = null;
        }

        public void insert(String newWord) {
            if (word == null) {
                // If the node is empty, set the word and initialize frequency
                this.word = newWord;
                this.frequency = 1;
            } else {
                int compareResult = newWord.compareTo(this.word);
                if (compareResult < 0) {
                    if (this.left == null) {
                        this.left = new TreeNode();
                    }
                    this.left.insert(newWord);
                } else if (compareResult > 0) {
                    if (this.right == null) {
                        this.right = new TreeNode();
                    }
                    this.right.insert(newWord);
                } else {
                    // Word already exists in the tree, increase frequency
                    this.frequency++;
                }
            }
        }
    }

    private static void inOrderTraversal(TreeNode root) {
        if (root != null) {
            inOrderTraversal(root.left);
            System.out.println(root.word + ": " + root.frequency);
            inOrderTraversal(root.right);
        }
    }

    public static void SearchFrequency(TreeNode root) {
        if (root == null || root.word == null) {
            System.out.println("BST is empty.");
            return;
        }

        // Perform in-order traversal to print word frequencies
        System.out.println("Word Frequencies (In-Order Traversal): ");
        inOrderTraversal(root);
    }


}
