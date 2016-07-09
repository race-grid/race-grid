package racegrid.api;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Util {

    public static <K1, K2, V> Map<K2, V> mapKeys(Map<K1, V> originalMap, Function<K1, K2> keyMapper) {
        return originalMap.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> keyMapper.apply(e.getKey()),
                        Map.Entry::getValue
                ));
    }
}
