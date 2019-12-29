package cn.itcast.demo;

import java.util.Arrays;

/**
 * 快速排序
 */
public class QuickSort {
    public static void main(String[] args) {
        int[] arr={6,5,4,3,2,1};
        quickSort(arr);
    }
    public static void quickSort(int[] arr){
        //输出原数组
        System.out.println(Arrays.toString(arr));
        int low=0;
        int high=arr.length-1;
        quickSort(low,high,arr);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 排序
     * @param low
     * @param high
     * @param arr
     */
    private static void quickSort(int low, int high, int[] arr) {
        if (low<high){
            //返回选择的索引
            int i=partition(low,high,arr);
            //对左边进行排序
            quickSort(low,i-1,arr);
            //对右边进行排序
            quickSort(i+1,high,arr);
        }
    }

    private static int partition(int low, int high, int[] arr) {
        //制定做指针和右指针
        int i=low;
        int j=high;
        //制定第一值作为基准值
        int x=arr[low];
        //使用循环实现分区操作
        while (i<j) {
            while (arr[j] > x && i < j) j--;

            if (i < j) {
                arr[i] = arr[j];
                i++;
            }
            while (arr[i] < x && i < j) i++;
            if (i < j) {
                arr[j] = arr[i];
                j--;
            }
        }
        //基准值填坑
        arr[i]=x;
        //返回索引
        return i;
    }
}
