package com.nexus.nexuschat.client.glass;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FixedEdgeResizeListener extends MouseAdapter {
    private final JFrame frame;
    private boolean resizing = false;
    private Point startPos;
    private Rectangle startBounds;
    private int cursorType = Cursor.DEFAULT_CURSOR;

    private final int BORDER = 8;

    public FixedEdgeResizeListener(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateCursor(e.getPoint());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (cursorType != Cursor.DEFAULT_CURSOR) {
            resizing = true;
            startPos = e.getLocationOnScreen();
            startBounds = frame.getBounds();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!resizing) return;

        Point curr = e.getLocationOnScreen();
        int dx = curr.x - startPos.x;
        int dy = curr.y - startPos.y;

        int newX = startBounds.x;
        int newY = startBounds.y;
        int newWidth = startBounds.width;
        int newHeight = startBounds.height;

        switch (cursorType) {
            case Cursor.N_RESIZE_CURSOR:
                newHeight = startBounds.height - dy;
                newY = startBounds.y + (startBounds.height - newHeight);
                break;
            case Cursor.S_RESIZE_CURSOR:
                newHeight = startBounds.height + dy;
                break;
            case Cursor.W_RESIZE_CURSOR:
                newWidth = startBounds.width - dx;
                newX = startBounds.x + (startBounds.width - newWidth);
                break;
            case Cursor.E_RESIZE_CURSOR:
                newWidth = startBounds.width + dx;
                break;
            case Cursor.NW_RESIZE_CURSOR:
                newWidth = startBounds.width - dx;
                newX = startBounds.x + (startBounds.width - newWidth);
                newHeight = startBounds.height - dy;
                newY = startBounds.y + (startBounds.height - newHeight);
                break;
            case Cursor.NE_RESIZE_CURSOR:
                newWidth = startBounds.width + dx;
                newHeight = startBounds.height - dy;
                newY = startBounds.y + (startBounds.height - newHeight);
                break;
            case Cursor.SW_RESIZE_CURSOR:
                newWidth = startBounds.width - dx;
                newX = startBounds.x + (startBounds.width - newWidth);
                newHeight = startBounds.height + dy;
                break;
            case Cursor.SE_RESIZE_CURSOR:
                newWidth = startBounds.width + dx;
                newHeight = startBounds.height + dy;
                break;
        }

        // Enforce minimum size
        int minWidth = 200;
        int minHeight = 150;

        if (newWidth < minWidth) {
            if (cursorType == Cursor.W_RESIZE_CURSOR || cursorType == Cursor.NW_RESIZE_CURSOR || cursorType == Cursor.SW_RESIZE_CURSOR)
                newX -= (minWidth - newWidth);
            newWidth = minWidth;
        }

        if (newHeight < minHeight) {
            if (cursorType == Cursor.N_RESIZE_CURSOR || cursorType == Cursor.NW_RESIZE_CURSOR || cursorType == Cursor.NE_RESIZE_CURSOR)
                newY -= (minHeight - newHeight);
            newHeight = minHeight;
        }

        frame.setBounds(newX, newY, newWidth, newHeight);
        frame.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        resizing = false;
    }

    private void updateCursor(Point p) {
        int w = frame.getWidth();
        int h = frame.getHeight();
        boolean left = p.x < BORDER;
        boolean right = p.x >= w - BORDER;
        boolean top = p.y < BORDER;
        boolean bottom = p.y >= h - BORDER;

        if (top && left) cursorType = Cursor.NW_RESIZE_CURSOR;
        else if (top && right) cursorType = Cursor.NE_RESIZE_CURSOR;
        else if (bottom && left) cursorType = Cursor.SW_RESIZE_CURSOR;
        else if (bottom && right) cursorType = Cursor.SE_RESIZE_CURSOR;
        else if (top) cursorType = Cursor.N_RESIZE_CURSOR;
        else if (bottom) cursorType = Cursor.S_RESIZE_CURSOR;
        else if (left) cursorType = Cursor.W_RESIZE_CURSOR;
        else if (right) cursorType = Cursor.E_RESIZE_CURSOR;
        else cursorType = Cursor.DEFAULT_CURSOR;

        frame.setCursor(Cursor.getPredefinedCursor(cursorType));
    }
}