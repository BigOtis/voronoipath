## Generalized Voronoi Diagrams for Path Planning

A simple discrete approach to creating generalized 2D Voronoi diagrams for object avoidance and path planning. To see some example output visit http://philglopez.com/voronoi

## Algorithm Pipeline/Overview

Given an input image:

1. Using MATLAB, convert the RGB image to a binary image, filter it and perform morphological closing on it to remove noise. 
   Use "bwconncomp" to label individual pixel clusters that make up the objects in the image.
   
2. Export a list of shapes and the pixels that belong to them to a data file.

3. Using Java, read the input shapes and perform a simultaneous flood fill using the oixel outlines of each shape as a starting point.

   i. When two flood waves from different shapes reach each other, define these pixels as an edge. Store the edges for later use.
  
   ii. Continue flooding until all pixels have been reached by the flood.
  
4. Display the Voronoi diagram with each region assigned to a random color. Display the shapes these regions surround with their
    original pixel colorings. 
    
   i. The user can now use their mouse to select two edges on the diagram. 
   
   ii. Once two edges are selected, the shortest path between the two edges will be computed using A*. 
   
   iii. A simulated robot will then demonstrate the traversal path in an animation.

## Flood Fill Algorithm

The flood fill algorithm runs given input shapes and the pixels that belong to the shapes. For the purpose of redrawing the original
object at a shapes location, the MATLAB script contained in this project outputs each shapes pixel coordinates along with the RGB value associated with them.

The flood fill algorithm is located in the VoronoiMapper.java class and is the main driver for the flood fill algorithm. Below is a pseudocode explaination of that code.

Given a queue of pixels for each shape, "shapes" (initially the pixels that make up shape itself)

* while the shapes still have pixels in their list:
   * for each shape
      * create a new empty queue of pixels for the shape
      * for each pixel in shape's list
         * flood this pixel to it's neighbors 
         * add any neighbors that were previously unvisited to the new queue for this shape
      * now replace the old queue for this shape with the new queue     
      
The "flood pixel" step above takes place in the Pixel.java "grow" method. Because we are using the Manhattan distance metric here, the Pixel vists it's left, right, top and bottom neighbors. 

* If any neighbor is already a pixel that belongs to the same shape as the visiting pixel, nothing happens.
* If the neighbor belongs to a different shape, then we mark is as an edge. This means it is in the middle of two shapes.
* If the neighbor is an unvisited pixel, then is claimed by the visitor and belongs to that shapes region of control. It is added back to the shapes queue for the next flood stage. 

This algorithm performs the "flood this pixel" stage one time for every pixel. Because of this, we can quickly generate the Generalized Voronoi Diagram for an image having only to visit each pixel once and then examine it's neighbors one time. 
