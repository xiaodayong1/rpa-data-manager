package com.ruoyi.rpa.util;

import java.util.ArrayList;
import java.util.List;

public class LinkedListUtil<T> {

    public ListNode<T> toLinkedList(List<T> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return null;
        }

        ListNode<T> head = new ListNode<>(dataList.get(0));
        ListNode<T> tail = head;

        for (int i = 1; i < dataList.size(); i++) {
            ListNode<T> newNode = new ListNode<>(dataList.get(i));
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }

        return head;
    }

    public List<T> toArrayList(ListNode<T> node) {
        ArrayList<T> list = new ArrayList<>();
        ListNode<T> current = node;

        while (current != null) {
            list.add(current.data);
            current = current.next;
        }

        return list;
    }

    public List<T> toArrayListDesc(ListNode<T> node) {
        ArrayList<T> list = new ArrayList<>();
        ListNode<T> current = node;

        while (current != null) {
            list.add(current.data);
            current = current.prev;
        }

        return list;
    }

    public void traverse(ListNode<T> head) {
        ListNode<T> current = head;
        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
    }
}
