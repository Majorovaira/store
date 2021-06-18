package ua.spribe.store.mutex;

import org.springframework.stereotype.Service;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductMutex {

    private ConcurrentHashMap<HashSet<Long>, WeakReference<Object>> mutexes = new ConcurrentHashMap<>();

    /*
    if two customers try to buy one product,
    we need to help first customer make this purchase
    because he has unique rule for this.
    That's the reason why we blocked all bucket if we have non unique product's ids
     */
    public synchronized WeakReference<Object> getLock(List<Long> ids) {
        Optional<Map.Entry<HashSet<Long>, WeakReference<Object>>> first = mutexes.entrySet()
                .stream()
                .filter(entry -> hasAnyOfCurrentIds(ids, entry.getKey()))
                .findFirst();
        if (first.isPresent()) {
            first.get().getKey().addAll(ids);
            return first.get().getValue();
        } else {
            Object object = new Object();
            WeakReference<Object> mutex = new WeakReference<>(object);
            mutexes.put(new HashSet<>(ids), mutex);
            return mutex;
        }
    }


    private boolean hasAnyOfCurrentIds(List<Long> ids, HashSet<Long> entryKey) {
        return ids.stream().anyMatch(entryKey::contains);
    }
}
