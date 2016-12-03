## Generalized Voronoi Diagrams for Path Planning

A simple discrete approach to creating generalized 2D Voronoi diagrams for object avoidance and path planning. 

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
