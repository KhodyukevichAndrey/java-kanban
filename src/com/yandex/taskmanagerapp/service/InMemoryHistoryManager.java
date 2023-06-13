package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> taskHistory = new CustomLinkedList<>();
    private final Map<Integer, Node<Task>> nodesById = new HashMap<>();

    @Override
    public void addTaskToHistory(Task task) {
        if (task == null) {
            return;
        }
        Node<Task> duplicateNode = nodesById.get((task.getId()));
        if (duplicateNode != null) {
            taskHistory.removeNode(duplicateNode);
        }
        nodesById.put(task.getId(), taskHistory.linkLast(task));
    }

    @Override
    public void removeTaskInHistory(int id) {
        Node<Task> remove = nodesById.remove(id);
        if (remove != null) {
            taskHistory.removeNode(remove);
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory.getTasks();
    }

    private static class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;

        public Node<T> linkLast(T task) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(tail, task, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            return newNode;
        }

        public List<T> getTasks() {
            Node<T> currentNode = head;
            List<T> history = new ArrayList<>();
            while (currentNode != null) {
                history.add(currentNode.task);
                currentNode = currentNode.next;
            }
            return history;
        }

        public void removeNode(Node<T> node) {
            final Node<T> prev = node.previous;
            final Node<T> next = node.next;
            if (prev == null && next == null) {
                head = null;
                tail = null;
            } else if (prev == null) {
                head = next;
                head.previous = null;
            } else if (next == null) {
                tail = prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.previous = prev;
            }
        }
    }

    private static class Node<T> {
        private Node<T> previous;
        private final T task;
        private Node<T> next;


        public Node(Node<T> previous, T task, Node<T> next) {
            this.previous = previous;
            this.task = task;
            this.next = next;
        }
    }
}
