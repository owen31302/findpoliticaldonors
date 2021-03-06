import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Yu-Cheng Lin on 10/28/17.
 *
 * TransactionManager will take transaction amount as input,
 * and update the information, such as median, number of transaction, total amount.
 *
 * Note:
 * In order to optimize the getMedian operation, it uses two heap structure (minHeap, maxHeap) to find the median.
 * It will take O(logN) times to maintain the structure, and gives us O(1) time to find the median.
 */
public class TransactionManager {
    private double totalAMT;
    private Queue<Double> maxQueue;
    private Queue<Double> minQueue;

    public TransactionManager(){
        this.maxQueue = new PriorityQueue<>();
        this.minQueue = new PriorityQueue<>();
        this.totalAMT = 0;
    }

    public void deposit(double amount){
        this.totalAMT += amount;

        this.maxQueue.add(amount);
        this.minQueue.add(-maxQueue.poll());
        if(maxQueue.size() < minQueue.size()){
            maxQueue.add(-minQueue.poll());
        }
    }

    public String getTotalTX(){
        return "" + (this.maxQueue.size() + this.minQueue.size());
    }

    public String getTotalAMT(){
        //return String.format("%.2f", this.totalAMT);
        return String.format("%.0f", this.totalAMT);
    }

    public String getMedian(){
        double result = this.maxQueue.size() > this.minQueue.size() ? maxQueue.peek() : (this.maxQueue.peek() - this.minQueue.peek()) / 2.0;
        return "" + Math.round(result);
    }
}
