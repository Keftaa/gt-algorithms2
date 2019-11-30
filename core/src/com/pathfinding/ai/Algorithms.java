package com.pathfinding.ai;

import java.util.Observable;
import java.util.Observer;

import javax.swing.plaf.basic.BasicTreeUI.NodeDimensionsHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class Algorithms extends Observable {
	private Main main;
	public Bellman bellman;
	public Dijkstra dijkstra;

	public Algorithms(Main main) {
		this.main = main;
		bellman = new Bellman();
		dijkstra = new Dijkstra();
	}

	class Bellman {

		public void calculate() throws NoPathFoundException {
			main.graphMan.setChanged();
			main.graphMan.notifyObservers(main.graphMan.vectors);
			for (Node n : main.graphMan.nodes) {
				if (n == main.graphMan.startNode) {
					n.component = new BellmanComponent().potential(0);
					((BellmanComponent) n.component).isTraversed = true;
				} else {
					n.component = new BellmanComponent()
							.potential(Integer.MAX_VALUE);
				}
			}

			Array<Node> nodesWithTraversedPredecessors = new Array<Node>();

			for (Node n : main.graphMan.nodes) {
				for (Vector v : n.endVectors) {
					if (!((BellmanComponent) v.startNode.component).isTraversed) {
						continue;
					}
					nodesWithTraversedPredecessors.add(n);
				}
			}
			while (nodesWithTraversedPredecessors.size > 0
					&& !((BellmanComponent) main.graphMan.endNode.component).isTraversed) {
				for (Node n : nodesWithTraversedPredecessors) {
					calculateNode(n);
				}
				nodesWithTraversedPredecessors.clear();
				for (Node node : main.graphMan.nodes) {
					for (Vector v : node.endVectors) {
						if (((BellmanComponent) node.component).isTraversed
								|| !((BellmanComponent) v.startNode.component).isTraversed) {
							continue;
						}
						nodesWithTraversedPredecessors.add(node);
					}
				}

			}

			// Checking for negative cycles:
			for (Vector v : main.graphMan.vectors) {
				if (((BellmanComponent) v.startNode.component).potential
						+ v.cost < ((BellmanComponent) v.endNode.component).potential) {
					throw new NoPathFoundException(
							"No path found due to negative cycle");
				}
			}
		}

		private void calculateNode(Node node) {
			Node[] previousNodes = new Node[node.endVectors.size]; // the
																	// predecessors
																	// that have
																	// been
																	// traversed
			for (int i = 0; i < node.endVectors.size; i++) {
				previousNodes[i] = node.endVectors.get(i).startNode;
			}

			// the potential of 'node' from each of the predecessors
			int[] potentials = new int[previousNodes.length];
			for (int i = 0; i < previousNodes.length; i++) {
				potentials[i] = node.endVectors.get(i).cost
						+ ((BellmanComponent) previousNodes[i].component).potential;
				System.out.println(potentials[i]);
			}
			// assign a random potential to 'node' from the list
			((BellmanComponent) node.component).potential = potentials[0];

			// find the best one (minimum)
			int selectedNodeIndex = -1; // keep an index to find the node that
										// has this potential
			for (int i = 0; i < potentials.length; i++) {
				if (potentials[i] <= ((BellmanComponent) node.component).potential) {
					((BellmanComponent) node.component).potential = potentials[i];
					selectedNodeIndex = i;
				}
			}

			((BellmanComponent) node.component).arborescences
					.add(previousNodes[selectedNodeIndex]);
			for (int i = 0; i < potentials.length; i++) {
				if (potentials[i] == potentials[selectedNodeIndex]
						&& i != selectedNodeIndex) {
					((BellmanComponent) node.component).arborescences
							.add(previousNodes[i]);
				}
			}
			((BellmanComponent) node.component).isTraversed = true;
		}

	}

	class Dijkstra {
		Array<Node> dijkstraResults;
		VisTable resultTable;

		Dijkstra() {
			dijkstraResults = new Array<Node>();
			resultTable = new VisTable();
		}

		public void calculate() throws NoPathFoundException {
			dijkstraResults.clear();
			resultTable.clearChildren();

			VisLabel label = new VisLabel("Node");
			label.setColor(Color.BLUE);
			resultTable.add(label);

			VisLabel potentialLabel = new VisLabel("Potential");
			potentialLabel.setColor(Color.BLUE);
			resultTable.add(potentialLabel);

			VisLabel arborescencesLabel = new VisLabel("Arborescence");
			arborescencesLabel.setColor(Color.BLUE);
			resultTable.add(arborescencesLabel);
			resultTable.row();
			try {
				Array<Node> visitedNodesMap = new Array<Node>();
				for (Node n : main.graphMan.nodes) {
					if (main.graphMan.startNode == n) {
						n.component = new DijkstraComponent().distance(0);

					} else {
						n.component = new DijkstraComponent()
								.distance(Integer.MAX_VALUE);

					}
				}
				int nodesToCheck = main.graphMan.nodes.size;
				Array<Node> neighbours = new Array<Node>();
				while (nodesToCheck > 0) {
					Node current = getMinimumCostNode();
					neighbours.clear();
					for (Vector v : current.startVectors) {
						neighbours.add(v.endNode);
					}
					for (Node neighb : neighbours) {
						if (!((DijkstraComponent) neighb.component).isTraversed) {
							int candidateCost = ((DijkstraComponent) current.component).distanceFromSource
									+ main.graphMan.getVector(current, neighb).cost;
							if (candidateCost < ((DijkstraComponent) neighb.component).distanceFromSource) {

								// Add the distance to the history before
								// changing it
								((DijkstraComponent) neighb.component).distancesHistory
										.add(((DijkstraComponent) neighb.component).distanceFromSource);
								// Same for previous node

								if (((DijkstraComponent) neighb.component).previousNode != null) { // the
																									// opposite
																									// will
																									// usually
																									// only
																									// happen
																									// the
																									// first
																									// time
									((DijkstraComponent) neighb.component).nodesHistory
											.add(((DijkstraComponent) neighb.component).previousNode);

								}

								((DijkstraComponent) neighb.component).distanceFromSource = candidateCost;
								((DijkstraComponent) neighb.component).previousNode = current;

							}

						}
					}
					visitedNodesMap.add(current);
					if (current == main.graphMan.endNode) {
						if (((DijkstraComponent) current.component).distanceFromSource == Integer.MAX_VALUE) {
							throw new NoPathFoundException("No Path Found");
						}
						break;
					}
					((DijkstraComponent) current.component).isTraversed = true;
					nodesToCheck--;

				}
				Node lastNode = main.graphMan.endNode;
				while (lastNode != null && lastNode != main.graphMan.startNode) {
					dijkstraResults.add(lastNode);
					lastNode = ((DijkstraComponent) lastNode.component).previousNode;
				}
				dijkstraResults.add(main.graphMan.startNode);

				for (Node n : main.graphMan.nodes) {
					if (dijkstraResults.contains(n, true)) {
						VisLabel idLabel = new VisLabel(n.ID + "");
						idLabel.setColor(Color.BLUE);
						resultTable.add(idLabel);
						VisLabel distanceLabel = new VisLabel();

						for (Integer distance : ((DijkstraComponent) n.component).distancesHistory) {
							if (distance == Integer.MAX_VALUE) {
								distanceLabel.setText(distanceLabel.getText()
										+ " Infinity, ");
							} else {
								distanceLabel.setText(distanceLabel.getText()
										+ "" + distance + ", ");

							}
						}
						if (((DijkstraComponent) n.component).distanceFromSource == Integer.MAX_VALUE) {
							distanceLabel.setText(distanceLabel.getText()
									+ " Infinity");
						} else {
							distanceLabel
									.setText(distanceLabel.getText()
											+ ""
											+ ((DijkstraComponent) n.component).distanceFromSource
											+ "");

						}

						distanceLabel.setColor(Color.BLUE);
						resultTable.add(distanceLabel);
						VisLabel previousNodeLabel = new VisLabel();
						if (((DijkstraComponent) n.component).previousNode == null) {
							previousNodeLabel.setText("None");
						}
						for (Node previous : ((DijkstraComponent) n.component).nodesHistory) {
							previousNodeLabel.setText(previousNodeLabel
									.getText() + ", " + previous.ID);
						}

						if (((DijkstraComponent) n.component).previousNode != null) {
							previousNodeLabel
									.setText(previousNodeLabel.getText()
											+ ", "
											+ ((DijkstraComponent) n.component).previousNode.ID);

						}

						previousNodeLabel.setColor(Color.BLUE);
						resultTable.add(previousNodeLabel);
						resultTable.row();
					} else {
						VisLabel idLabel = new VisLabel(n.ID + "");
						idLabel.setColor(Color.BLUE);
						resultTable.add(idLabel);
						VisLabel distanceLabel = new VisLabel("Infinity");
						distanceLabel.setColor(Color.BLUE);
						resultTable.add(distanceLabel);
						VisLabel previousNodeLabel = new VisLabel("None");
						previousNodeLabel.setColor(Color.BLUE);
						resultTable.add(previousNodeLabel);
						resultTable.row();

					}
				}

				setChanged();
				Object[] notification2 = { HUD.OBSERVE_RESULTTABLE, resultTable };
				notifyObservers(notification2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private Node getMinimumCostNode() {
			int minCost = Integer.MAX_VALUE;
			Node res = null;
			for (Node n : main.graphMan.nodes) {
				if (!((DijkstraComponent) n.component).isTraversed) {
					if (((DijkstraComponent) n.component).distanceFromSource <= minCost) {
						minCost = ((DijkstraComponent) n.component).distanceFromSource;
						res = n;
					}
				}
			}

			return res;
		}

	}

}