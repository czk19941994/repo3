package com.xuecheng.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


@SpringBootTest
@RunWith(SpringRunner.class)
public class Rest {
    public static void main(String[] args) throws Exception{
        boolean b = hasNull(new Pair<String>(null, ""));
        System.out.println(b);
        swap(new Pair<String>("a","b"));
        Person person = Person.class.newInstance();
        System.out.println(person);
        Pair<String> stringPair = makePair(String.class);
        Type genericSuperclass = stringPair.getClass().getGenericSuperclass();
        Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        Class clazz= (Class)actualTypeArguments[0];
        String o = (String) clazz.newInstance();
        System.out.println(o.length());
    }
    @Test
    public static boolean hasNull(Pair<?> p){
        return p.getFirst()==null||p.getSecond()==null;

    }
    //通配符对捕获
    public static void swap(Pair<?> pair){
        swapHelper(pair);
        System.out.println(pair.getFirst());
    }
    public static<T> void swapHelper(Pair<T> pair){
        T t=pair.getFirst();
        pair.setFirst(pair.getSecond());
        pair.setSecond(t);
    }
    //使用class<t>类型进行参数匹配
    public static <T> Pair<T> makePair(Class<T> c)throws Exception{
        return new Pair<>(c.newInstance(),c.newInstance());
    }

}

