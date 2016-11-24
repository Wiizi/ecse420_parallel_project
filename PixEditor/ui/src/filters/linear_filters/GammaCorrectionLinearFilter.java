package filters.linear_filters;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class GammaCorrectionLinearFilter extends LinearFilter{
    private float gamma_correction;
    public GammaCorrectionLinearFilter(int number_of_threads, float gamma_correction){
        this.number_of_threads = number_of_threads;
        this.gamma_correction = 1.0f/gamma_correction;
    }
    @Override
    public int applyFilterOnPixel(int val){
        int a = ( val >> 24 ) & 0xff;
        int r = ( val >> 16 ) & 0xff;
        int g = ( val >> 8 ) & 0xff;
        int b = val & 0xff;
        r = gammaCorrectChannel(r);
        g = gammaCorrectChannel(g);
        b = gammaCorrectChannel(b);
        val = b;
        val += (g << 8);
        val += (r << 16);
        val += (a << 24);
        return val;
    }

    private int gammaCorrectChannel(int val){
        return (int)(255 * (Math.pow(((float)val / 255.0), this.gamma_correction)));
    }


}