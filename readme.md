# Figure 1   
A k-ary fat tree with `k = 4` and `16 PMs`. There are two  
communicating VM pairs: `(v1 , v1′ )` and `(v2 , v2′ )`, and two middlebox instances 
`MB1` and `MB2`. The capacity of each `MB κ = 1`. 
The minimum VM communication takes place as follows: `(v1 , v )` traverses `M B1` (colored blue, 
1 with cost of 3) while `(v2,v2)` traverses MB1 too (colored red, with cost of 5), resulting in minimum total cost of 8 under uniform energy model. 



# Generalized Problem

## You should write a program that take the following command line inputs:

```
/a.out k l minB maxB B 
```

`k:` the size of the fat-tree topology

`l:` number of VM pairs, all the l pairs are randomly placed onto the physical machines in the data center

`minB`, `maxB:` each VM pair's bandwidth demand is a random number between [minB, maxB]

`B:` the bandwidth of each edge in fat tree

## Then you should design several algorithms that to maximize the total number of VMs, and compare their performances.

