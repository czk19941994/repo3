package com.xuecheng.manage_cms;

import java.util.*;

public class Son extends Person<Date> {
    public void setSecond(Date date){

        System.out.println(date.getTime());
    }

    public static void main(String[] args) {
        List<String> list=new ArrayList<>();
        list.add("n");
        list.add("a");
        List<String> list2=new ArrayList<>();
        list2.add("a");
        list.retainAll(list2);
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
        list.size();
        List<String> staff=new LinkedList<>();
        staff.add("amy");
        staff.add("bob");
        staff.add("ccc");
        ListIterator<String> listIterator = staff.listIterator();
        listIterator.next();
        listIterator.add("aa");
        Iterator<String> iterator1 = staff.iterator();
        while (iterator1.hasNext()){
            System.out.println(iterator1.next());
        }
        List<String> list1 = Collections.nCopies(10, "a");
        for (String s : list1) {
            System.out.println(s);
        }
        Object[] objects = list.toArray();
        String[] strings = list.toArray(new String[list.size()]);
        Collections.sort(staff);
        int ccc = Collections.binarySearch(staff, "ccc");
        System.out.println(ccc);

    }
}
