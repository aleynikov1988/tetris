package com.example.tetris.models;

import android.graphics.Color;
import android.graphics.Point;
import androidx.annotation.NonNull;
import com.example.tetris.constants.FieldConstants;
import java.util.Random;

public class Block {
    private int shapeIndex;
    private int frameNumber;
    private BlockColor color;
    private Point position;

    public Shape getShape() {
        return Shape.values()[this.shapeIndex];
    }

    @NonNull
    public final byte[][] getShape(int frameNumber) {
        return Shape.values()[this.shapeIndex].getFrame(frameNumber).as2dByteArray();
    }

    public int getFrameNumber() {
        return this.frameNumber;
    }

    public int getFrameCount() {
        return Shape.values()[this.shapeIndex].getFrameCount();
    }

    public BlockColor getColor() {
        return this.color;
    }

    public int getColorRGB() {
        return this.color.rgbValue;
    }

    public static int getColorRGB(byte value) {
        for (BlockColor color : BlockColor.values()) {
            if (color.byteValue == value) {
                return color.rgbValue;
            }
        }
        return -1;
    }

    public byte getColorByte() {
        return this.color.byteValue;
    }

    public Point getPosition() {
        return this.position;
    }

    private Block(int shapeIndex, BlockColor color) {
        this.shapeIndex = shapeIndex;
        this.frameNumber = 0;
        this.color = color;
        this.position = new Point(FieldConstants.COLUMN_COUNT.getValue() / 2, 0);
    }

    public static Block createBlock() {
        Random random = new Random();
        int shapeIndex = random.nextInt(Shape.values().length);
        BlockColor color = BlockColor.values()[random.nextInt(BlockColor.values().length)];
        Block block = new Block(shapeIndex, color);

        block.position.x -= Shape.values()[shapeIndex].getStartPosition();
        return block;
    }

    public enum BlockColor {
        PINK(Color.rgb(255, 105, 180), (byte) 2),
        GREEN(Color.rgb(0, 128, 0), (byte) 3),
        ORANGE(Color.rgb(255, 140, 0), (byte) 4),
        YELLOW(Color.rgb(255, 255, 0), (byte) 5),
        CYAN(Color.rgb(0, 255, 255), (byte) 6);

        private final int rgbValue;
        private final byte byteValue;

        BlockColor(int rgbValue, byte byteValue) {
            this.rgbValue = rgbValue;
            this.byteValue = byteValue;
        }
    }

    public final void setState(int frameNumber, Point position) {
        this.frameNumber = frameNumber;
        this.position = position;
    }
}
