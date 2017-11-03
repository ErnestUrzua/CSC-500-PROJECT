package csc.pkg500.pkgfinal.project;

public class Edge {
    private int node1;
    private int node2;
    private double currentBandwidth;
    
    public Edge(){
        this.node1 = -1;
        this.node2 = -1;
        this.currentBandwidth = 0;
    }
    
    public Edge(int node1, int node2){
        this.node1 = node1;
        this.node2 = node2;
    }
    
    public Edge(int node1, int node2, double currentBandwidth){
        this(node1,node2);
        this.currentBandwidth = currentBandwidth;
    }

    public int getNode1() {
        return node1;
    }

    public void setNode1(int node1) {
        this.node1 = node1;
    }

    public int getNode2() {
        return node2;
    }

    public void setNode2(int node2) {
        this.node2 = node2;
    }

    public double getCurrentBandwidth() {
        return currentBandwidth;
    }

    public void setCurrentBandwidth(double currentBandwidth) {
        this.currentBandwidth = currentBandwidth;
    }

    @Override
    public String toString() {
        return "Edge{" + "node1=" + node1 + ", node2=" + node2 + ", currentBandwidth=" + currentBandwidth + '}';
    }
   
    
}
