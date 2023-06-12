package com.yandex.taskmanagerapp.service;


import com.yandex.taskmanagerapp.model.Node;
import com.yandex.taskmanagerapp.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> taskHistory = new CustomLinkedList<>();
    private final Map<Integer, Node> nodesById = new HashMap<>();

    @Override
    public void addTaskToHistory(Task task) {
        if (task == null) {
            return;
        }
        if (nodesById.containsKey(task.getId())) {
            taskHistory.removeNode(nodesById.get(task.getId()));
        }
        nodesById.put(task.getId(), taskHistory.linkLast(task));
    }

    @Override
    public void removeTaskInHistory(int id) {
        if (nodesById.containsKey(id)) {
            taskHistory.removeNode(nodesById.get(id));
            nodesById.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory.getTasks();
    }

    class CustomLinkedList<T> {
        private Node head;
        private Node tail;

        public Node linkLast(T task) {
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
            Node currentNode = head;
            List<T> history = new ArrayList<>();
            if (currentNode == null) {
                System.out.println("Задачи в истории отсутствуют");
            } else {
                while (currentNode != null) {
                    history.add((T) currentNode.task);
                    currentNode = currentNode.next;
                }
            }
            return history;
        }

        public void removeNode(Node<T> node) {
            Node currentNode = head;
            if(currentNode == node) {
                head = currentNode.next;
            } else if (tail == node) {
                tail = tail.previous;
                tail.next = null;
            } else {
                while (currentNode != null) {
                    if (currentNode == node) {
                        currentNode.next.previous = currentNode.previous;
                        currentNode.previous.next = currentNode.next;
                        break;
                    } else {
                        currentNode = currentNode.next;
                    }
                }
            }
        }
    }
}
