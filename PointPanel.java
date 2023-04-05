
// CSC 445 Graph Assignment 3
// Gavin Hopkins

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

class PointPanel extends JPanel {
    private final Point2D.Double[] points;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private final int[][] edges;
    private Point[] scaledPoints;
    private int startPoint = -1;
    private int endPoint = -1;
    private final LinkedList<Object> shortestPath = new LinkedList<>();
    private final LinkedList<Object> shortestPathEdges = new LinkedList<>();

    public PointPanel(Point2D.Double[] points, int[][] edges) {
        this.points = points;
        this.edges = edges;

        MouseListener mouseListener = new MouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);

        minX = points[0].getX();
        maxX = points[0].getX();
        minY = points[0].getY();
        maxY = points[0].getY();
        for (int i = 1; i < points.length; i++) {
            Point2D.Double p = points[i];
            if (p.getX() > maxX)
                maxX = p.getX();
            if (p.getX() < minX)
                minX = p.getX();
            if (p.getY() > maxY)
                maxY = p.getY();
            if (p.getY() < minY)
                minY = p.getY();
        }
        System.out.println("minX: " + minX);
        System.out.println("maxX: " + maxX);
        System.out.println("minY: " + minY);
        System.out.println("maxY: " + maxY);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        final int MARGIN = 15;
        double width = getSize().getWidth() - 2 * MARGIN;
        double height = getSize().getHeight() - 2 * MARGIN;
        int pointSize = 20;
        scaledPoints = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            double percentWidth = (points[i].getX() - minX) / (maxX - minX);
            int relativeX = (int) (percentWidth * width + MARGIN);
            double percentHeight = (points[i].getY() - minY) / (maxY - minY);
            int relativeY = (int) (height - percentHeight * height + MARGIN);
            scaledPoints[i] = new Point(relativeX, relativeY);

