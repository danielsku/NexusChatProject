package com.nexus.nexuschat.client.glass;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

public class PanelTransparent extends JPanel {

    private float transparent = 0.3f;
    private int cornerRadius = 30;
    private Color panelColour;
    private boolean setBorder;

    public PanelTransparent(LayoutManager layout) {
        super(layout);
    }

    public Color getPanelColour() {
        return panelColour;
    }

    public void setPanelColour(Color panelColour) {
        this.panelColour = panelColour;
    }

    public PanelTransparent(Color panelColour, boolean setBorder) {
        this.panelColour = panelColour;
        this.setBorder = setBorder;
        setOpaque(false);
        setBackground(panelColour);
    }

    public PanelTransparent() {
        setOpaque(false);
        setBackground(panelColour);
    }

    public float getTransparent() {
        return transparent;
    }

    public void setTransparent(float transparent) {
        this.transparent = transparent;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        // smooth edges (optional)
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // paint translucent background
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparent));
        g2.setColor(panelColour);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if(setBorder)
        {
            g2.setColor(new Color(255, 255, 255, 80)); // soft white border
            g2.setStroke(new BasicStroke(0.5f));
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);  // draw rectangle along edges
        }

        g2.dispose();
        // NO super.paintComponent(g) → prevents stacking artifacts


    }
}
