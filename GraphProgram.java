
// CSC 445 Graph Assignment 3
// Gavin Hopkins

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Scanner;

public class GraphProgram {

    public static void main(String[] args) throws Exception {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        Scanner scanner = new Scanner(file);
        scanner.useDelimiter("[,\\s]+");

        int numPoints = scanner.nextInt();
        System.out.println("Number of points: " + numPoints);
        Point2D.Double[] points = new Point2D.Double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            double x = scanner.nextDouble();
            double y = scanner.nextDouble();
            points[i] = new Point2D.Double(x, y);
            System.out.println(points[i]);
        }

        int numEdges = 0;
        if (scanner.hasNextInt()) {
            numEdges = scanner.nextInt();
        }
        System.out.println("Number of edges: " + numEdges);
        int[][] edges = new int[numEdges][2];
        System.out.println("Connected Points: ");
        for (int i = 0; i < numEdges; i++) {
            int node1 = scanner.nextInt();
            int node2 = scanner.nextInt();
            edges[i][0] = node1;
            edges[i][1] = node2;
            System.out.println("(" + node1 + ", " + node2 + ")");
        }

        PointPanel panel = new PointPanel(points, edges);
        JFrame frame = new JFrame();
        frame.add(panel);

        frame.setTitle("CSC 445 Assignment 3");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}