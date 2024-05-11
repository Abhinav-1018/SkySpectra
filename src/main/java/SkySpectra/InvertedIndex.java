package SkySpectra;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class InvertedIndex {
//
//    // Define a Trie data structure to store the keys (words) of the inverted index
//    private static class TrieNode {
//        Map<Character, TrieNode> children = new HashMap<>();
//        List<Integer> positions = new ArrayList<>();
//    }
//
//    private TrieNode root = new TrieNode(); // root of the Trie
//
//    private int[][] occurrences; // 2D array to store the list of occurrences of each word
//
//    public void buildIndex(String folderPath) {
//        File folder = new File(folderPath);
//        if (!folder.isDirectory()) {
//            throw new IllegalArgumentException("Invalid folder path: " + folderPath);
//        }
//
//        // Initialize the occurrences array with the number of files in the folder
//        occurrences = new int[folder.listFiles().length][];
//
//        int pageIndex = 0; // initializing page index
//        for (File file : folder.listFiles()) {
//            if (file.isFile() && file.getName().endsWith(".txt")) {
//                // Read the file and extract the words
//                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//                    String line;
//                    int position = 0;
//                    while ((line = br.readLine()) != null) {
//                        String[] words = line.split("\\s+");
//                        for (String word : words) {
//                            // Add the position of the word in the file to the Trie node
//                            addWord(root, word.toLowerCase(), pageIndex, position++);
//                        }
//                    }
//                } catch (IOException e) {
//                    System.err.println("Error reading file: " + file.getAbsolutePath());
//                    e.printStackTrace();
//                }
//                pageIndex++; // increment the page index for the next file
//            }
//        }
//    }
//
//    // Method to add a word to the Trie node
//    private void addWord(TrieNode node, String word, int pageIndex, int position) {
//        for (char c : word.toCharArray()) {
//            node = node.children.computeIfAbsent(c, k -> new TrieNode());
//        }
//        node.positions.add(pageIndex * 10000 + position); // encode page index and position as a single integer
//    }
//
//    // Method to search for a keyword in the inverted index and display the list of occurrences
//    public void searchKeyword(String keyword) {
//        TrieNode node = root;
//        for (char c : keyword.toLowerCase().toCharArray()) {
//            node = node.children.get(c);
//            if (node == null) {
//                System.out.println("Keyword not found: " + keyword);
//                return;
//            }
//        }
//        System.out.println("Occurrences of keyword: " + keyword);
//        for (int p : node.positions) {
//            int pageIndex = p / 10000;
//            int position = p % 10000;
//            System.out.println("File: " + pageIndex + ", Position: " + position);
//        }
//    }
//}
//
//

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class InvertedIndex {

    private static class AVLNode {
        AVLNode left, right;
        int height;
        String key;
        AVLIndex index;
        String fileName;

        AVLNode(String key, int pageIndex, int position,String fileName) {
            this.key = key;
            this.height = 1;
            this.index = new AVLIndex(pageIndex, position);
            this.fileName = fileName;
        }
    }

    private static class AVLIndex {
        int pageIndex;
        int position;

        AVLIndex(int pageIndex, int position) {
            this.pageIndex = pageIndex;
            this.position = position;
        }
    }

    private AVLNode root;

    public void buildIndex(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("Invalid folder path: " + folderPath);
        }

        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                // Read the file and extract the words
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    int pageIndex = 0;
                    int position = 0;
                    while ((line = br.readLine()) != null) {
                        String[] words = line.split("\\s+");
                        for (String word : words) {
                            // Add the position of the word in the file to the AVL tree
                            root = insert(root, word.toLowerCase(), pageIndex, position++, file.getName());
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file: " + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
    }

    private AVLNode insert(AVLNode node, String word, int pageIndex, int position,String fileName) {
        if (node == null) {
            return new AVLNode(word, pageIndex, position,fileName);
        }

        if (word.compareTo(node.key) < 0) {
            node.left = insert(node.left, word, pageIndex, position,fileName);
        } else if (word.compareTo(node.key) > 0) {
            node.right = insert(node.right, word, pageIndex, position,fileName);
        } else {
            // If the word is equal, move to the next word
            node.index.pageIndex = pageIndex;
            node.index.position = position;
            return node;
        }

        // Update height of current node
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Check the balance factor
        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && word.compareTo(node.left.key) < 0) {
            return rightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && word.compareTo(node.right.key) > 0) {
            return leftRotate(node);
        }

        // Left Right Case
        if (balance > 1 && word.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && word.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private int height(AVLNode node) {
        return (node != null) ? node.height : 0;
    }

    private int getBalance(AVLNode node) {
        return (node != null) ? height(node.left) - height(node.right) : 0;
    }

    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    public void searchKeyword(String keyword) {
        AVLNode node = search(root, keyword.toLowerCase());
        if (node == null) {
            System.out.println("Keyword not found: " + keyword);
            return;
        }

        System.out.println("Occurrences of keyword: " + keyword);
        printOccurrences(node);
    }

    private AVLNode search(AVLNode node, String keyword) {
        while (node != null) {
            int comparison = keyword.compareTo(node.key);
            if (comparison == 0) {
                return node;
            } else if (comparison < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return null;
    }

    private void printOccurrences(AVLNode node) {
        if (node != null) {
            printOccurrences(node.left);
            System.out.println("pageIndex: " + node.index.pageIndex + ", Position: " + node.index.position +"  Filename: "+node.fileName);
            printOccurrences(node.right);
        }
    }


}
