package csc.pkg500.pkgfinal.project;

import java.util.Scanner;

public class CSC500FINALPROJECT {

    private static int k;
    private static int l;
    private static int bMin;
    private static int bMax;
    private static int b;

    //array that holds randomly generated bandwidth consumption for each VM pair
    private static double[] vmBandwidth;

    //create the array that holds all PMs and switches k^2/4 + k^2 + k^3/4
    private static Object[] allNetworkElements;

    //array that holds all edge and bandwidth per edge information
    //Total number of edges - 3/4 * k^3
    private static Edge[] allEdges;

    public static void main(String[] args) {

        initializeValues();

        initializeVirtualMachines();

        //initializeEdges();
        
        for(int i = 0; i < cube(k) / 4; i++){
            PhysicalMachine temp = (PhysicalMachine) allNetworkElements[i];
            System.out.println("PM " + i + " has virtual machines " + temp.getVm1() + " and " + temp.getVm2());
        }
        
        for(int i = 0; i < l; i++){
            System.out.println(hopsPerPair(i) + " Hops for pair: " + i + " ");
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
//        b = scan.nextInt();
        k = 4;
        l = 16;
        bMin = 1;
        bMax = 2;
        b = 5;
        
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
            allEdges[i] = new Edge(i, a + (i / d));
        }

        //initialize Edge switch to Aggregation switch edges
        int count = a;
        int count2 = 0;
        for (int i = a; i < b; i++) {
            for (int j = 0; j < d; j++) {
                allEdges[count] = new Edge(i, b + j + count2);
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
                allEdges[count] = new Edge(i, a + square(k) + count2);
                count++;
                count2++;
            }
            if ((i + 1) % d == 0) {
                count2 = 0;
            }
        }

        for (Edge e : allEdges) {
            System.out.println(e);
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
                continue;
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
        System.out.println("VM: " + input + " found in PM " + pairLocations[0] + " and PM " + pairLocations[1]);
        
        //they are both in the same PM
        if(pairLocations[0] == pairLocations[1])
            return 0;
        
        //if they are both connected to the same Edge switch
        if(pairLocations[1] - pairLocations[0] < k / 2 &&
           (pairLocations[1] / (k / 2)) == (pairLocations[0] / (k / 2)))
            return 1;
        
        //if they are connected to neighboring edge switches
        if(pairLocations[1] - pairLocations[0] < k &&
           (pairLocations[1] / (k)) == (pairLocations[0] / (k)))
            return 4;
        
        return 6;
    }
}
