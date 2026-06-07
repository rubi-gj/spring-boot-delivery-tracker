package deliverytracker.util;


import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GenericTracker<T extends Comparable<T>> {


    private final TreeSet<T> items = new TreeSet<>();


    private final Map<String, T> itemMap = new HashMap<>();


    public void add(String key, T item) {
        items.add(item);
        itemMap.put(key, item);
    }


    public Optional<T> findByKey(String key) {
        return Optional.ofNullable(itemMap.get(key));
    }


    public List<T> filter(Predicate<T> predicate) {
        return items.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public List<T> getAllSorted() {
        return new ArrayList<>(items);
    }


    public int count() {
        return items.size();
    }


    public boolean contains(String key) {
        return itemMap.containsKey(key);
    }


    public void remove(String key) {
        T item = itemMap.remove(key);
        if (item != null) {
            items.remove(item);
        }
    }
}