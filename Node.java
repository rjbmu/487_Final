import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Node {
	
	private double longitude, latitude, congestion;
	private String name;
	private String[] connected;
	
	//Node object constructor
	public Node(String name, double latitude, double longitude, double congestion, String[] connected) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.congestion = congestion;
		this.connected = connected;
	}
	
	/**
	 * Reading from a CSV file and returning a Node object array
	 * @param file CSV to read in
	 * @return	Node[]
	 */
	public static ArrayList<Node> readFromFile(String file) {
		Scanner in = null;
		
		ArrayList<Node> nodeList = new ArrayList<Node>();
		String tempRow;
		String[] tempValues, connected;
		Node tempNode;
		
		
		try {
			in = new Scanner(new File(file));
			
			while(in.hasNextLine()) {
				tempRow = in.nextLine();
				
				tempValues = tempRow.split(",",0);
				connected = new String [tempValues.length - 4];
				
				//Reading in the names of the connected nodes to an array to attach to the Node object
				for (int i = 4; i < tempValues.length; i++) {
					connected[i - 4] = tempValues[i];
				}
				
				//Creating a Node object and appending it to the arrayList
				tempNode = new Node(tempValues[0], Double.parseDouble(tempValues[1]), Double.parseDouble(tempValues[2]), Double.parseDouble(tempValues[3]), connected);
				nodeList.add(tempNode);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return nodeList;
	}
	
	/**
	 * Returns an index corresponding with the index of the found matching node, or -1 if no match is found
	 * @param nodeList	Array list containing all nodes
	 * @param nodeName	String that represents the name of the node being searched for
	 * @return	The index of the found node in the list, or -1 if no match found
	 */
	public static int nodeSearch(ArrayList<Node> nodeList, String nodeName) {
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeName.equals(nodeList.get(i).name)) {
				return i;
			}
		}
		//If no node matching the input name is found, returns -1
		return -1;
	}
	
	/**
	 * Overridden equals method that just checks the name variable for parity
	 */
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Node)) {
			return false;
		}
		
		Node n = (Node) o;
		return n.name.equals(name);
	}
	
	@Override
	public String toString() {
		return String.format(name);
	}
	
	//Getters
	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}
	
	public double getCongestion() {
		return congestion;
	}
	
	public String getName() {
		return name;
	}

	public String[] getConnected() {
		return connected;
	}

	
}
