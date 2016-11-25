package filters.linear_filters;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class InversionLinearFilter extends LinearFilter{
    private int threshold;
    public InversionLinearFilter(int number_of_threads, float threshold){
        this.number_of_threads = number_of_threads;
        this.threshold = (int)(255 * threshold);
    }
    @Override
    public int applyFilterOnPixel(int val){
        int a = ( val >> 24 ) & 0xff;
        int r = ( val >> 16 ) & 0xff;
        int g = ( val >> 8 ) & 0xff;
        int b = val & 0xff;
        r = invertChannel(r);
        g = invertChannel(g);
        b = invertChannel(b);
        val = b;
        val += (g << 8);
        val += (r << 16);
        val += (a << 24);
        return val;
    }
    public int invertChannel(int val){
        if (val < threshold)
            val = (int)(255 - val);
        return clamp(val);
    }
}