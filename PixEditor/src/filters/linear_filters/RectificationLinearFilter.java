package filters.linear_filters;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class RectificationLinearFilter extends LinearFilter{
    private int rectification_threshold;
    public RectificationLinearFilter(int number_of_threads, float threshold){
        this.rectification_threshold = (int) (127 * (threshold-1f));
        this.number_of_threads = number_of_threads;
    }
    @Override
    public int applyFilterOnPixel(int val){
        int a = ( val >> 24 ) & 0xff;
        int r = ( val >> 16 ) & 0xff;
        int g = ( val >> 8 ) & 0xff;
        int b = val & 0xff;
        r = rectifyChannel(r);
        g = rectifyChannel(g);
        b = rectifyChannel(b);
        val = b;
        val += (g << 8);
        val += (r << 16);
        val += (a << 24);
        return val;
    }
    private int rectifyChannel(int val){
        val -= rectification_threshold;
        val = (val >= 0) ? val : 0;
        val += rectification_threshold;
        return clamp(val);
    }
}
