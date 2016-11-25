package filters.linear_filters;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class BrightnessLinearFilter extends LinearFilter{
    private int brightness;
    public BrightnessLinearFilter(int number_of_threads, float brightness){
        this.number_of_threads = number_of_threads;
        this.brightness = (int)(255 * (brightness-1f));
    }
    @Override
    public int applyFilterOnPixel(int val){
        int a = ( val >> 24 ) & 0xff;
        int r = ( val >> 16 ) & 0xff;
        int g = ( val >> 8 ) & 0xff;
        int b = val & 0xff;
        r = brightnessChannel(r);
        g = brightnessChannel(g);
        b = brightnessChannel(b);
        val = b;
        val += (g << 8);
        val += (r << 16);
        val += (a << 24);
        return val;
    }
    private int brightnessChannel(int val){
        return val + this.brightness;
    }

}
