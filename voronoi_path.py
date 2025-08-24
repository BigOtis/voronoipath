import argparse
import logging
import cv2
import numpy as np
from skimage.morphology import medial_axis
import networkx as nx
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
from matplotlib.widgets import Button, Slider


def load_and_segment(path: str):
    """Load an image and segment foreground obstacles using HSV thresholding.

    Parameters
    ----------
    path : str
        Path to input image.

    Returns
    -------
    orig : np.ndarray
        Original BGR image.
    binary : np.ndarray
        Binary mask where obstacles are 1 and free space is 0.
    vis : np.ndarray
        RGB image visualizing flood-filled objects.
    num_objects : int
        Number of detected objects.
    """
    logging.info("Loading image %s", path)
    orig = cv2.imread(path)
    if orig is None:
        raise FileNotFoundError(path)

    hsv = cv2.cvtColor(orig, cv2.COLOR_BGR2HSV)
    # Background assumed to be near-white: low saturation and high value
    background = cv2.inRange(hsv, (0, 0, 200), (180, 30, 255))
    foreground = cv2.bitwise_not(background)
    kernel = np.ones((3, 3), np.uint8)
    foreground = cv2.morphologyEx(foreground, cv2.MORPH_OPEN, kernel, iterations=1)

    num_labels, labels = cv2.connectedComponents(foreground)
    num_objects = num_labels - 1
    logging.info("Detected %d objects", num_objects)

    rng = np.random.default_rng(0)
    colors = rng.integers(0, 255, size=(num_labels, 3), dtype=np.uint8)
    color_map = colors[labels]
    rgb = cv2.cvtColor(orig, cv2.COLOR_BGR2RGB)
    vis = cv2.addWeighted(rgb, 0.3, color_map, 0.7, 0)
    for i in range(1, num_labels):
        ys, xs = np.where(labels == i)
        x0, x1 = xs.min(), xs.max()
        y0, y1 = ys.min(), ys.max()
        cv2.rectangle(vis, (x0, y0), (x1, y1), (255, 255, 255), 1)

    binary = (foreground > 0).astype(np.uint8)
    return orig, binary, vis, num_objects


def compute_voronoi(binary: np.ndarray):
    """Compute Voronoi skeleton of free space using medial axis."""
    logging.info("Computing Voronoi skeleton")
    free_space = binary == 0
    skeleton, dist = medial_axis(free_space, return_distance=True)
    logging.info("Skeleton extracted with %d pixels", int(np.sum(skeleton)))
    return skeleton, dist


def build_graph(skeleton: np.ndarray) -> nx.Graph:
    """Build an adjacency graph from skeleton pixels."""
    logging.info("Building graph from skeleton")
    g = nx.Graph()
    coords = np.column_stack(np.nonzero(skeleton))  # (y, x)
    total = len(coords)
    for idx, (y, x) in enumerate(coords):
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
        if (idx + 1) % 1000 == 0 or idx + 1 == total:
            logging.info("Processed %d/%d skeleton pixels", idx + 1, total)
    logging.info(
        "Graph built with %d nodes and %d edges",
        g.number_of_nodes(),
        g.number_of_edges(),
    )
    return g


class VoronoiNavigator:
    """Interactive viewer allowing users to click two points and see shortest path."""

    def __init__(self, visual, skeleton, graph):
        self.image = visual
        self.skeleton = skeleton
        self.graph = graph
        self.fig, self.ax = plt.subplots()
        self.ax.imshow(visual)
        self.ax.imshow(skeleton, cmap="gray", alpha=0.6)
        plt.subplots_adjust(bottom=0.15)
        self.clicks = []
        self.path = []
        self.path_line = None
        self.path_dot = None
        self.animation = None
        self.fig.canvas.mpl_connect("button_press_event", self.on_click)

        # Controls
        axplay = self.fig.add_axes([0.82, 0.02, 0.1, 0.05])
        self.play_btn = Button(axplay, "Play")
        self.play_btn.on_clicked(self.play_path)
        axspeed = self.fig.add_axes([0.25, 0.04, 0.5, 0.03])
        self.speed_slider = Slider(axspeed, "Speed", 10, 200, valinit=30, valstep=1)

    def on_click(self, event):
        if event.inaxes != self.ax:
            return
        x, y = int(event.xdata), int(event.ydata)
        node = self.nearest_node((y, x))
        self.ax.plot(node[1], node[0], "ro" if not self.clicks else "go")
        self.clicks.append(node)
        self.fig.canvas.draw()
        if len(self.clicks) == 2:
            self.compute_path()

    def compute_path(self):
        start, goal = self.clicks
        logging.info("Computing path from %s to %s", start, goal)
        self.path = nx.shortest_path(self.graph, start, goal, weight="weight")
        logging.info("Path has %d nodes", len(self.path))
        self.clicks.clear()
        self.play_path()

    def play_path(self, event=None):
        if not self.path:
            return
        xs = [p[1] for p in self.path]
        ys = [p[0] for p in self.path]
        if self.path_line:
            self.path_line.remove()
        if self.path_dot:
            self.path_dot.remove()
        self.path_line, = self.ax.plot([], [], color="cyan", linewidth=2)
        self.path_dot, = self.ax.plot([], [], "mo", markersize=5)
        interval = self.speed_slider.val
        if self.animation:
            self.animation.event_source.stop()

        def update(i):
            self.path_line.set_data(xs[: i + 1], ys[: i + 1])
            # set_data expects sequences; wrap coordinates in lists for single point
            self.path_dot.set_data([xs[i]], [ys[i]])
            return self.path_line, self.path_dot

        self.animation = FuncAnimation(
            self.fig,
            update,
            frames=len(xs),
            interval=interval,
            blit=True,
            repeat=False,
        )
        self.fig.canvas.draw()

    def nearest_node(self, point):
        coords = np.array(list(self.graph.nodes))
        dists = np.sum((coords - np.array(point)) ** 2, axis=1)
        return tuple(coords[np.argmin(dists)])

    def show(self):
        plt.show()


def main(image_path: str, no_display: bool = False):
    logging.basicConfig(level=logging.INFO, format="%(levelname)s:%(message)s")
    _orig, binary, vis, num_objects = load_and_segment(image_path)
    skeleton, _ = compute_voronoi(binary)
    graph = build_graph(skeleton)
    if no_display:
        logging.info(
            "objects: %d nodes: %d edges: %d",
            num_objects,
            graph.number_of_nodes(),
            graph.number_of_edges(),
        )
        return
    viewer = VoronoiNavigator(vis, skeleton, graph)
    viewer.ax.set_title(f"{num_objects} objects detected")
    viewer.show()


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Voronoi path planner")
    parser.add_argument("image", help="Input image path")
    parser.add_argument(
        "--no-display", action="store_true", help="Run without interactive viewer"
    )
    args = parser.parse_args()
    main(args.image, args.no_display)
