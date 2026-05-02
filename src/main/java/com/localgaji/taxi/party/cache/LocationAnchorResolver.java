package com.localgaji.taxi.party.cache;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
public class LocationAnchorResolver {

    private static final double CELL_SIZE = 0.002;  // 약 200m

    public String resolve(Point point) {
        int latCell = (int) (point.getY() / CELL_SIZE);
        int lonCell = (int) (point.getX() / CELL_SIZE);
        return latCell + ":" + lonCell;
    }
}