package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConvexHull {

    /**
     * Computes the cross product of vectors AB and AC.
     * Returns a positive value for a counter-clockwise turn,
     * negative for a clockwise turn, and zero if collinear.
     */
    private static long crossProduct(Point a, Point b, Point c) {
        return (long)(b.x - a.x) * (c.y - a.y) - (long)(b.y - a.y) * (c.x - a.x);
    }

    /**
     * Calculates the convex hull for a given array of points.
     */
    public static List<Point> computeHull(List<Point> points) {
        int n = points.size();
        if (n <= 2) {
            return points;
        }

        // 1. Sort points lexicographically by x, then by y
        Collections.sort(points, (p1, p2) -> {
            if (p1.x != p2.x) {
                return Integer.compare(p1.x, p2.x);
            }
            return Integer.compare(p1.y, p2.y);
        });

        List<Point> hull = new ArrayList<>();

        // 2. Build the lower hull
        for (int i = 0; i < n; i++) {
            while (hull.size() >= 2 && crossProduct(hull.get(hull.size() - 2), hull.get(hull.size() - 1), points.get(i)) <= 0) {
                hull.remove(hull.size() - 1);
            }
            hull.add(points.get(i));
        }

        // 3. Build the upper hull
        int lowerHullSize = hull.size();
        for (int i = n - 2; i >= 0; i--) {
            while (hull.size() > lowerHullSize && crossProduct(hull.get(hull.size() - 2), hull.get(hull.size() - 1), points.get(i)) <= 0) {
                hull.remove(hull.size() - 1);
            }
            hull.add(points.get(i));
        }

        // Remove the duplicate last point (same as the first point)
        if (hull.size() > 1) {
            hull.remove(hull.size() - 1);
        }

        return hull;
    }

}
