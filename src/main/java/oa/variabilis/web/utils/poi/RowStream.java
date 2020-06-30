/*
 *  Variabilis - Un software estadístico para aplicación de la batería de riesgo psicosocial en Colombia.
 *  El presente software se provee bajo una licencia comercial y está cubierto por derechos de autor.
 *  No se autoriza su uso directo o indirecto , descompilación o uso del código fuente sin el consentimiento expreso del autor de la obra.
 *  Nestor Arias-2015
 */
package oa.variabilis.web.utils.poi;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Iterador sencillo de filas para hojas de cálculo.
 *
 * @author Nestor Arias <nestor_arias@hotmail.com>
 */
public class RowStream implements Stream<Row> {

    private final Sheet sheet;
    public RowStream(Sheet sheet) {
        this.sheet = sheet;
    }

    private Stream<Row> buildStream() {
        Iterable<Row> iterable = () -> new RowIterator(sheet);
        Stream<Row> targetStream = StreamSupport.stream(iterable.spliterator(), false);
        return targetStream;
    }

    @Override
    public Stream<Row> filter(Predicate<? super Row> predicate) {
        return buildStream().filter(predicate);
    }

    @Override
    public <R> Stream<R> map(Function<? super Row, ? extends R> mapper) {
        return buildStream().map(mapper);
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super Row> mapper) {
        return buildStream().mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super Row> mapper) {
        return buildStream().mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super Row> mapper) {
        return buildStream().mapToDouble(mapper);
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super Row, ? extends Stream<? extends R>> mapper) {
        return buildStream().flatMap(mapper);
    }

    @Override
    public IntStream flatMapToInt(Function<? super Row, ? extends IntStream> mapper) {
        return buildStream().flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(Function<? super Row, ? extends LongStream> mapper) {
        return buildStream().flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super Row, ? extends DoubleStream> mapper) {
        return buildStream().flatMapToDouble(mapper);
    }

    @Override
    public Stream<Row> distinct() {
        return buildStream().distinct();
    }

    @Override
    public Stream<Row> sorted() {
        return buildStream().sorted();
    }

    @Override
    public Stream<Row> sorted(Comparator<? super Row> comparator) {
        return buildStream().sorted(comparator);
    }

    @Override
    public Stream<Row> peek(Consumer<? super Row> action) {
        return buildStream().peek(action);
    }

    @Override
    public Stream<Row> limit(long maxSize) {
        return buildStream().limit(maxSize);
    }

    @Override
    public Stream<Row> skip(long n) {
        return buildStream().skip(n);
    }

    @Override
    public void forEach(Consumer<? super Row> action) {
        buildStream().forEach(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super Row> action) {
        buildStream().forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return buildStream().toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return buildStream().toArray(generator);
    }

    @Override
    public Row reduce(Row identity, BinaryOperator<Row> accumulator) {
        return buildStream().reduce(identity, accumulator);
    }

    @Override
    public Optional<Row> reduce(BinaryOperator<Row> accumulator) {
        return buildStream().reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super Row, U> accumulator, BinaryOperator<U> combiner) {
        return buildStream().reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Row> accumulator, BiConsumer<R, R> combiner) {
        return buildStream().collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super Row, A, R> collector) {
        return buildStream().collect(collector);
    }

    @Override
    public Optional<Row> min(Comparator<? super Row> comparator) {
        return buildStream().min(comparator);
    }

    @Override
    public Optional<Row> max(Comparator<? super Row> comparator) {
        return buildStream().max(comparator);
    }

    @Override
    public long count() {
        return buildStream().count();
    }

    @Override
    public boolean anyMatch(Predicate<? super Row> predicate) {
        return buildStream().anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super Row> predicate) {
        return buildStream().allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super Row> predicate) {
        return buildStream().noneMatch(predicate);
    }

    @Override
    public Optional<Row> findFirst() {
        return buildStream().findFirst();
    }

    @Override
    public Optional<Row> findAny() {
        return buildStream().findAny();
    }

    @Override
    public Iterator<Row> iterator() {
        return sheet.iterator();
    }

    @Override
    public Spliterator<Row> spliterator() {
        return buildStream().spliterator();
    }

    @Override
    public boolean isParallel() {
        return buildStream().isParallel();
    }

    @Override
    public Stream<Row> sequential() {
        return buildStream().sequential();
    }

    @Override
    public Stream<Row> parallel() {
        return buildStream().parallel();
    }

    @Override
    public Stream<Row> unordered() {
        return buildStream().unordered();
    }

    @Override
    public Stream<Row> onClose(Runnable closeHandler) {
        return buildStream().onClose(closeHandler);
    }

    @Override
    public void close() {
     //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
