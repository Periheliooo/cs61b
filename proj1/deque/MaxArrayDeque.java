package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> MaxArrayDequeComparator;

    public MaxArrayDeque(Comparator<T> c){
        super();
        MaxArrayDequeComparator = c;
    }

    /**
     * import java.util.Comparator;
     *
     * // 这是一个专门比字符串长度的裁判
     * public class StringLengthComparator implements Comparator<String> {
     *
     *     // 裁判的核心技能：compare 方法
     *     @Override
     *     public int compare(String a, String b) {
     *         // 如果 a 比 b 长，返回正数；如果一样长，返回 0；如果 a 比 b 短，返回负数。
     *         return a.length() - b.length();
     *     }
     * }
     */

    public T max(){
        return max(MaxArrayDequeComparator);
    }

    public T max(Comparator<T> c){
        if (isEmpty()){
            return null;
        }
        T maxItem = this.get(0);
        for (T i : this){
            if (c.compare(i, maxItem) > 0){
                maxItem = i;
            }
        }
        return maxItem;
    }
}
