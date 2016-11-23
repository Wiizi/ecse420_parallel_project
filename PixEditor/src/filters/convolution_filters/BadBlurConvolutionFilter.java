package filters.convolution_filters;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class BadBlurConvolutionFilter extends ConvolutionFilter{
    public BadBlurConvolutionFilter(int number_of_threads, float strength){
        this.radius = 3;
        this.number_of_threads = number_of_threads;
        this.weights = new float[radius * 2 + 1][radius * 2 + 1];
        float sum = 0;
        strength *= 1;
        for (int jj = 0; jj < radius * 2 + 1; jj++){
            for (int ii = 0; ii < radius * 2 + 1; ii++){
                sum += strength;
            }
        }
        float val = 1f / sum;
        for (int jj = 0; jj < radius * 2 + 1; jj++){
            for (int ii = 0; ii < radius * 2 + 1; ii++){
                this.weights[ii][jj] = strength * val;
            }
        }
    }
}
