package cn.travellerr.aronaTools.components;

import cn.travellerr.aronaTools.shareTools.GFont;
import cn.travellerr.aronaTools.shareTools.Log;
import net.mamoe.mirai.message.data.ForwardMessage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ForwardMessageTransformer {

    private static final int MAX_WIDTH = 800;

    public static BufferedImage drawForwardMessage(ForwardMessage forwardMessage) {

        Font font = GFont.font.deriveFont(Font.PLAIN, 30);
        int[] dimensions = calculateCanvasDimensions(font, forwardMessage);

        BufferedImage canvas = new BufferedImage(dimensions[0]+5, dimensions[1]+10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvas.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.BLACK); // 设置背景颜色为黑色
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); // 填充背景

        g2d.setFont(font);
        g2d.setColor(Color.white);

        int y = g2d.getFontMetrics().getHeight();


        for (ForwardMessage.INode node : forwardMessage.getNodeList()) {
            String content = node.getMessageChain().contentToString();
            int lineY = y;
            for (String line : content.split("\n")) {
                if (g2d.getFontMetrics().stringWidth(line) > canvas.getWidth() - 10) {
                    // 如果内容超出画板宽度，则换行
                    StringBuilder newLine = new StringBuilder();
                    for (char ch : line.toCharArray()) {
                        if (g2d.getFontMetrics().stringWidth(newLine.toString() + ch) > canvas.getWidth() - 10) {
                            g2d.drawString(newLine.toString(), 5, lineY);
                            lineY += g2d.getFontMetrics().getHeight();
                            newLine = new StringBuilder(String.valueOf(ch));
                        } else {
                            newLine.append(ch);
                        }
                    }
                    g2d.drawString(newLine.toString(), 5, lineY);
                } else {
                    g2d.drawString(line, 5, lineY);
                }
                lineY += g2d.getFontMetrics().getHeight();
            }


            lineY += g2d.getFontMetrics().getHeight();

            y += lineY - y;
        }

        g2d.dispose();

        try {
            canvas = compressPngImageUntilSize(canvas, 1024 * 1024);
        } catch (IOException e) {
            Log.error("图片压缩失败", e);
        }
        return canvas;
    }

    private static BufferedImage compressPngImageUntilSize(BufferedImage image, int maxSize) throws IOException {
        float quality = 1.0f;
        ByteArrayOutputStream compressedOutputStream;

        do {
            compressedOutputStream = new ByteArrayOutputStream();
            ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
            ImageWriteParam param = writer.getDefaultWriteParam();

            try (MemoryCacheImageOutputStream outputStream = new MemoryCacheImageOutputStream(compressedOutputStream)) {
                writer.setOutput(outputStream);
                writer.write(null, new IIOImage(image, null, null), param);
            }
            writer.dispose();

            quality -= 0.1f; // Decrease quality for next iteration if needed
        } while (compressedOutputStream.size() > maxSize && quality > 0);

        return ImageIO.read(new ByteArrayInputStream(compressedOutputStream.toByteArray()));
    }

    private static int[] calculateCanvasDimensions(Font font, ForwardMessage forwardMessage) {
        int height = 0;
        int maxWidth = 0;
        BufferedImage tempCanvas = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempCanvas.createGraphics();
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();

        for (ForwardMessage.INode node : forwardMessage.getNodeList()) {
            String content = node.getMessageChain().contentToString();
            for (String line : content.split("\n")) {
                int lineWidth = metrics.stringWidth(line);
                if (lineWidth > MAX_WIDTH) {
                    height += wrapText(metrics, line);
                    maxWidth = MAX_WIDTH;
                } else {
                    height += metrics.getHeight();
                    maxWidth = Math.max(maxWidth, lineWidth);
                }
            }
            height += metrics.getHeight();
        }
        g2d.dispose();
        return new int[]{MAX_WIDTH, height};
    }

    private static int wrapText(FontMetrics metrics, String line) {
        int height = 0;
        StringBuilder newLine = new StringBuilder();
        for (char ch : line.toCharArray()) {
            if (metrics.stringWidth(newLine.toString() + ch) > MAX_WIDTH) {
                height += metrics.getHeight();
                newLine = new StringBuilder(String.valueOf(ch));
            } else {
                newLine.append(ch);
            }
        }
        return height + metrics.getHeight();
    }
}
