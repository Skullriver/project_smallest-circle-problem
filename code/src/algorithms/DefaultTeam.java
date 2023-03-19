package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import supportGUI.Circle;

public class DefaultTeam {

    // calculCercleMin: ArrayList<Point> --> Circle
    // renvoie un cercle couvrant tout point de la liste, de rayon minimum.
    public Circle calculCercleMin(ArrayList<Point> points) {
        if (points.isEmpty()) {
            return null;
        }
        //return naiveMinCircle(points);
        return WelzlMinCircle(points, new ArrayList<Point>());
    }

    /**
     * Naive Algorithm for computing the minimum circle covering a given set of points in the plane
     *
     * @param inputPoints : Set of points in 2D
     * @return the minimum circle covering a given set of points (naive computing way)
     */
    @SuppressWarnings("unchecked")
    private Circle naiveMinCircle(ArrayList<Point> inputPoints) {
        ArrayList<Point> points = (ArrayList<Point>) inputPoints.clone();
        if (points.size() < 1)
            return null;

        double cX, cY, cRadiusSquared;
        for (Point p : points) {
            for (Point q : points) {
                cX = .5 * (p.x + q.x);
                cY = .5 * (p.y + q.y);
                cRadiusSquared = 0.25 * ((p.x - q.x) * (p.x - q.x) + (p.y - q.y) * (p.y - q.y));
                boolean allHit = true;
                for (Point s : points)
                    if ((s.x - cX) * (s.x - cX) + (s.y - cY) * (s.y - cY) > cRadiusSquared) {
                        allHit = false;
                        break;
                    }
                if (allHit)
                    return new Circle(new Point((int) cX, (int) cY), (int) Math.sqrt(cRadiusSquared));
            }
        }
        double resX = 0;
        double resY = 0;
        double resRadiusSquared = Double.MAX_VALUE;
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                for (int k = j + 1; k < points.size(); k++) {
                    Point p = points.get(i);
                    Point q = points.get(j);
                    Point r = points.get(k);
                    // si les trois sont colineaires, on passe
                    if ((q.x - p.x) * (r.y - p.y) - (q.y - p.y) * (r.x - p.x) == 0)
                        continue;
                    // si p et q sont sur la meme ligne, ou p et r sont sur la meme ligne, on les
                    // echange
                    if ((p.y == q.y) || (p.y == r.y)) {
                        if (p.y == q.y) {
                            p = points.get(k); // ici on est certain que p n'est sur la meme ligne de ni q ni r
                            r = points.get(i); // parce que les trois points sont non-colineaires
                        } else {
                            p = points.get(j); // ici on est certain que p n'est sur la meme ligne de ni q ni r
                            q = points.get(i); // parce que les trois points sont non-colineaires
                        }
                    }
                    // on cherche les coordonnees du cercle circonscrit du triangle pqr
                    // soit m=(p+q)/2 et n=(p+r)/2
                    double mX = .5 * (p.x + q.x);
                    double mY = .5 * (p.y + q.y);
                    double nX = .5 * (p.x + r.x);
                    double nY = .5 * (p.y + r.y);
                    // soit y=alpha1*x+beta1 l'equation de la droite passant par m et
                    // perpendiculaire a la droite (pq)
                    // soit y=alpha2*x+beta2 l'equation de la droite passant par n et
                    // perpendiculaire a la droite (pr)
                    double alpha1 = (q.x - p.x) / (double) (p.y - q.y);
                    double beta1 = mY - alpha1 * mX;
                    double alpha2 = (r.x - p.x) / (double) (p.y - r.y);
                    double beta2 = nY - alpha2 * nX;
                    // le centre c du cercle est alors le point d'intersection des deux droites
                    // ci-dessus
                    cX = (beta2 - beta1) / (double) (alpha1 - alpha2);
                    cY = alpha1 * cX + beta1;
                    cRadiusSquared = (p.x - cX) * (p.x - cX) + (p.y - cY) * (p.y - cY);
                    if (cRadiusSquared >= resRadiusSquared)
                        continue;
                    boolean allHit = true;
                    for (Point s : points)
                        if ((s.x - cX) * (s.x - cX) + (s.y - cY) * (s.y - cY) > cRadiusSquared) {
                            allHit = false;
                            break;
                        }
                    if (allHit) {
                        //System.out.println("Found r=" + Math.sqrt(cRadiusSquared));
                        resX = cX;
                        resY = cY;
                        resRadiusSquared = cRadiusSquared;
                    }
                }
            }
        }
        return new Circle(new Point((int) resX, (int) resY), (int) Math.sqrt(resRadiusSquared));
    }

    /**
     * Welzl Algorithm for computing the minimum circle covering a given set of points in the plane
     *
     * @param P : Set of input points in 2D
     * @param R : Set of points on the smallest circle (empty on launching)
     * @return the minimum circle covering a given set of points
     */
    @SuppressWarnings("unchecked")
	private Circle WelzlMinCircle(ArrayList<Point> P, ArrayList<Point> R) {
        ArrayList<Point> P_copy = (ArrayList<Point>) P.clone();
        Random rand = new Random();
        Circle D = new Circle(new Point(0, 0), 0);
        if (P_copy.isEmpty() || R.size() == 3) {
            D = boundaryMinCircle(R);
        } else {
            Point p = P_copy.get(rand.nextInt(P_copy.size()));
            P_copy.remove(p);
            D = WelzlMinCircle(P_copy, R);
            if (D != null && !contains(D, p)) {
                R.add(p);
                D = WelzlMinCircle(P_copy, R);
                R.remove(p);
            }
        }
        return D;

    }

    /**
     * Returns true if the given point is inside or on the boundary of the given circle.
     * @param c : The circle
     * @param p : a point
     * @return true or false if the given point is inside or on the boundary of the given circle
     *
     * */
    public boolean contains(Circle c, Point p) {
        if ((c != null) && (p != null)) {
            if (p.distance(c.getCenter()) <= c.getRadius())
                return true;
        }
        return false;
    }

    /**
     * Helper function for computing the minimum circle covering a given set of points in the plane
     * @param boundary : Set of points on the smallest circle (empty on launching)
     * @return the minimum circle covering a given set of points
     */
    private Circle boundaryMinCircle(ArrayList<Point> boundary) {
        if (boundary.size() == 0) {
            return new Circle(new Point(0, 0), 0);
        } else if (boundary.size() == 1) {
            return new Circle(boundary.get(0), 0);
        } else if (boundary.size() == 2) {
            return circleFrom2points(boundary.get(0), boundary.get(1));
        } else {
            return circleFrom3points(boundary.get(0), boundary.get(1), boundary.get(2));
        }
    }

    /**
     * Returns the smallest circle that passes through the two given points.
     * @param p : a point
     * @param q : a point
     * @return the smallest circle
     * */
    private Circle circleFrom2points(Point p, Point q) {
        Point center = new Point((p.x + q.x) / 2, (p.y + q.y) / 2);
        int radius = (int) center.distance(p);
        return new Circle(center, radius);
    }

    /**
     * Returns the circumscribed circle that passes through the three given points.
     * @param p : a point
     * @param q : a point
     * @param r : a point
     * @return the smallest circle
     * */
    private Circle circleFrom3points(Point p, Point q, Point r) {
        //	| x1 y1 1 |
        //	| x2 y2 1 |
        //	| x3 y3 1 |
        //
        //	Determinant = x1(y2 - y3) + x2(y3 - y1) + x3(y1 - y2)

        double det = (p.x * (q.y - r.y) + q.x * (r.y - p.y) + r.x * (p.y - q.y)) * 2;
        if (det == 0) // points are colinear, no circle can be formed
            return new Circle(new Point(0, 0), 0);

        int p_len = (p.x * p.x) + (p.y * p.y);
        int q_len = (q.x * q.x) + (q.y * q.y);
        int r_len = (r.x * r.x) + (r.y * r.y);

        double x = ((p_len * (q.y - r.y)) + (q_len * (r.y - p.y)) + (r_len * (p.y - q.y))) / det;
        double y = ((p_len * (r.x - q.x)) + (q_len * (p.x - r.x)) + (r_len * (q.x - p.x))) / det;
        Point center = new Point((int) x, (int) y);

        return new Circle(center, (int) Math.ceil(center.distance(p)));

    }

}
