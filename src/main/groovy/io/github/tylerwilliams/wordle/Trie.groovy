package io.github.tylerwilliams.wordle

import groovy.transform.CompileStatic

/**
 * Partially implements https://en.wikipedia.org/wiki/Trie
 */
@CompileStatic
class Trie {
    private Node root = new Node(value: (char) 0)
    private Set<String> wordSet

    Trie(Set<String> wordSet) {
        this.wordSet = wordSet

        for (word in wordSet) {
            addNode(word)
        }
    }

    boolean anyWordStartsWith(String prefix) {
        findNode(prefix)?.hasChildren()
    }

    boolean contains(String word) {
        wordSet.contains(word)
    }

    private void addNode(String word) {
        Node addedNode = word.toCharArray().inject(root) { Node currentNode, char nextChar ->
            currentNode.getOrCreateChild(nextChar)
        }

        addedNode.word = word
    }

    private Node findNode(String str) {
        Node node = root
        for (char c : str.toCharArray()) {
            node = node.getChild(c)
            if (node == null) {
                break
            }
        }
        return node
    }

    @CompileStatic
    private static class Node {
        private char value
        private String word
        private Map<Character, Node> children

        boolean hasChildren() {
            children?.size() > 0
        }

        Node getChild(char c) {
            children?[c]
        }

        Node getOrCreateChild(char c) {
            if (children == null) {
                children = new HashMap<>()
            }
            children.computeIfAbsent(c) { new Node(value: c) }
        }
    }
}
