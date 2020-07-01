/*******************************************************************************
 * Copyright (C) 2019 Pheromir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.pheromir.trustedbot;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Images {

    public static BufferedImage overlay(BufferedImage background, BufferedImage overlay) {
        int width = Math.max(background.getWidth(), overlay.getWidth());
        int height = Math.max(background.getHeight(), overlay.getHeight());
        int centerX = width / 2 - overlay.getWidth() / 2;
        int centerY = height / 2 - overlay.getHeight() / 2;
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics gr = combined.getGraphics();
        gr.drawImage(background, 0, 0, null);
        gr.drawImage(overlay, centerX, centerY, null);
        gr.dispose();
        return combined;
    }

    public static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    public static BufferedImage blur(BufferedImage im) {
        int height = im.getHeight();
        int width = im.getWidth();
        // result is transposed, so the width/height are swapped
        BufferedImage temp = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        float[] k = new float[]{0.00598F, 0.060626F, 0.241843F, 0.383103F, 0.241843F, 0.060626F, 0.00598F};
        // horizontal blur, transpose result
        for (int y = 0; y < height; y++) {
            for (int x = 3; x < width - 3; x++) {
                float r = 0, g = 0, b = 0;
                for (int i = 0; i < 7; i++) {
                    int pixel = im.getRGB(x + i - 3, y);
                    b += (pixel & 0xFF) * k[i];
                    g += ((pixel >> 8) & 0xFF) * k[i];
                    r += ((pixel >> 16) & 0xFF) * k[i];
                }
                int p = (int) b + ((int) g << 8) + ((int) r << 16);
                // transpose result!
                temp.setRGB(y, x, p);
            }
        }
        return temp;
    }

}
