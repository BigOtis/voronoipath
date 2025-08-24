# Voronoi Path Planning

This repository contains a Python-based demonstration of computing generalized Voronoi diagrams for 2‑D path planning.  The script uses OpenCV to separate foreground objects from an input image, builds a Voronoi skeleton of the free space, and lets you interactively explore shortest paths between points.

## Usage

```bash
pip install -r requirements.txt
python voronoi_path.py VoronoiShapes/blocks.JPG
```

An image window will appear. Click once to select a start location and again to select a goal.  The shortest route along the Voronoi skeleton is animated in red.

To run in a headless environment, omit the interactive window:

```bash
python voronoi_path.py VoronoiShapes/blocks.JPG --no-display
```

The script logs progress through loading, object detection, skeletonization, and graph construction stages.

## Algorithm Overview

1. Load the image and segment obstacles by thresholding in HSV space.
2. Flood fill and label connected components to highlight and count distinct objects.
3. Compute the medial axis of the free space to obtain a Voronoi skeleton.
4. Build a graph from skeleton pixels.
5. Use Dijkstra's algorithm (via NetworkX) to compute the shortest path between two selected points and animate the traversal.  Clicks are only accepted on skeleton pixels.
