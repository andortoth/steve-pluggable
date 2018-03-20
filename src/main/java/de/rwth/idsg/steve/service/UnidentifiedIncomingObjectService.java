package de.rwth.idsg.steve.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The name of this class was inspired by UFO (Unidentified flying object) and enterprise software development.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2018
 */
public class UnidentifiedIncomingObjectService {

    private final Cache<String, UnidentifiedIncomingObject> objectsHolder;

    public UnidentifiedIncomingObjectService(int maxSize) {
        objectsHolder = CacheBuilder.newBuilder()
                                    .maximumSize(maxSize)
                                    .build();
    }

    public List<UnidentifiedIncomingObject> getObjects() {
        return objectsHolder.asMap()
                            .values()
                            .stream()
                            .sorted(Comparator.comparingInt(UnidentifiedIncomingObject::getNumberOfAttempts).reversed())
                            .collect(Collectors.toList());
    }

    public void processNewUnidentified(String key) {
        synchronized (objectsHolder) {
            UnidentifiedIncomingObject value = objectsHolder.getIfPresent(key);
            if (value == null) {
                objectsHolder.put(key, new UnidentifiedIncomingObject(key));
            } else {
                value.updateStats();
            }
        }
    }
}
