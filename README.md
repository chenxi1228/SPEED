# SPEED
Codes for the paper "SPEED: Streaming Partition and Parallel Acceleration for Temporal Interaction Graph Embedding" submitted to ICDE'24

# Data

For the datasets Wikipedia, Reddit, MOOC and LastFM, please download data from the [project homepage of JODIE](https://snap.stanford.edu/jodie/) and pre-process them with the script provided by [TGN](https://github.com/twitter-research/tgn).

For ML25m, please download data from the [grouplens](https://grouplens.org/datasets/movielens/25m/) and put the file ratings.csv into the folder [Datasets](Datasets) then, pre-process it with the [ML25m2TGN.py](ML25m2TGN.py).

For DGraphfin, please download data from the [DGraph](https://dgraph.xinye.com/dataset) and put it into the folder [Datasets](Datasets) then, pre-process it with the [DGraphFin2TGN.py](DGraphFin2TGN.py).

For ML25m, please download data from the [Tianchi](https://tianchi.aliyun.com/dataset/649) and put the file into the folder [Datasets](Datasets) then, pre-process it with the [Taobao2TGN.py](Taobao2TGN.py).

# How to use

## Streaming Edge Partitioning Component (SEP)
###Usage: Parameters:

graphfile: the name of the file that stores the graph to be partitioned.
nparts: the number of parts that the graph will be partitioned into. Maximum value 256.
Options:

`-algorithm string` -> specifies the algorithm to be used (hdrf greedy hashing grid pds dbh). Default mymethod.

`-lambda double` -> specifies the lambda parameter for hdrf and mymethod. Default 1.

`-beta double` -> specifies the beta parameter for hdrf. Default 0.1.

`-seed int` -> seed for repeated experiments. Default 0.

`-threads integer` -> specifies the number of threads used by the application. Default all available processors.

`-output string` -> specifies the prefix for the name of the files where the output will be stored (files: prefix.info, prefix.edges and prefix.vertices).

For a more in-depth discussion see the paper ###Example

`java -jar dist/VGP.jar data/sample_graph.txt 4 -algorithm mymethod -lambda 1 -beta 0.1  -seed 1 -threads 1 -output output`  

## Parallel Acceleration Component (PAC)
### Regular training
Please use the main branch to proceed a regular training. You can also use it to train big datasets, however, by applying the codes in the branch "big_datasets" would save you some time.

```
python ddp_train_self_supervised.py --gpu 0,1,2,3 --data [DATA] --part_exp [2/4/8...] --[jodie/tgn/tgat/dyrep/tige] --prefix [add_your_prefered_prefix] --top_k [0/1/5/10/-1] --seed [0/1/2...] --sync_mode [last/none/average] --divide_method pre
```

If you are facing OOM problem, passing the arguments `--backup_memory_to_cpu` and  `--testing_on_cpu` may help you.

### Training big datasets
If you would like to train the three big datasets, please use the codes in the branch "big_datasets".
In this branch accelerate the whole training process by optimising the I/O process and omitting unnecessary inductive validation and only using `testing_from_begin` setting.

You should first save the graphs and sub-graphs by running for different (parameters): `--datasets`, `--part_exp`, `--gpu`, `--divide_method` and `--seed` (the saved graphs will be the same if these parameters are the same), you may also would like to set a different `--prefix`:
```
python ddp_train_self_supervised.py --gpu 0,1,2,3 --data [DATA] --part_exp [2/4/8...] --[jodie/tgn/tgat/dyrep/tige] --prefix [add_your_prefered_prefix] --top_k [0/1/5/10/-1] --seed [0/1/2...] --sync_mode [last/none/average] --divide_method pre --backup_memory_to_cpu --testing_on_cpu --no_ind_val --dim 100 --save_mode save
```

Then you can reuse the saved graphs by setting `--save_mode read` for training for different top_k or other parameters. 

```
python ddp_train_self_supervised.py --gpu 0,1,2,3 --data [DATA] --part_exp [2/4/8...] --[jodie/tgn/tgat/dyrep/tige] --prefix [add_your_prefered_prefix] --top_k [0/1/5/10/-1] --seed [0/1/2...] --sync_mode [last/none/average] --divide_method pre --backup_memory_to_cpu --testing_on_cpu --no_ind_val --dim 100 --save_mode read
```


Node Classification
```
python train_supervised.py --code [CODE]
```
Here, [CODE] is the HASH code of a trained model with `train_self_supervised.py`.