            if (startPoint == i) {
                g2d.setColor(Color.GREEN);
            } else if (endPoint == i) {
                g2d.setColor(Color.BLUE);
            } else if(shortestPath.contains(i)) {
                g2d.setColor(Color.GREEN);
            } else {
                g2d.setColor(Color.RED);
            }
            g2d.fillOval(scaledPoints[i].x - 10, scaledPoints[i].y - 10, 20, 20);
        }

        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(labelFont);
        for (int i = 0; i < scaledPoints.length; i++) {
            Point p = scaledPoints[i];
            String label = Integer.toString(i);
            int labelWidth = fm.stringWidth(label);
            int labelHeight = fm.getAscent();
            int labelX = p.x - labelWidth / 2;
            int labelY = p.y + labelHeight / 2;
            g2d.drawString(label, labelX, labelY);
        }

        for (int[] edge : edges) {
            g2d.setColor(Color.BLUE);
            int node1 = edge[0];
            int node2 = edge[1];
            int x1 = scaledPoints[node1].x;
            int y1 = scaledPoints[node1].y;
            int x2 = scaledPoints[node2].x;
            int y2 = scaledPoints[node2].y;
            double dx = (x2 - x1);
            double dy = (y2 - y1);
            double d = Math.sqrt(dx * dx + dy * dy);
            double offSetX = (dx / d) * pointSize / 2;
            double offSetY = (dy / d) * pointSize / 2;
            g2d.draw(new Line2D.Double(x1 + offSetX, y1 + offSetY, x2 - offSetX, y2 - offSetY));
        }

        g2d.setColor(Color.GREEN);
        for (Object shortestPathEdge : shortestPathEdges) {
            int[] edge = (int[]) shortestPathEdge;
            int node1 = edge[0];
            int node2 = edge[1];
            int x1 = scaledPoints[node1].x;
            int y1 = scaledPoints[node1].y;
            int x2 = scaledPoints[node2].x;
            int y2 = scaledPoints[node2].y;
            double dx = (x2 - x1);
            double dy = (y2 - y1);
            double d = Math.sqrt(dx * dx + dy * dy);
            double offSetX = (dx / d) * pointSize / 2;
            double offSetY = (dy / d) * pointSize / 2;
            g2d.draw(new Line2D.Double(x1 + offSetX, y1 + offSetY, x2 - offSetX, y2 - offSetY));
        }
    }

    public Point[] getScaledPoints() {
        return scaledPoints;
    }

    public void dijkstra(int startNode, int endNode) {
        ArrayList<Integer> adjacentEdges = new ArrayList<>();
        for (int[] edge : edges) {
            if (edge[0] == startNode || edge[1] == startNode) {
                if (edge[0] == startNode) {
                    adjacentEdges.add(edge[1]);
                } else {
                    adjacentEdges.add(edge[0]);
                }
            }
        }
        if (adjacentEdges.isEmpty()) {
            System.out.println("Point " + startNode + " has no connecting edges");
            return;
        }
        adjacentEdges.clear();
        for (int[] edge : edges) {
            if (edge[0] == endNode || edge[1] == endNode) {
                if (edge[0] == endNode) {
                    adjacentEdges.add(edge[1]);
                } else {
                    adjacentEdges.add(edge[0]);
                }
            }
        }
        if (adjacentEdges.isEmpty()) {
            System.out.println("Point " + endNode + " has no connecting edges");
            return;
        }

        double[] distance = new double[points.length];
        int[] previous = new int[points.length];
        Arrays.fill(previous, -1);
        Arrays.fill(distance, Double.MAX_VALUE);
        distance[startNode] = 0.0;

        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> distance[n]));
        pq.add(startNode);

        boolean foundEndNode = false;
        while (!pq.isEmpty() && !foundEndNode) {
            int currentNode = pq.poll();
            if (currentNode == endNode) {
                foundEndNode = true;
            }
            for (int[] edge : edges) {
                int node1 = edge[0];
                int node2 = edge[1];
                if (node1 == currentNode) {
                    double newDistance = distance[currentNode] + Math.sqrt(Math.pow(
                            points[node2].x - points[node1].x, 2)
                            + Math.pow(points[node2].y - points[node1].y, 2));
                    if (newDistance < distance[node2]) {
                        distance[node2] = newDistance;
                        previous[node2] = node1;
                        pq.add(node2);
                    }
                } else if (node2 == currentNode) {
                    double newDistance = distance[currentNode] + Math.sqrt(Math.pow(
                            points[node2].x - points[node1].x, 2)
                            + Math.pow(points[node2].y - points[node1].y, 2));
                    if (newDistance < distance[node1]) {
                        distance[node1] = newDistance;
                        previous[node1] = node2;
                        pq.add(node1);
                    }
                }
            }
        }
        shortestPath.clear();
        shortestPathEdges.clear();
        int currentNode = endNode;
        while (currentNode != startNode) {
            shortestPath.addFirst(currentNode);
            int previousNode = previous[currentNode];
            shortestPathEdges.addFirst(new int[]{previousNode, currentNode});
            currentNode = previousNode;
        }
        shortestPath.addFirst(startNode);
        System.out.println("Shortest path from point " + startNode + " to point " + endNode + ": " + shortestPath);
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            final double THRESHOLD = 10.0;
            if (e.getButton() == MouseEvent.BUTTON1) {
                for (int i = 0; i < getScaledPoints().length; i++) {
                    Point p = getScaledPoints()[i];
                    double dist = Point2D.distance(x, y, p.x, p.y);
                    if (dist < THRESHOLD) {
                        startPoint = i;
                        System.out.println("Clicked on start point " + i + " at (" + points[i].getX() + ", " + points[i].getY() + ")");
                    }
                }
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                for (int i = 0; i < getScaledPoints().length; i++) {
                    Point p = getScaledPoints()[i];
                    double dist = Point2D.distance(x, y, p.x, p.y);
                    if (dist < THRESHOLD) {
                        endPoint = i;
                        System.out.println("Clicked on end point " + i + " at (" + points[i].getX() + ", " + points[i].getY() + ")");
                    }
                }
            }
            if (startPoint != -1 && endPoint != -1) {
                dijkstra(startPoint, endPoint);
            } else {
                System.out.println("Point has no edges");
            }
            repaint();
        }
    }
}