package cn.itcast.demo.search;

/**
 * 递归折半查找
 */
public class BinarySearch {
    public static void main(String[] args) {
            Integer[] arr={1,2,3,4,5,6,7,8,15,89};
            int key=8;
        int i = binarySearch(0, arr.length-1, arr, key);
        System.out.println(i);
    }
    public static int binarySearch(int low,int high,Integer[] arr,int key){
        int mid=(low+high)/2;
        if (low>high){
            return -1;
        }
        if (arr[mid]==key){
            return mid;
        }else if (arr[mid]>key){
            high=mid-1;
            return binarySearch(low,high,arr,key);
        }else {
            low=mid+1;
            return binarySearch(low,high,arr,key);
        }
    }
}
