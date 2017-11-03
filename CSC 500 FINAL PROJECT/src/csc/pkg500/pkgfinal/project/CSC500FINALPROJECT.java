package csc.pkg500.pkgfinal.project;

import java.util.Scanner;


public class CSC500FINALPROJECT {
    private static int k;
    private static int l;
    private static int bMin;
    private static int bMax;
    private static int b;
    
    public static void main(String[] args) {
        initializeValues();
        
        //create the array that holds all PM and switches k^2/4 + k^2 + k^3/4
        Device[] allNetworkElements = new Device[(5*k^2/4) + (k^3)/4];
        
    }
    
    public static void initializeValues(){
        Scanner scan = new Scanner(System.in);
        
        System.out.println("Enter k value: ");
        k = scan.nextInt();
        
        System.out.println("Enter l value: ");
        l = scan.nextInt();
        
        System.out.println("Enter bMin value: ");
        bMin = scan.nextInt();
        
        System.out.println("Enter bMax value: ");
        bMax = scan.nextInt();
        
        System.out.println("Enter b value: ");
        b = scan.nextInt();
    }
    
}
