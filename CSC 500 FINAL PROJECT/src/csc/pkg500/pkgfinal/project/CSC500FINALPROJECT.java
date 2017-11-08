package csc.pkg500.pkgfinal.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CSC500FINALPROJECT {

    private static int k = 32;
    private static int l = 8192;
    private static int bMin = 1;
    private static int bMax = 4;
    private static int bandwidthCapacity = 10;

    //array that holds randomly generated bandwidth consumption for each VM pair
    private static double[] vmBandwidth;

    //create the array that holds all PMs and switches 5/4 * k^2 +  1/4 * k^3 
    private static Object[] allNetworkElements;

    //array that holds all edge and bandwidth per edge information
    //Total number of edges - 3/4 * k^3
    private static Edge[] allEdges;

    public static void main(String[] args) {
        if(args.length == 5){
            initializeFromCommandline(args);
        }
        
        initializeValues();

        initializeVirtualMachines();

        initializeEdges();

//        printPath();
//
//        printEdges();

        randomOrderSearch();
        lowestBandwidthVmFirst();
        highestBandwidthVmFirst();
    }
    
    public static void initializeFromCommandline(String[] input){
        k = Integer.parseInt(input[0]);
        
        l = Integer.parseInt(input[1]);
        
        bMin = Integer.parseInt(input[2]);
        
        bMax = Integer.parseInt(input[3]);
        
        bandwidthCapacity = Integer.parseInt(input[4]);
    }
    
    public static void printParameters(){
        System.out.println("Initialized with following parameters...");
        System.out.println("K value: " + k);
        System.out.println("Number of VM pairs: " + l);
        System.out.println("Minimum bandwidth per VM pair: " + bMin);
        System.out.println("Maximum bandwidth per VM pair: " + bMax);
        System.out.println("Maximum bandwidth allowed per edge: " + bandwidthCapacity + "\n");
    }

    public static void printPath() {
        ArrayList<Integer> path;

        for (int i = 0; i < l; i++) {
            int hops = hopsPerPair(i);
            int[] pairLocations = findVmLocation(i);
            System.out.println("VM: " + i + " found in PM " + pairLocations[0] + " and PM " + pairLocations[1]);
            System.out.println("With hops " + hops);
            System.out.println("Connection Path: ");
            path = findPath(i);
            if (path == null) {
                System.out.println("Couldn't allocate bandwidth\n");
            } else {
                for (Integer n : path) {
                    System.out.println(n);

                }
                System.out.println();
            }
        }
    }

    public static int randomOrderSearch() {
        cleanEdgeBandwidth();
        int count = 0;
        ArrayList<Integer> path;
        System.out.println("Random VM pair order search");
        for (int i = 0; i < l; i++) {
            path = findPath(i);
            if (path == null) {

            } else {
                count++;
            }

        }

        System.out.println("Total VMs: " + l + " VMs placed: " + count );
        float percentagePlaced = (float) count / l;
        System.out.println("Percentage placed: " + percentagePlaced + "\n");
        return count;
    }

    public static void cleanEdgeBandwidth() {
        for (Edge e : allEdges) {
            e.setCurrentBandwidth(0);
        }
    }

    public static void reverseArray(int[] x) {
        reverse(x, 0, x.length - 1);
    }

    public static void reverse(int[] x, int i, int j) {
        if (i < j) {//Swap
            int tmp = x[i];
            x[i] = x[j];
            x[j] = tmp;
            reverse(x, ++i, --j);//Recursive
        }
    }

    public static void highestBandwidthVmFirst() {
        cleanEdgeBandwidth();

        System.out.println("Highest Bandwidth VM pair first search");

        double[] bandwidthOrder;
        int[] order = new int[l];

        bandwidthOrder = vmBandwidth.clone();

        Arrays.sort(bandwidthOrder);

        for (int j = 0; j < bandwidthOrder.length; j++) {
            for (int i = 0; i < l; i++) {
                if (vmBandwidth[i] == bandwidthOrder[j]) {
                    order[j] = i;
                }
            }
        }

        reverseArray(order);

        int count = 0;
        ArrayList<Integer> path;
        for (int i = 0; i < l; i++) {
            path = findPath(order[i]);
            if (path == null) {

            } else {
                count++;

            }

        }

        System.out.println("Total VMs: " + l + " VMs placed: " + count );
        float percentagePlaced = (float) count / l;
        System.out.println("Percentage placed: " + percentagePlaced + "\n");
    }

    public static void lowestBandwidthVmFirst() {
        cleanEdgeBandwidth();
        System.out.println("Lowest Bandwidth VM pair first search");

        double[] bandwidthOrder;
        int[] order = new int[l];

        bandwidthOrder = vmBandwidth.clone();

        Arrays.sort(bandwidthOrder);

        for (int j = 0; j < bandwidthOrder.length; j++) {
            for (int i = 0; i < l; i++) {
                if (vmBandwidth[i] == bandwidthOrder[j]) {
                    order[j] = i;
                }
            }
        }

        int count = 0;
        ArrayList<Integer> path;
        for (int i = 0; i < l; i++) {
            path = findPath(order[i]);
            if (path == null) {

            } else {
                count++;

            }

        }

        System.out.println("Total VMs: " + l + " VMs placed: " + count );
        float percentagePlaced = (float) count / l;
        System.out.println("Percentage placed: " + percentagePlaced + "\n");
    }

    public static void printEdges() {
        for (Edge e : allEdges) {
            System.out.println(e);
        }
    }

    public static void initializeValues() {

        Scanner scan = new Scanner(System.in);

//        System.out.println("Enter k value: ");
//        k = scan.nextInt();
//        
//        System.out.println("Enter l value: ");
//        l = scan.nextInt();
//        
//        System.out.println("Enter bMin value: ");
//        bMin = scan.nextInt();
//        
//        System.out.println("Enter bMax value: ");
//        bMax = scan.nextInt();
//        
//        System.out.println("Enter b value: ");
//        bandwidthCapacity = scan.nextInt();
        //initialize elements array with total number of elements
        allNetworkElements = new Object[((5 * square(k)) / 4) + (cube(k)) / 4];

        //initialize edge array with total number of edges
        allEdges = new Edge[3 * cube(k) / 4];

        for (int i = 0; i < (cube(k) / 4); i++) {
            allNetworkElements[i] = new PhysicalMachine();
        }

        for (int i = (cube(k) / 4); i < (cube(k) / 4 + square(k) / 2); i++) {
            allNetworkElements[i] = new Switch("edge");
        }

        for (int i = (cube(k) / 4 + square(k) / 2); i < (cube(k) / 4 + square(k)); i++) {
            allNetworkElements[i] = new Switch("aggregation");
        }

        for (int i = (cube(k) / 4 + square(k)); i < allNetworkElements.length; i++) {
            allNetworkElements[i] = new Switch("core");
        }
        
        printParameters();

    }

    public static void initializeVirtualMachines() {
        int numberOfPhysicalMachines = (cube(k)) / 4;
        int whichPhysicalMachine;
        PhysicalMachine element;

        //initalizing the vm pairs to random PMs
        for (int i = 0; i < l; i++) {

            //first of the pair
            whichPhysicalMachine = (int) (Math.random() * numberOfPhysicalMachines);
            element = (PhysicalMachine) allNetworkElements[whichPhysicalMachine];

            while (!element.addVm(i)) {
                whichPhysicalMachine = (int) (Math.random() * numberOfPhysicalMachines);
                element = (PhysicalMachine) allNetworkElements[whichPhysicalMachine];
            }

            //second of the pair
            whichPhysicalMachine = (int) (Math.random() * numberOfPhysicalMachines);
            element = (PhysicalMachine) allNetworkElements[whichPhysicalMachine];

            while (!element.addVm(i)) {
                whichPhysicalMachine = (int) (Math.random() * numberOfPhysicalMachines);
                element = (PhysicalMachine) allNetworkElements[whichPhysicalMachine];
            }
        }
        //intialize bandwidths with number of vm pairs
        vmBandwidth = new double[l];

        //setting the bandwidth for each pair
        for (int i = 0; i < vmBandwidth.length; i++) {
            double bandwidth = bMin + Math.random() * (bMax - bMin);
            vmBandwidth[i] = bandwidth;
        }

    }

    public static void initializeEdges() {
        //simple constants to make loops appear cleaner
        int a = cube(k) / 4;
        int b = a + square(k) / 2;
        int c = square(k) / 2;
        int d = k / 2;

        //initialize PM to Edge switch edges
        for (int i = 0; i < a; i++) {
            allEdges[i] = new Edge(i, a + (i / d), bandwidthCapacity);
        }

        //initialize Edge switch to Aggregation switch edges
        int count = a;
        int count2 = 0;
        for (int i = a; i < b; i++) {
            for (int j = 0; j < d; j++) {
                allEdges[count] = new Edge(i, b + j + count2, bandwidthCapacity);
                count++;
            }
            if ((i + 1) % d == 0) {
                count2 += d;
            }
        }

        //initialize Aggregation switch edges to Core switch edges
        count2 = 0;
        for (int i = b; i < a + square(k); i++) {
            for (int j = 0; j < d; j++) {
                allEdges[count] = new Edge(i, a + square(k) + count2, bandwidthCapacity);
                count++;
                count2++;
            }
            if ((i + 1) % d == 0) {
                count2 = 0;
            }
        }
    }

    public static int square(int input) {
        return (int) Math.pow(input, 2);
    }

    public static int cube(int input) {
        return (int) Math.pow(input, 3);
    }

    public static int[] findVmLocation(int input) {
        int[] vmLocations = new int[]{-1, -1};

        for (int i = 0; i < cube(k) / 4; i++) {
            PhysicalMachine currentPM = (PhysicalMachine) allNetworkElements[i];

            //finds first vm location
            if (vmLocations[0] == -1 && (currentPM.getVm1() == input || currentPM.getVm2() == input)) {
                vmLocations[0] = i;
                if (currentPM.getVm1() == input && currentPM.getVm2() == input) {
                    vmLocations[1] = i;
                    return vmLocations;
                } else {
                    continue;
                }
            }

            //finds second vm location
            if (vmLocations[0] != -1 && (currentPM.getVm1() == input || currentPM.getVm2() == input)) {
                vmLocations[1] = i;
                return vmLocations;
            }

        }

        return vmLocations;
    }

    public static int hopsPerPair(int input) {
        int[] pairLocations = findVmLocation(input);
        //System.out.println("VM: " + input + " found in PM " + pairLocations[0] + " and PM " + pairLocations[1]);

        //they are both in the same PM
        if (pairLocations[0] == pairLocations[1]) {
            return 0;
        }

        //if they are both connected to the same Edge switch
        if (pairLocations[1] - pairLocations[0] < k / 2
                && (pairLocations[1] / (k / 2)) == (pairLocations[0] / (k / 2))) {
            return 1;
        }

        //if they are connected to neighboring edge switches in the same pod
        if (pairLocations[1] - pairLocations[0] < k
                && (pairLocations[1] / (k)) == (pairLocations[0] / (k))) {
            return 3;
        }

        return 5;
    }

    public static int findLowestBandwidthSwitchChoice(ArrayList<Integer> availableSwitches, int previousSwitch, int currentVm) {

        //find which switch has the lowest bandwidth to our previous switch
        double tempBandwidth = Double.MAX_VALUE;
        int currentLowestBwSwitch = -1;
        for (int i = 0; i < availableSwitches.size(); i++) {
            for (int j = 0; j < allEdges.length; j++) {
                if (allEdges[j].getNode1() == previousSwitch
                        && allEdges[j].getNode2() == availableSwitches.get(i)
                        && tempBandwidth > allEdges[j].getCurrentBandwidth()
                        && allEdges[j].testAdd(vmBandwidth[currentVm])) {

                    currentLowestBwSwitch = availableSwitches.get(i);
                    tempBandwidth = allEdges[j].getCurrentBandwidth();
                }
            }
        }

        return currentLowestBwSwitch;
    }

    public static int findLowestBandwidthSwitchChoiceDown(ArrayList<Integer> availableSwitches, int previousSwitch, int currentVm) {

        //find which switch has the lowest bandwidth to our previous switch
        double tempBandwidth = Double.MAX_VALUE;
        int currentLowestBwSwitch = -1;
        for (int i = 0; i < availableSwitches.size(); i++) {
            for (int j = 0; j < allEdges.length; j++) {
                if (allEdges[j].getNode2() == previousSwitch
                        && allEdges[j].getNode1() == availableSwitches.get(i)
                        && tempBandwidth > allEdges[j].getCurrentBandwidth()
                        && allEdges[j].testAdd(vmBandwidth[currentVm])) {

                    currentLowestBwSwitch = availableSwitches.get(i);
                    tempBandwidth = allEdges[j].getCurrentBandwidth();
                }
            }
        }

        return currentLowestBwSwitch;
    }

    public static ArrayList<Integer> findPath(int input) {
        // l is the number of vm pairs
        //find the path from vm0 to vm0
        int[] locations = findVmLocation(input);
        int hops = hopsPerPair(input);
//        System.out.println("With hops " + hops);
//        System.out.println("Connection Path: ");

        ArrayList<Integer> path = new ArrayList<>();
        ArrayList<Integer> on = new ArrayList<>();

        //for 0 hops
        if (hops == 0) {
            path.add(locations[0]);
            return path;
        }

        //handling the case for when there is only one hop
        if (hops == 1) {
            on.add(locations[0]);
            path.add(locations[0]);

            for (Edge allEdge : allEdges) {
                if (allEdge.getNode1() == on.get(0)) {
                    on.add(allEdge.getNode2());
                    //adding bandwidth contribution
                    if (!allEdge.addBandwidth(vmBandwidth[input])) {
                        return null;
                    }

                    path.add(allEdge.getNode2());
                    on.remove(0);
                    break;
                }
            }

            for (Edge e : allEdges) {
                if (e.getNode2() == on.get(0)) {

                    path.add(locations[1]);
                    break;
                }
            }

            for (Edge e : allEdges) {
                if (e.getNode1() == locations[1]) {
                    //adding bw contribution for return edge
                    if (!e.addBandwidth(vmBandwidth[input])) {
                        return null;
                    }
                    break;
                }

            }

            return path;
        }
        //handling the 3 hop case

        if (hops == 3) {
            on.add(locations[0]);
            path.add(locations[0]);

            //finding the first switch
            for (Edge e : allEdges) {
                if (e.getNode1() == on.get(0)) {
                    on.add(e.getNode2());
                    path.add(e.getNode2());
                    on.remove(0);

                    //adding bandwidth contribution
                    if (!e.addBandwidth(vmBandwidth[input])) {
                        return null;
                    }
                    break;
                }
            }
            //finding the second switches
            for (Edge e : allEdges) {
                if (e.getNode1() == on.get(0)) {
                    on.add(e.getNode2());
                }
            }
            int previousSwitch = on.remove(0);
            int currentLowestBwSwitch = findLowestBandwidthSwitchChoice(on, previousSwitch, input);

            //adding bandwidth for 2-3 switch
            for (Edge e : allEdges) {
                if (e.getNode1() == previousSwitch && e.getNode2() == currentLowestBwSwitch) {
                    if (!e.addBandwidth(vmBandwidth[input])) {
                        return null;
                    }
                }
            }

            //take the switch with the lowest bandwidth route
            on.clear();
            on.add(currentLowestBwSwitch);
            path.add(currentLowestBwSwitch);

            //find the path to the switch that connects to the other VM in the pair
            int finalSwitch = -1;
            for (Edge e : allEdges) {
                if (e.getNode1() == locations[1]) {
                    finalSwitch = e.getNode2();
                }
            }

            //now connect 2nd to last switch with last switch
            for (Edge e : allEdges) {
                if (e.getNode2() == on.get(0) && e.getNode1() == finalSwitch) {
                    path.add(e.getNode1());

                    if (!e.addBandwidth(vmBandwidth[input])) {
                        return null;
                    }
                }
            }
            //add location of final PM
            //add final path bandwidth
            for (Edge e : allEdges) {
                if (e.getNode1() == locations[1]) {
                    if (!e.addBandwidth(vmBandwidth[input])) {
                        return null;
                    }
                }

            }
            path.add(locations[1]);

            return path;
        }

        //handling 5 hop case
        if (hops == 5) {
            on.add(locations[0]);
            path.add(locations[0]);

            //getting first switch
            for (Edge allEdge : allEdges) {
                if (allEdge.getNode1() == on.get(0)) {
                    on.add(allEdge.getNode2());
                    path.add(allEdge.getNode2());
                    on.remove(0);

                    if (!allEdge.addBandwidth(vmBandwidth[input])) {
                        return null;
                    }

                    break;
                }
            }

            //getting second switch
            //finding the second switches
            for (Edge e : allEdges) {
                if (e.getNode1() == on.get(0)) {
                    on.add(e.getNode2());
                }
            }
            int previousSwitch = on.remove(0);
            //find which switch has the lowest bandwidth to our previous switch

            int currentLowestBwSwitch = findLowestBandwidthSwitchChoice(on, previousSwitch, input);
            //couldn't find switch
            if (currentLowestBwSwitch == -1) {
                return null;
            }

            //adding bandwidth from 2-3
            for (Edge e : allEdges) {
                if (e.getNode1() == previousSwitch && e.getNode2() == currentLowestBwSwitch) {
                    if (!e.addBandwidth(vmBandwidth[input])) {
                        return null;
                    }
                }
            }

            //take the switch with the lowest bandwidth route (Aggregate switch)
            on.clear();
            on.add(currentLowestBwSwitch);
            path.add(currentLowestBwSwitch);

            //find the core switch with the lowest bandwidth
            //first finding all the core switches our aggregate switch connects to
            for (Edge e : allEdges) {
                if (e.getNode1() == on.get(0)) {
                    on.add(e.getNode2());
                }
            }

            previousSwitch = on.remove(0);
            //find our lowest bandwidth choice AS - CS
            currentLowestBwSwitch = findLowestBandwidthSwitchChoice(on, previousSwitch, input); //its failing in here?

            //couldn't find switch
            if (currentLowestBwSwitch == -1) {
                return null;
            }

            //adding bandwidth from 3-4
            for (Edge e : allEdges) {
                if (e.getNode1() == previousSwitch && e.getNode2() == currentLowestBwSwitch) {
                    if (!e.addBandwidth(vmBandwidth[input])) {
                        return null;
                    }
                }
            }

            on.clear();
            on.add(currentLowestBwSwitch);
            path.add(currentLowestBwSwitch);

            //find the lowest bandwidth choice from CS - appropriate AS
            //which pod does it connect to?
            int podNumber = ((4 * locations[1]) / square(k));

            //is our cs in first half or second half?
            int a = currentLowestBwSwitch;

            int min = -1;
            int max = -1;

            if (a >= cube(k) / 4 + square(k) && a < cube(k) / 4 + 9 * square(k) / 8) {
                //first half
                //what range of pod numbers to consider?
                min = cube(k) / 4 + square(k) / 2 + (podNumber * (k / 2));
                max = cube(k) / 4 + square(k) / 2 + (podNumber * k / 2) + k / 4; //not inclusive
            }

            if (a >= cube(k) / 4 + 9 * square(k) / 8 && a < cube(k) / 4 + 5 * square(k) / 4) {
                //second half
                //what range of pod numbers to consider?
                min = cube(k) / 4 + square(k) / 2 + (podNumber * k / 2) + k / 4;
                max = cube(k) / 4 + square(k) / 2 + (podNumber * k / 2) + k / 2;// not inclusive
            }

            previousSwitch = on.remove(0);

            on.clear();

            //adding possible AS to consider
            for (int i = min; i < max; i++) {
                on.add(i);
            }

            //lowest bandwidth choice from CS - AS
            currentLowestBwSwitch = findLowestBandwidthSwitchChoiceDown(on, previousSwitch, input);

            //couldn't find switch
            if (currentLowestBwSwitch == -1) {
                return null;
            }

            //adding bandwidth from 4-5
            for (Edge e : allEdges) {
                if (e.getNode2() == previousSwitch && e.getNode1() == currentLowestBwSwitch) {
                    if (!e.addBandwidth(vmBandwidth[input])) {
                        return null;
                    }
                }
            }

            path.add(currentLowestBwSwitch);
            //to find choice from AS - ES go backwards from bottom
            //find the path to the switch that connects to the other VM in the pair
            int finalSwitch = -1;
            for (Edge e : allEdges) {
                if (e.getNode1() == locations[1]) {
                    finalSwitch = e.getNode2();
                }
            }

            //now connect 2nd to last switch with last switch
            for (Edge e : allEdges) {
                if (e.getNode2() == on.get(0) && e.getNode1() == finalSwitch) {
                    path.add(e.getNode1());
                    if (!e.addBandwidth(vmBandwidth[input])) {
                        return null;
                    }

                }

            }

            path.add(locations[1]);

            //add bandwidth for last
            for (Edge e : allEdges) {
                if (e.getNode1() == locations[1]) {
                    if (!e.addBandwidth(vmBandwidth[input])) {
                        return null;
                    }
                }
            }

            return path;
        }

        return path;
    }

}
