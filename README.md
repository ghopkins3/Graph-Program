# Graph-Program
Program that takes a text file of points and edges and outputs a graphical display of the points and edges, as well as calculating the shortest path using Djikstra's algorithm.

When executed, a file dialog box is opened and the user is prompted to select a text file.
When the user chooses a text file with the correct format, the points and edges are displayed graphically using a JPanel.
The points are displayed in red with a number denoting their order in the text file and the edges are displayed in blue.
The user can then left click a starting point and right click an ending point. 
The starting point, ending point, and shortest path are then highlighted in green and printed to the console. 

Text File format where 4 is the number of points, followed by the coordinates of the points, 3 is the number of edges, followed by the points which are connected by edges: 
4
0, 20
-5, 0
0, 0
10, 0

3
0 2
0 3
0 1
