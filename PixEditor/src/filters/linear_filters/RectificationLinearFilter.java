package filters.linear_filters;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class RectificationLinearFilter extends LinearFilter{
    public RectificationLinearFilter(int number_of_threads){
        this.number_of_threads = number_of_threads;
    }
    @Override
    public int applyFilterOnPixel(int val){
        val -= 127;
        val = (val >= 0) ? val : 0;
        val += 127;
        return val;
    }
}
