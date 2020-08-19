package com.alipay.kepler.util;

import com.antfin.arc.arch.message.graph.Edge;
import com.antfin.arc.arch.message.graph.Vertex;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyListSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonListSerializer;
import de.javakaffee.kryoserializers.guava.ArrayListMultimapSerializer;
import de.javakaffee.kryoserializers.guava.HashMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableListSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSetSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSortedSetSerializer;
import de.javakaffee.kryoserializers.guava.LinkedHashMultimapSerializer;
import de.javakaffee.kryoserializers.guava.LinkedListMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ReverseListSerializer;
import de.javakaffee.kryoserializers.guava.TreeMultimapSerializer;
import de.javakaffee.kryoserializers.guava.UnmodifiableNavigableSetSerializer;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class SerDeHelper {
    private static final int INITIAL_BUFFER_SIZE = 4096;
    private static List<String> needRegisterClasses;

    private static ThreadLocal<Kryo> local = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            // for not constructor
            Kryo.DefaultInstantiatorStrategy is = new Kryo.DefaultInstantiatorStrategy();
            is.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            kryo.setInstantiatorStrategy(is);

            kryo.getFieldSerializerConfig().setOptimizedGenerics(false);

            kryo.register(Edge.class, 1033);
            kryo.register(Vertex.class, 1034);

            kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
            kryo.register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
            kryo.register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());

            ArrayListMultimapSerializer.registerSerializers(kryo);
            HashMultimapSerializer.registerSerializers(kryo);
            ImmutableListSerializer.registerSerializers(kryo);
            ImmutableMapSerializer.registerSerializers(kryo);
            ImmutableMultimapSerializer.registerSerializers(kryo);
            ImmutableSetSerializer.registerSerializers(kryo);
            ImmutableSortedSetSerializer.registerSerializers(kryo);
            LinkedHashMultimapSerializer.registerSerializers(kryo);
            LinkedListMultimapSerializer.registerSerializers(kryo);
            ReverseListSerializer.registerSerializers(kryo);
            TreeMultimapSerializer.registerSerializers(kryo);
            UnmodifiableNavigableSetSerializer.registerSerializers(kryo);

            ClassLoader tcl = Thread.currentThread().getContextClassLoader();
            if (tcl != null) {
                kryo.setClassLoader(tcl);
            }
            if (needRegisterClasses != null && needRegisterClasses.size() != 0) {
                for (String clazz : needRegisterClasses) {
                    try {
                        String[] clazzToId = clazz.split(":");
                        int registerId = Integer.parseInt(clazzToId[1]);
                        System.out.println(String.format("register class:%s id:%s", clazz, registerId));
                        kryo.register(Class.forName(clazzToId[0], false,
                            Thread.currentThread().getContextClassLoader()), registerId);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            return kryo;
        }
    };

    //object2Byte
    public static byte[] object2Byte(Object o) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        try {
            local.get().writeClassAndObject(output, o);
            output.flush();
            return byteArrayOutputStream.toByteArray();
        } finally {
            output.clear();
            output.close();
        }
    }

    public static Object byte2Object(byte[] str) {
        Input input = new Input(str);
        return local.get().readClassAndObject(input);
    }

    public static Object byte2Object(byte[] str, int offset, int count) {
        Input input = new Input(str, offset, count);
        return local.get().readClassAndObject(input);
    }

    public static Object byte2Object(InputStream inputStream) {
        List<Object> objects = new ArrayList<>();
        Input input = new Input(inputStream);
        while (true) {
            try {
                objects.add(local.get().readClassAndObject(input));
            } catch (KryoException e) {
                if (e.getMessage().toLowerCase(Locale.ROOT).contains("buffer underflow")) {
                    break;
                }
            }
        }
        return objects;
    }

}
