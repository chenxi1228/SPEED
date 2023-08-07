# Streaming Edge Partitioning Component (SEP)
A software package for Streaming Edge Partitioning.

Based on the paper:

"SPEED: Streaming Partition and Parallel Acceleration for Temporal Interaction Graph Embedding" submitted to ICDE'24

If you use the application please cite the paper.

###Usage:
Parameters:
- `graphfile`: the name of the file that stores the graph to be partitioned.
- `nparts`: the number of parts that the graph will be partitioned into. Maximum value 256.

Options:
- `-algorithm string`  ->  specifies the algorithm to be used (hdrf greedy hashing grid pds dbh). Default mymethod.
- `-lambda double`  ->  specifies the lambda parameter for hdrf and mymethod. Default 1.
- `-beta double` ->  specifies the beta parameter for hdrf. Default 0.1.
- `-seed int` ->  seed for repeated experiment. Default 0.
- `-threads integer`  ->  specifies the number of threads used by the application. Default all available processors.
- `-output string`  ->  specifies the prefix for the name of the files where the output will be stored (files: prefix.info, prefix.edges and prefix.vertices).


For a more in-depth discussion see the paper
###Example

```
java -jar dist/VGP.jar data/sample_graph.txt 4 -algorithm mymethod -lambda 1 -beta 0.1 -seed 1 -threads 1 -output output  
```
