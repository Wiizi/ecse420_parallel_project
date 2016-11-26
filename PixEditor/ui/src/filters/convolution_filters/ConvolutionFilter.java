package filters.convolution_filters;

import filters.Filter;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * Created by Andrei-ch on 2016-11-20.
 */
public abstract class ConvolutionFilter extends Filter {
    protected float[][] weights;
    protected int radius;

    @Override
    public BufferedImage applyFilter(BufferedImage image){
        //image = image.getSubimage(512,512,2,2);
        BufferedImage output_image = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        filter(image, output_image, this.number_of_threads);
        return output_image;
    }

    public void filter(BufferedImage image, BufferedImage output_image, int number_of_threads){
        int width_in, height_in, total_pixels, pixels_per_thread, leftover;
        width_in = image.getWidth();
        height_in = image.getHeight();
        total_pixels = width_in * height_in;
        pixels_per_thread = total_pixels / number_of_threads;
        leftover = total_pixels - number_of_threads * pixels_per_thread;

        ConvolutionThread runnables[] = new ConvolutionThread[number_of_threads];
        Thread threads[] = new Thread[number_of_threads];

        // set up thread args
        for (int i = 0; i < number_of_threads && i < total_pixels; i++) {
            runnables[i] = new ConvolutionThread(i,image, output_image, pixels_per_thread, pixels_per_thread * i);
        }
        //System.out.println("leftover " + leftover);
        // if image length is not a multiple of number of threads there will be some leftover bits
        if (leftover > 0){
            runnables[0].length += leftover;
            for (int i = 1; i < number_of_threads; i++) {
                runnables[i].length_offset += leftover;
            }
        }
        long t1 = System.currentTimeMillis();
        // start threads
        for (int i = 0; i < number_of_threads; i++) {
            threads[i] = new Thread(runnables[i]);
            threads[i].start();
        }
        // join threads
        for (int i = 0; i < number_of_threads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long t2 = System.currentTimeMillis();
        //System.out.println("Time spent inside filter: [" +(t2-t1) +"]");
        System.out.println("Filtering complete.");
    }

    protected float[][] rotateMatrix90(float[][] matrix, int rotations){
        rotations %= 4;
        if (rotations == 0)
            return matrix;
        int n = matrix.length;
        float[][] ret = new float[n][n];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                ret[i][j] = matrix[n - j - 1][i];
            }
        }
        return rotateMatrix90(ret, rotations-1);
    }

    private class ConvolutionThread implements Runnable{
        private int thread_id;
        protected BufferedImage image_buffer, output_buffer;
        protected int length, length_offset;
        public ConvolutionThread(int thread_id, BufferedImage image_buffer, BufferedImage output_buffer, int length, int length_offset){
            this.thread_id = thread_id;
            this.output_buffer = output_buffer;
            this.image_buffer = image_buffer;
            this.length = length;
            this.length_offset = length_offset;
        }
        public void run(){
            byte[] bytes_in = new byte[4];
            ByteBuffer buffer_in = ByteBuffer.wrap(bytes_in);
            int i,j, imageWidth, imageHeight, pixel_rgba;
            imageWidth = image_buffer.getWidth();
            imageHeight = image_buffer.getHeight();
            i = get_2d(length_offset, imageWidth, 1);
            j = get_2d(length_offset, imageWidth, 0);
            //System.out.println("Thread" + this.thread_id + " working on " + this.length + " pixels starting at offset " + this.length_offset + "; ["+i+","+j+"]");
            int counter = 0;
            while (this.length > 0){
                counter++;
                int cA, cR, cG, cB;
                cA = 255;
                cR = cG = cB = 0;
                for (int jj = -radius; jj < radius+1; jj++){
                    for (int ii = -radius; ii < radius+1; ii++){
                        int x,y;
                        x = i + ii;
                        y = j + jj;
                        if (x < 0 || y < 0 || x >= imageWidth || y >= imageHeight)
                            continue;
                        pixel_rgba = image_buffer.getRGB(x,y);
                        buffer_in.putInt(0,pixel_rgba);
                        cR += weights[ii+radius][jj+radius] * getUnsignedByte(buffer_in.get(1));
                        cG += weights[ii+radius][jj+radius] * getUnsignedByte(buffer_in.get(2));
                        cB += weights[ii+radius][jj+radius] * getUnsignedByte(buffer_in.get(3));
                    }
                }
                cR = clamp(cR);
                cG = clamp(cG);
                cB = clamp(cB);
                buffer_in.put(0, (byte)cA);
                buffer_in.put(1, (byte)cR);
                buffer_in.put(2, (byte)cG);
                buffer_in.put(3, (byte)cB);
                pixel_rgba = buffer_in.getInt(0);
                output_buffer.setRGB(i,j, pixel_rgba);
                i++;
                if (i == imageWidth){ // preincrement when on the edge of the image
                    j++;
                    i=0;
                }
                this.length--;
            }
            //System.out.println("thread" + this.thread_id + ": counter " + counter);
        }
    }
}