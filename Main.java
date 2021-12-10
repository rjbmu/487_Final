import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

//Final Project submission for ECE 487
//By Ryan Berning

//Haversine class and methods made by Jason Winn
//https://github.com/jasonwinn/haversine

public class Main {
	
	static ArrayList<Node> nodeList;
	static ArrayList<Node> tempList = new ArrayList<Node>();
	static ArrayList<String> destList = new ArrayList<String>();
	static Node[] totalPath, nodesPath, finalPath;
	static String startPoint, endPoint, transport;
	static int startIndex, endIndex, destinations, tripTime, divisor;
	static Node startNode, endNode;
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		//Filling in the Node arrayList from the CSV
		nodeList = Node.readFromFile("nodeInfo.csv");
		
		//Asking for number of destinations
		System.out.println("How many destinations: ");
		destinations = in.nextInt();
		//clearing the \n
		in.nextLine();
		
		//Getting the start point and destination(s)
		System.out.println("What is your start point?");
		startPoint = in.nextLine();
		for (int i = 0; i < destinations; i++) {
			System.out.println("What is your destination #" + (i + 1) + "? ");
			destList.add(in.nextLine());
		}
		
		//Method of locomotion
		System.out.println("How are you traveling? ");
		transport = in.nextLine();
		
		//Takes time as an int in military time(I.E. 1100 = 11am, 2300 = 11pm)
		System.out.println("What time are you traveling? ");
		tripTime = in.nextInt();
		
		endPoint = destList.get(0);
		
		//Checking all destinations and the start point for existence
		startIndex = Node.nodeSearch(nodeList, startPoint);
		endIndex = Node.nodeSearch(nodeList, endPoint);
		for (int i = 0; i < destList.size(); i++) {
			int tempIndex = Node.nodeSearch(nodeList, destList.get(i));
			if (tempIndex == -1) {
				System.out.println("End point not found. Exiting.");
				System.exit(0);
			}
		}
		if (startIndex == -1) {
			System.out.println("Start point not found. Exiting.");
			System.exit(0);
		}
		
		startNode = nodeList.get(startIndex);
		endNode = nodeList.get(endIndex);
		
		if (destinations > 0) {
				for (int i = 0; i < destinations; i++) {
					int nextDest = Node.nodeSearch(nodeList, destList.get(i));
					endNode = nodeList.get(nextDest);
					//Running the pathing algorithm
					nodesPath = aStar(nodeList, startNode, endNode, tripTime);
					
					//Separate if cases to prevent double recording the end node as the new start node
					if (i == 0) {
						for (int j = 0; j < nodesPath.length; j++) {
							tempList.add(nodesPath[j]);
						}
					} else {
						for (int j = 1; j < nodesPath.length; j++) {
							tempList.add(nodesPath[j]);
						}
					}
					//Making the destination of the last path, the start point of the next path
					startNode = endNode;
				}
				
				//converting the multiple destination list into an array to be printed
				finalPath = new Node[tempList.size()];
				tempList.toArray(finalPath);
			
		} else {
			System.out.println("0 destinations selected. Exiting.");
			System.exit(0);
		}
		
		//Printing the path between nodes
		for(int i = 0; i < finalPath.length; i++) {
			if (i < finalPath.length - 1) {
				System.out.print(finalPath[i].toString() + "->");
			} else {
				System.out.print(finalPath[i].toString() + "\n");
			}
		}
		
		double distance = findDistance(finalPath);
		double adjustedDistance = findAdjustedDistance(finalPath, tripTime);
		
		//Changing the speed value depending on method of transportation
		transport = transport.toLowerCase();
		if (transport.equals("scooter")) {
			divisor = 400;
		} else if (transport.equals("bicycle")) {
			divisor = 420;
		} else {
			divisor = 84;
		}
		
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		DecimalFormat numberFormat2 = new DecimalFormat("#.0");
		//Dividing the path distance by the average locomotion speed, in meters per minute
		System.out.println("The route is " + numberFormat.format(distance) + " meters and will take " + numberFormat2.format(adjustedDistance/divisor) + " minutes.");
		
