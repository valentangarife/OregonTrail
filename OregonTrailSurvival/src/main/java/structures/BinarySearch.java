package structures;

public class BinarySearch<T extends Comparable<T>> {

    public String binarySearch(LinkedList<T, ?> list, T target) {

        if (list == null || target == null || list.getSize() == 0) {
            return "-1";
        }

        int left = 0;
        int right = list.getSize() - 1;

        while (left <= right) {

            int mid = left + (right - left) / 2;
            Node<T, ?> midNode = list.search(mid);

            if (midNode == null) {
                return "-1";
            }

            T midValue = midNode.getKey();
            int cmp = midValue.compareTo(target);

            if (cmp == 0) {
                return String.valueOf(mid + 1); // 1-indexado
            }
            if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return "-1";
    }
}
