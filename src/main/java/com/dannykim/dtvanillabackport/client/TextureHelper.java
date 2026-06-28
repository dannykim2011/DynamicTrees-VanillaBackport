package com.dannykim.dtvanillabackport.client;

import com.mojang.blaze3d.platform.NativeImage;

import java.util.Arrays;

final class TextureHelper {
    static final class PixelBuffer {
        int[] pixels;
        final int w;
        final int h;

        PixelBuffer(final int w, final int h) {
            this.w = w;
            this.h = h;
            this.pixels = new int[w * h];
        }

        PixelBuffer(final NativeImage image) {
            this.w = image.getWidth();
            this.h = image.getHeight();
            this.pixels = new int[this.w * this.h];
            for (int x = 0; x < this.w; x++) {
                for (int y = 0; y < this.h; y++) {
                    this.pixels[this.calcPos(x, y)] = image.getPixelRGBA(x, y);
                }
            }
        }

        PixelBuffer(final PixelBuffer other) {
            this.w = other.w;
            this.h = other.h;
            this.pixels = Arrays.copyOf(other.pixels, other.pixels.length);
        }

        NativeImage toNativeImage() {
            final NativeImage image = new NativeImage(this.w, this.h, true);
            for (int x = 0; x < this.w; x++) {
                for (int y = 0; y < this.h; y++) {
                    image.setPixelRGBA(x, y, this.getPixel(x, y));
                }
            }
            return image;
        }

        private int calcPos(final int x, final int y) {
            return y * this.w + x;
        }

        private int getPixel(final int x, final int y) {
            return x >= 0 && x < this.w && y >= 0 && y < this.h ? this.pixels[this.calcPos(x, y)] : 0;
        }

        private void setPixel(final int x, final int y, final int pixel) {
            if (x >= 0 && x < this.w && y >= 0 && y < this.h) {
                this.pixels[this.calcPos(x, y)] = pixel;
            }
        }

        void blit(final PixelBuffer destination, final int offsetX, final int offsetY) {
            this.blit(destination, offsetX, offsetY, 0);
        }

        void blit(final PixelBuffer destination, final int offsetX, final int offsetY, final int rotation) {
            switch (rotation & 3) {
                case 0 -> {
                    for (int y = 0; y < this.h; y++) for (int x = 0; x < this.w; x++)
                        destination.setPixel(x + offsetX, y + offsetY, this.getPixel(x, y));
                }
                case 1 -> {
                    for (int y = 0; y < this.h; y++) for (int x = 0; x < this.w; x++)
                        destination.setPixel(this.h - y - 1 + offsetX, x + offsetY, this.getPixel(x, y));
                }
                case 2 -> {
                    for (int y = 0; y < this.h; y++) for (int x = 0; x < this.w; x++)
                        destination.setPixel(this.w - x - 1 + offsetX, this.h - y - 1 + offsetY, this.getPixel(x, y));
                }
                case 3 -> {
                    for (int y = 0; y < this.h; y++) for (int x = 0; x < this.w; x++)
                        destination.setPixel(y + offsetX, this.w - x - 1 + offsetY, this.getPixel(x, y));
                }
            }
        }
    }
}