		try {
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that implements an a* algorithm to find the path between the nodes given to it
	 * @param nodeList	arraylist of all nodes
	 * @param startNode	Node to start at
	 * @param endNode	Destination node to work towards
	 * @param tripTime	Time that the trip is occurring, needed for congestion
	 * @return	Returns an array of nodes in the order from startNode to endNode
	 */
	public static Node[] aStar(ArrayList<Node> nodeList, Node startNode, Node endNode, int tripTime) {
		Node[] nodesPath;
		Node closestNode = null, compareNode = null;
		double endLat, endLong;
		int runTime = nodeList.size();
		String closest;
		ArrayList <Node> pathList = new ArrayList<Node>();
		boolean pathFound = false;
		
		//Adding the startNode to the start of the arrayList
		pathList.add(startNode);
		
		
		String[] tempConArray;
		closestNode = startNode;
		endLat = endNode.getLatitude();
		endLong = endNode.getLongitude();
		
		//While loop to run until path is found or runTime = 0, prevents infinite runs
		while(runTime > 0 && pathFound == false) {
			tempConArray = closestNode.getConnected();
			closest = tempConArray[0];
			
			for (int i = 0; i < tempConArray.length; i++) {
				if (tempConArray[i].equals(endNode.getName())) {
					closest = tempConArray[i];
					pathFound = true;
					break;
				}
			}
			if (pathFound == false) {
				for (int i = 0; i < tempConArray.length; i++) {
					double compCong, closeCong;
					
					//Getting the actual node objects from the names of closest and the other connected nodes
					closestNode = nodeList.get(Node.nodeSearch(nodeList, closest));
					compareNode = nodeList.get(Node.nodeSearch(nodeList, tempConArray[i]));
					
					double compLat = compareNode.getLatitude();
					double compLong = compareNode.getLongitude();
					double closeLat = closestNode.getLatitude();
					double closeLong = closestNode.getLongitude();
					
					//Checking tripTime to determine if certain routes are congested, only effects times between 10am and 4pm
					if (tripTime > 1000 && tripTime < 1600) {
						compCong = compareNode.getCongestion();
						closeCong = closestNode.getCongestion();
					} else {
						compCong = 1;
						closeCong = 1;
					}
						
					//Checking proximity by finding the difference of the two nodes distances to the destination node
					//Taking in to account congestion by multiplying by the congestion multiplier if the time variable is within a certain range
					double distance1 = Haversine.distance(closeLat, closeLong, endLat, endLong) * closeCong;
					double distance2 = Haversine.distance(compLat, compLong, endLat, endLong) * compCong;
					
					//changing closest to the node(name) at tempConArray[i] if its distance list less than the current nodes distance
					if (distance1 > distance2) {
						closest = tempConArray[i];
					}
					runTime--;
				}
			}
			
			//Setting closestNode to the node found in the above for loop
			closestNode = nodeList.get(Node.nodeSearch(nodeList, closest));	
			pathList.add(closestNode);
		}
		
		nodesPath = new Node[pathList.size()];
		pathList.toArray(nodesPath);
		
		return nodesPath;
	}
	
	/**
	 * Method to find the sum of the distance between nodes given an array of nodes
	 * @param nodePath	Node array
	 * @return	Double of the distance sum in meters
	 */
	public static double findDistance(Node[] nodePath) {
		double sumDistance = 0;
		
		for(int i = 0; i < nodePath.length - 1; i++) {
			sumDistance = sumDistance + Haversine.distance(nodePath[i].getLatitude(), nodePath[i].getLongitude(), nodePath[i + 1].getLatitude(), nodePath[i + 1].getLongitude());
		}
		
		return (sumDistance * 1000);
	}
	
	/**
	 * Method that returns the "distance" taking in to account the time, only used for finding travel time
	 * @param nodePath	array of nodes on the path
	 * @return	Double of the adjusted distance in meters
	 */
	public static double findAdjustedDistance(Node[] nodePath, int time) {
		double sumDistance = 0;
		double congestion = 1;
		
		//Uses the most congested value of the nodes
		for(int i = 0; i < nodePath.length - 1; i++) {
			
			if (time > 1000 && time < 1600) {
				congestion = nodePath[i].getCongestion();
			}
			
			sumDistance = sumDistance + Haversine.distance(nodePath[i].getLatitude(), nodePath[i].getLongitude(), nodePath[i + 1].getLatitude(), nodePath[i + 1].getLongitude()) * congestion;
		}
		
		return (sumDistance * 1000);
	}
}
