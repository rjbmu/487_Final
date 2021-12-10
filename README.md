# 487_Final
My final project for ECE 487

The purpose of this program is to read in a CSV of the paths and buildings at Miami University and output a shortest path between the chosen buildings using an a* algorithm.

This repo contains the program itself(Main.java), the node class(Node.java) which makes node objects from the nodeInfo.csv(also included), the haversine class(Haversine.java, made by Jason Winn: https://github.com/jasonwinn/haversine), a zip to make a Latex document of the final report for this project and the PDF of the final report.

To run the program: Create a new Java project in the IDE of your choice, put the nodeInfo.csv file into the project folder next to the bin, src, etc. Put Haversine.java, Node.java and Main.java into the src folder. The program should now work when you hit the run button.

When the program starts it will ask you how many destinations you want to go to, enter the integer of destinations you would like. It will then ask you for a start point, then destination(s) depending on the number you input. Type them in the format shown below or the program will exit. It will then ask how you are traveling, the options are scooter, bicycle, or walking. If you give an invalid input it will default to walking. It will then ask what time you are traveling. This is input in military time as an integer, so for example, 8am is entered as 0800 and 5pm is entered as 1700. After the time is input the program will run and output a path, route distance and travel time, and then close.
