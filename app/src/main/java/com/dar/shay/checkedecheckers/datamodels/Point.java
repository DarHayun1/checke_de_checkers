package com.dar.shay.checkedecheckers.datamodels;

import java.util.ArrayList;

public class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    ArrayList<Point> getOptionalPoints() {
        ArrayList<Point> list = new ArrayList<>();

        list.add(TopRight());
        list.add(TopLeft());
        list.add(BottomRight());
        list.add(BottomLeft());

        list.add(list.get(0).TopRight());
        list.add(list.get(1).TopLeft());
        list.add(list.get(2).BottomRight());
        list.add(list.get(3).BottomLeft());

        return list;

    }


    Point TopRight() {
        return new Point(x + 1, y - 1);
    }

    Point TopLeft() {
        return new Point(x - 1, y - 1);
    }

    Point BottomRight() {
        return new Point(x + 1, y + 1);
    }

    Point BottomLeft() {
        return new Point(x - 1, y + 1);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}