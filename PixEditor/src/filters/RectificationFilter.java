package filters;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Andrei-ch on 2016-11-20.
 */
public class RectificationFilter extends Filter {
    public RectificationFilter(int number_of_threads){
        this.number_of_threads = number_of_threads;
    }

    @Override
    public BufferedImage applyFilter(BufferedImage image){
        BufferedImage output_image = image.getSubimage(0,0,image.getWidth(),image.getHeight());
        filter(image, output_image, this.number_of_threads);
        return output_image;
    }

    public void filter(BufferedImage image, BufferedImage output_image, int number_of_threads){
        int width_in, height_in, total_pixels, pixels_per_thread, leftover;
        width_in = image.getWidth();
        height_in = image.getHeight();
        total_pixels = width_in * height_in;
        pixels_per_thread = total_pixels / (number_of_threads);
        leftover = total_pixels - number_of_threads * pixels_per_thread;

        RectificationThread runnables[] = new RectificationThread[number_of_threads];
        Thread threads[] = new Thread[number_of_threads];

        // set up thread args
        for (int i = 0; i < number_of_threads && i < total_pixels; i++) {
            runnables[i] = new RectificationThread(i,image, output_image, pixels_per_thread, pixels_per_thread * i);
        }
        System.out.println("leftover " + leftover);
        // if image length is not a multiple of number of threads there will be some leftover bits
        if (leftover > 0){
            runnables[0].length += leftover;
            for (int i = 1; i < number_of_threads; i++) {
                runnables[i].length_offset += leftover;
            }
        }
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
    }

    private class RectificationThread implements Runnable{
        private int thread_id;
        private final int BYTES_PER_PIXEL = 4;
        protected BufferedImage image_buffer, output_buffer;
        protected int length, length_offset;
        public RectificationThread(int thread_id, BufferedImage image_buffer, BufferedImage output_buffer, int length, int length_offset){
            this.thread_id = thread_id;
            this.output_buffer = output_buffer;
            this.image_buffer = image_buffer;
            this.length = length;
            this.length_offset = length_offset;
        }
        public void run(){
            // pixel is 32bits, 8 bits for each channel (BYTES_PER_PIXEL channels: RGBA)
            // rectify RGB but not A
            byte[] bytes = new byte[12];
            ByteBuffer buffer;
            buffer = ByteBuffer.wrap(bytes);
            int i,j,pixel_rgba = 0;
            int max_index = image_buffer.getHeight() * image_buffer.getWidth();
            for (int index = length_offset; index < (length_offset + length) && index < max_index; index ++) {
                i = get_2d(index, image_buffer.getWidth(), 1);
                j = get_2d(index, image_buffer.getWidth(), 0);
                //System.out.println("Thread" + this.thread_id + ": doing index " + i + ", " + j);
                try {
                    pixel_rgba = image_buffer.getRGB(i, j);
                } catch (Exception e){
                    System.out.println("error at index " + index + " [" + i + "," + j + "]; " + image_buffer.getWidth());
                    e.printStackTrace();
                    System.exit(-1);
                }
                buffer.putInt(0, pixel_rgba);
                int val;
                for (int rgba_index = 0; rgba_index < BYTES_PER_PIXEL; rgba_index++){
                    if (rgba_index != 0) {
                        val = getUnsignedByte(buffer.get(rgba_index));
                        val -= 127;
                        val = (val >= 0) ? val : 0;
                        val += 127;
                    } else {
                        val = 255;
                    }
                    buffer.put(rgba_index, (byte) val);
                }
                val = buffer.getInt(0);
                output_buffer.setRGB(i,j,val);
            }
        }
    }

    public int getUnsignedByte(byte b){
        return ((b & 0b10000000) == 0b10000000) ? (b+255) : b;
    }
}