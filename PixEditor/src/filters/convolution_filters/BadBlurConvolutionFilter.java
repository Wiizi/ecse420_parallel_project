package filters.convolution_filters;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class BadBlurConvolutionFilter extends ConvolutionFilter{
    public BadBlurConvolutionFilter(int number_of_threads, float strength){
        this.weights = new float[][] {
                {0.1f*strength, 0.1f*strength,       0.1f*strength},
                {0.1f*strength, 1-strength*0.1f*8,   0.1f*strength},
                {0.1f*strength, 0.1f*strength,       0.1f*strength}
        };
        this.radius = 1;
        this.number_of_threads = number_of_threads;
    }
}
