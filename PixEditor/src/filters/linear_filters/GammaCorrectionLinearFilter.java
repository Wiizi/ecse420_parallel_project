package filters.linear_filters;

import filters.LinearFilter;

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
        return (int)(255 * (Math.pow(((float)val / 255.0), this.gamma_correction)));
    }
}