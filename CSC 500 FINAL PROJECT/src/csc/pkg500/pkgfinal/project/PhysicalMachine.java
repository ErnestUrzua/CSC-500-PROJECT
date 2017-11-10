package csc.pkg500.pkgfinal.project;


public class PhysicalMachine{
    private int vm1;
    private int vm2;
    
    public PhysicalMachine(){
        this.vm1 = -1;
        this.vm2 = -1;
    }
    
    public PhysicalMachine(int in1, int in2){
        this.vm1 = in1;
        this.vm2 = in2;
    }

    public int getVm1() {
        return vm1;
    }

    public void setVm1(int vm1) {
        this.vm1 = vm1;
    }

    public int getVm2() {
        return vm2;
    }

    public void setVm2(int vm2) {
        this.vm2 = vm2;
    }
    
    public boolean addVm(int input){
        if(vm1 == -1){
            vm1 = input;
            return true;
        }
        if(vm1 != -1 && vm2 == -1){
            vm2 = input;
            return true;
        }
        else
            return false;
    }
    
    public int howManyVms(){
        int count = 0;
        if(this.vm1 != -1)
            count++;
        if(this.vm2 != -1)
            count++;
        
        return count;
    }
    
    public String whatType(){
        return "physical machine";
    }
}
