package filters.linear_filters;

/**
 * Created by Andrei-ch on 2016-11-22.
 */
public class BinarizeLinearFilter extends LinearFilter {
    private int threshold;

    public BinarizeLinearFilter(int number_of_threads, float threshold) {
        this.number_of_threads = number_of_threads;
        this.threshold = (int) (255 * 3 * (threshold));
    }

    @Override
    public int applyFilterOnPixel(int val) {
        int a = (val >> 24) & 0xff;
        int r = (val >> 16) & 0xff;
        int g = (val >> 8) & 0xff;
        int b = val & 0xff;
        val = brightnessChannel(r,g,b);
        r = val;
        g = val;
        b = val;
        val = b;
        val += (g << 8);
        val += (r << 16);
        val += (a << 24);
        return val;
    }

    private int brightnessChannel(int r, int g, int b) {
        int val = r + g + b;
        if (val < threshold)
            return 0;
        else
            return 255;
    }
}