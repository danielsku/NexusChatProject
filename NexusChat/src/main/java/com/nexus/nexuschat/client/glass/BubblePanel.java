package com.nexus.nexuschat.client.glass;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class BubblePanel extends JPanel{
    private Color backgroundColor;
    private float transparency = 0.125f;
    private int cornerRadius = 20;

    public BubblePanel(Color bgColor) {
        this.backgroundColor = bgColor;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Clip to rounded rectangle so nothing draws outside
        RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(
                0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius
        );
        g2.setClip(roundRect);

        // Draw semi-transparent background (simulate glass)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
        g2.setColor(backgroundColor);
        g2.fill(roundRect);

        // Optional border for glass effect
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(roundRect);

        g2.dispose();
    }
}
