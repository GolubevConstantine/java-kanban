package manager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task task;
        Node next;
        Node prev;

        public Node(Task element, Node next, Node prev) {
            this.task = element;
            this.next = next;
            this.prev = prev;
        }
    }

    HashMap<Integer, Node> historyMap = new HashMap<>();
    Node first;
    Node last;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        linkLast(task);
    }

    void linkLast(Task task) {
        final Node node = new Node(task, null, last);
        if (first == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
        historyMap.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        Node remove = historyMap.remove(id);
        if (remove == null) return;
        if (remove.prev == null && remove.next == null) {
            first = null;
            last = null;
        }
        if (remove.prev == null) {
            first = remove.next;
            remove.next.prev = null;
        } else if (remove.next == null) {
            last = remove.prev;
            remove.prev.next = null;
        } else {
            remove.prev.next = remove.next;
            remove.next.prev = remove.prev;
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = first;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }
}
