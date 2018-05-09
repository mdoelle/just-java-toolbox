package de.justsoftware.toolbox.stream;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;

/**
 * A {@link Stream} for {@link Map.Entry}s
 *
 * The code is inspired by
 * <a href=
 * "https://stackoverflow.com/questions/29239377/any-way-to-stream-a-map-like-k-v-instead-of-working-with-entry#answer-29254246">Any
 * way to stream a map like “(k,v)” instead of working with (entry)? (Stackoverflow)</a>
 *
 * @param <K>
 *            type of the keys of the entries in the stream
 * @param <V>
 *            type of the values of the entries in the stream
 */
@ParametersAreNonnullByDefault
public final class EntryStream<K, V> {

    private final Stream<Map.Entry<K, V>> _stream;

    private EntryStream(final Stream<Map.Entry<K, V>> stream) {
        _stream = stream;
    }

    @Nonnull
    public static <K, V> EntryStream<K, V> from(final Map<K, V> map) {
        return from(map.entrySet().stream());
    }

    @Nonnull
    public static <K, V> EntryStream<K, V> from(final Multimap<K, V> multimap) {
        return from(multimap.entries().stream());
    }

    @Nonnull
    public static <K, V> EntryStream<K, V> from(final Stream<Map.Entry<K, V>> s) {
        return new EntryStream<>(s);
    }

    @Nonnull
    public static <K, V> EntryStream<K, V> from(final Stream<K> s, final Function<? super K, ? extends V> f) {
        return from(s, Function.identity(), f);
    }

    @Nonnull
    public static <S, K, V> EntryStream<K, V> from(final Stream<S> s,
            final Function<? super S, ? extends K> keyFunction,
            final Function<? super S, ? extends V> valueFunction) {
        return from(s.map(k -> new AbstractMap.SimpleImmutableEntry<>(keyFunction.apply(k), valueFunction.apply(k))));
    }

    @Nonnull
    public Stream<Map.Entry<K, V>> stream() {
        return _stream;
    }

    @Nonnull
    public EntryStream<K, V> distinct() {
        return from(_stream.distinct());
    }

    @Nonnull
    public EntryStream<K, V> peek(final BiConsumer<? super K, ? super V> action) {
        return from(_stream.peek(e -> action.accept(e.getKey(), e.getValue())));
    }

    @Nonnull
    public EntryStream<K, V> skip(final long n) {
        return from(_stream.skip(n));
    }

    @Nonnull
    public EntryStream<K, V> limit(final long maxSize) {
        return from(_stream.limit(maxSize));
    }

    @Nonnull
    public EntryStream<K, V> filterKey(final Predicate<? super K> keyPredicate) {
        return from(_stream.filter(e -> keyPredicate.test(e.getKey())));
    }

    @Nonnull
    public EntryStream<K, V> filterValue(final Predicate<? super V> valuePredicate) {
        return from(_stream.filter(e -> valuePredicate.test(e.getValue())));
    }

    @Nonnull
    public EntryStream<K, V> filter(final BiPredicate<? super K, ? super V> predicate) {
        return from(_stream.filter(e -> predicate.test(e.getKey(), e.getValue())));
    }

    @Nonnull
    public <R> EntryStream<R, V> mapKey(final Function<? super K, ? extends R> keyMapper) {
        return from(_stream.map(e -> new AbstractMap.SimpleImmutableEntry<>(keyMapper.apply(e.getKey()), e.getValue())));
    }

