import argparse
import cv2
import numpy as np
from rembg import remove
from skimage.morphology import medial_axis
import networkx as nx
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation


def load_and_segment(path: str):
    """Load an image and isolate foreground objects with background removal.

    Parameters
    ----------
    path : str
        Path to input image.

    Returns
    -------
    orig : np.ndarray
        Original BGR image.
    binary : np.ndarray
        Binary image where foreground objects are 1 and free space is 0.
    """
    orig = cv2.imread(path)
    if orig is None:
        raise FileNotFoundError(path)

    # rembg expects RGB input and returns an array with alpha channel
    rgb = cv2.cvtColor(orig, cv2.COLOR_BGR2RGB)
    rgba = remove(rgb)

    # Extract alpha channel as object mask
    if rgba.shape[2] == 4:
        alpha = rgba[:, :, 3]
    else:
        alpha = cv2.cvtColor(rgba, cv2.COLOR_RGB2GRAY)

    mask = (alpha > 0).astype(np.uint8)
    kernel = np.ones((3, 3), np.uint8)
    binary = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, kernel)
    return orig, binary


def compute_voronoi(binary: np.ndarray):
    """Compute Voronoi skeleton of free space using medial axis."""
    free_space = binary == 0
    skeleton, dist = medial_axis(free_space, return_distance=True)
    return skeleton, dist


def build_graph(skeleton: np.ndarray) -> nx.Graph:
    """Build an adjacency graph from skeleton pixels."""
    g = nx.Graph()
    coords = np.column_stack(np.nonzero(skeleton))  # (y, x)
    for y, x in coords:
        g.add_node((y, x))
        for dy in (-1, 0, 1):
            for dx in (-1, 0, 1):
                if dy == 0 and dx == 0:
                    continue
                ny, nx_ = y + dy, x + dx
                if (
                    0 <= ny < skeleton.shape[0]
                    and 0 <= nx_ < skeleton.shape[1]
                    and skeleton[ny, nx_]
                ):
                    g.add_edge((y, x), (ny, nx_), weight=float(np.hypot(dy, dx)))
    return g


class VoronoiNavigator:
    """Interactive viewer allowing users to click two points and see shortest path."""

    def __init__(self, image, skeleton, graph):
        self.image = image
        self.skeleton = skeleton
        self.graph = graph
        self.fig, self.ax = plt.subplots()
        self.ax.imshow(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))
        self.ax.imshow(skeleton, cmap="gray", alpha=0.6)
        self.clicks = []
        self.path_line = None
        self.fig.canvas.mpl_connect("button_press_event", self.on_click)

    def on_click(self, event):
        if event.inaxes != self.ax:
            return
        x, y = int(event.xdata), int(event.ydata)
        self.ax.plot(x, y, "ro" if not self.clicks else "go")
        self.clicks.append((y, x))  # store as (row, col)
        self.fig.canvas.draw()
        if len(self.clicks) == 2:
            self.draw_path()

    def draw_path(self):
        start = self.nearest_node(self.clicks[0])
        goal = self.nearest_node(self.clicks[1])
        path = nx.shortest_path(self.graph, start, goal, weight="weight")
        xs = [p[1] for p in path]
        ys = [p[0] for p in path]
        self.path_line, = self.ax.plot([], [], "r-", linewidth=2)

        def update(i):
            self.path_line.set_data(xs[: i + 1], ys[: i + 1])
            return (self.path_line,)

        FuncAnimation(self.fig, update, frames=len(path), interval=30, blit=True, repeat=False)
        self.fig.canvas.draw()
        self.clicks.clear()

    def nearest_node(self, point):
        coords = np.array(list(self.graph.nodes))
        dists = np.sum((coords - np.array(point)) ** 2, axis=1)
        return tuple(coords[np.argmin(dists)])

    def show(self):
        plt.show()


def main(image_path: str, no_display: bool = False):
    image, binary = load_and_segment(image_path)
    skeleton, _ = compute_voronoi(binary)
    graph = build_graph(skeleton)
    if no_display:
        print(f"nodes: {graph.number_of_nodes()} edges: {graph.number_of_edges()}")
        return
    viewer = VoronoiNavigator(image, skeleton, graph)
    viewer.show()


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Voronoi path planner")
    parser.add_argument("image", help="Input image path")
    parser.add_argument(
        "--no-display", action="store_true", help="Run without interactive viewer"
    )
    args = parser.parse_args()
    main(args.image, args.no_display)
