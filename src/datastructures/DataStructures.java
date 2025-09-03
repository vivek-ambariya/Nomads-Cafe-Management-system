package datastructures;

import java.util.*;
import java.util.stream.Stream;

public class DataStructures {
    public static class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;
        private int size;

        private static class Node<T> {
            T data;
            Node<T> next;

            Node(T data) {
                this.data = data;
                this.next = null;
            }
        }

        public CustomLinkedList() {
            head = null;
            tail = null;
            size = 0;
        }

        public void add(T data) {
            Node<T> newNode = new Node<>(data);
            if (tail == null) {
                head = newNode;
                tail = newNode;
            } else {
                tail.next = newNode;
                tail = newNode;
            }
            size++;
        }

        public T poll() {
            if (head == null)
                return null;

            T data = head.data;
            head = head.next;
            if (head == null) {
                tail = null;
            }
            size--;
            return data;
        }

        public boolean isEmpty() {
            return head == null;
        }
    }

    public static class CustomHashMap<K, V> {
        private static class Entry<K, V> {
            K key;
            V value;
            Entry<K, V> next;

            Entry(K key, V value) {
                this.key = key;
                this.value = value;
            }
        }

        private final int INITIAL_CAPACITY = 16;
        private Entry<K, V>[] table;
        private int size;
        @SuppressWarnings("unchecked")
        public CustomHashMap() {
            table = new Entry[INITIAL_CAPACITY];
            size = 0;
        }

        private int hash(K key) {
            return (key == null) ? 0 : Math.abs(key.hashCode()) % table.length;
        }

        public void put(K key, V value) {
            int idx = hash(key);
            Entry<K, V> current = table[idx];
            while (current != null) {
                if ((current.key == null && key == null) || (current.key != null && current.key.equals(key))) {
                    current.value = value;
                    return;
                }
                current = current.next;
            }
            Entry<K, V> entry = new Entry<>(key, value);
            entry.next = table[idx];
            table[idx] = entry;
            size++;
        }

        public V get(K key) {
            int idx = hash(key);
            Entry<K, V> current = table[idx];
            while (current != null) {
                if ((current.key == null && key == null) || (current.key != null && current.key.equals(key))) {
                    return current.value;
                }
                current = current.next;
            }
            return null;
        }

        public V remove(K key) {
            int idx = hash(key);
            Entry<K, V> current = table[idx];
            Entry<K, V> prev = null;
            while (current != null) {
                if ((current.key == null && key == null) || (current.key != null && current.key.equals(key))) {
                    if (prev == null) {
                        table[idx] = current.next;
                    } else {
                        prev.next = current.next;
                    }
                    size--;
                    return current.value;
                }
                prev = current;
                current = current.next;
            }
            return null;
        }

        public boolean containsKey(K key) {
            return get(key) != null;
        }

        public void clear() {
            for (int i = 0; i < table.length; i++) {
                table[i] = null;
            }
            size = 0;
        }

        // Returns a Collection of all stored values (like HashMap.values())
        public Collection<V> values() {
            ArrayList<V> values = new ArrayList<>();
            for (int i = 0; i < table.length; i++) {
                Entry<K, V> current = table[i];
                while (current != null) {
                    values.add(current.value);
                    current = current.next;
                }
            }
            return values;
        }
    }
    public static class CustomHashSet<E> implements Iterable<E> {
        private static class Node<E> {
            E value;
            Node<E> next;

            Node(E value) {
                this.value = value;
                this.next = null;
            }
        }
        private final int INITIAL_CAPACITY = 16;
        private Node<E>[] table;
        private int size;

        @SuppressWarnings("unchecked")
        public CustomHashSet() {
            // Fix: generic array allocation in Java
            table = (Node<E>[]) new Node[INITIAL_CAPACITY];
            size = 0;
        }

        private int hash(E value) {
            return (value == null) ? 0 : Math.abs(value.hashCode()) % table.length;
        }

        public boolean add(E value) {
            int idx = hash(value);
            Node<E> current = table[idx];
            while (current != null) {
                if ((current.value == null && value == null) ||
                        (current.value != null && current.value.equals(value))) {
                    return false; // Duplicate, don't add
                }
                current = current.next;
            }
            Node<E> node = new Node<>(value);
            node.next = table[idx];
            table[idx] = node;
            size++;
            return true;
        }

        public boolean contains(E value) {
            int idx = hash(value);
            Node<E> current = table[idx];
            while (current != null) {
                if ((current.value == null && value == null) ||
                        (current.value != null && current.value.equals(value))) {
                    return true;
                }
                current = current.next;
            }
            return false;
        }
        public Stream<E> stream() {
            return values().stream();
        }

        @Override
        public String toString() {
            return values().toString();
        }
        public ArrayList<E> values() {
            ArrayList<E> result = new ArrayList<>();
            for (int i = 0; i < table.length; i++) {
                Node<E> current = table[i];
                while (current != null) {
                    result.add(current.value);
                    current = current.next;
                }
            }
            return result;
        }
        @Override
        public Iterator<E> iterator() {
            return values().iterator();
        }
    }
}