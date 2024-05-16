package com.ruoyi.rpa.util;

public class ListNode<T> {
    public T data;
    public ListNode<T> prev;
    public ListNode<T> next;

    public ListNode(T data) {
        this.data = data;
        this.prev = null;
        this.next = null;
    }
}