    @Nonnull
    public <R> EntryStream<K, R> mapValue(final Function<? super V, ? extends R> valueMapper) {
        return from(
                _stream.map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), valueMapper.apply(e.getValue()))));
    }

    @Nonnull
    public <R> Stream<R> map(final BiFunction<? super K, ? super V, ? extends R> mapper) {
        return _stream.map(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    @Nonnull
    public DoubleStream mapToDouble(final ToDoubleBiFunction<? super K, ? super V> mapper) {
        return _stream.mapToDouble(e -> mapper.applyAsDouble(e.getKey(), e.getValue()));
    }

    @Nonnull
    public IntStream mapToInt(final ToIntBiFunction<? super K, ? super V> mapper) {
        return _stream.mapToInt(e -> mapper.applyAsInt(e.getKey(), e.getValue()));
    }

    @Nonnull
    public LongStream mapToLong(final ToLongBiFunction<? super K, ? super V> mapper) {
        return _stream.mapToLong(e -> mapper.applyAsLong(e.getKey(), e.getValue()));
    }

    @Nonnull
    public <RK, RV> EntryStream<RK, RV> flatMap(
            final BiFunction<? super K, ? super V, ? extends EntryStream<RK, RV>> mapper) {
        return from(_stream.flatMap(e -> mapper.apply(e.getKey(), e.getValue()).stream()));
    }

    @Nonnull
    public <R> Stream<R> flatMapToObj(final BiFunction<? super K, ? super V, ? extends Stream<R>> mapper) {
        return _stream.flatMap(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    @Nonnull
    public DoubleStream flatMapToDouble(final BiFunction<? super K, ? super V, ? extends DoubleStream> mapper) {
        return _stream.flatMapToDouble(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    @Nonnull
    public IntStream flatMapToInt(final BiFunction<? super K, ? super V, ? extends IntStream> mapper) {
        return _stream.flatMapToInt(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    @Nonnull
    public LongStream flatMapToLong(final BiFunction<? super K, ? super V, ? extends LongStream> mapper) {
        return _stream.flatMapToLong(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    @Nonnull
    public EntryStream<K, V> sortedByKey(final Comparator<? super K> comparator) {
        return from(_stream.sorted(Map.Entry.comparingByKey(comparator)));
    }

    @Nonnull
    public EntryStream<K, V> sortedByValue(final Comparator<? super V> comparator) {
        return from(_stream.sorted(Map.Entry.comparingByValue(comparator)));
    }

    public boolean allMatch(final BiPredicate<? super K, ? super V> predicate) {
        return _stream.allMatch(e -> predicate.test(e.getKey(), e.getValue()));
    }

    public boolean anyMatch(final BiPredicate<? super K, ? super V> predicate) {
        return _stream.anyMatch(e -> predicate.test(e.getKey(), e.getValue()));
    }

    public boolean noneMatch(final BiPredicate<? super K, ? super V> predicate) {
        return _stream.noneMatch(e -> predicate.test(e.getKey(), e.getValue()));
    }

    public long count() {
        return _stream.count();
    }

    @Nonnull
    public Stream<K> keys() {
        return _stream.map(Map.Entry::getKey);
    }

    @Nonnull
    public Stream<V> values() {
        return _stream.map(Map.Entry::getValue);
    }

    @Nonnull
    public Optional<Map.Entry<K, V>> maxByKey(final Comparator<? super K> comparator) {
        return _stream.max(Map.Entry.comparingByKey(comparator));
    }

    @Nonnull
    public Optional<Map.Entry<K, V>> maxByValue(final Comparator<? super V> comparator) {
        return _stream.max(Map.Entry.comparingByValue(comparator));
    }

    @Nonnull
    public Optional<Map.Entry<K, V>> minByKey(final Comparator<? super K> comparator) {
        return _stream.min(Map.Entry.comparingByKey(comparator));
    }

    @Nonnull
    public Optional<Map.Entry<K, V>> minByValue(final Comparator<? super V> comparator) {
        return _stream.min(Map.Entry.comparingByValue(comparator));
    }

    public void forEach(final BiConsumer<? super K, ? super V> action) {
        _stream.forEach(e -> action.accept(e.getKey(), e.getValue()));
    }

    public void forEachOrdered(final BiConsumer<? super K, ? super V> action) {
        _stream.forEachOrdered(e -> action.accept(e.getKey(), e.getValue()));
    }

    public <R> R collect(final Collector<? super Map.Entry<K, V>, ?, R> collector) {
        return _stream.collect(collector);
    }

    @Nonnull
    public static <K, V> EntryStream<K, V> of() {
        return from(Stream.of());
    }

    @Nonnull
    public static <K, V> EntryStream<K, V> of(final K k1, final V v1) {
        return from(Stream.of(new AbstractMap.SimpleImmutableEntry<>(k1, v1)));
    }

    @Nonnull
    public static <K, V> EntryStream<K, V> of(final K k1, final V v1, final K k2, final V v2) {
        return from(Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(k1, v1),
                new AbstractMap.SimpleImmutableEntry<>(k2, v2)));
    }

    @Nonnull
    public static <K, V> EntryStream<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3) {
        return from(Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(k1, v1),
                new AbstractMap.SimpleImmutableEntry<>(k2, v2),
                new AbstractMap.SimpleImmutableEntry<>(k3, v3)));
    }

}
