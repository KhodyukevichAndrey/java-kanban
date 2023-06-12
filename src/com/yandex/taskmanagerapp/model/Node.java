package com.yandex.taskmanagerapp.model;

public class Node<T> {
    public Node<T> previous;
    public T task;
    public Node<T> next;


    public Node(Node<T> previous, T task, Node<T> next) {
        this.previous = previous;
        this.task = task;
        this.next = next;
    }
}
