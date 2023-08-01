# SPEED
Codes for the paper "Streaming Partition and Parallel Acceleration for Temporal Interaction Graph Embedding" submitted to ICDE'24

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

`-threads integer` -> specifies the number of threads used by the application. Default all available processors.

`-output string` -> specifies the prefix for the name of the files where the output will be stored (files: prefix.info, prefix.edges and prefix.vertices).

For a more in-depth discussion see the paper ###Example

`java -jar dist/VGP.jar data/sample_graph.txt 4 -algorithm mymethod -lambda 1 -beta 0.1 -threads 1 -output example/output`  

## Parallel Acceleration Component (PAC)
### Regular training


### Training big datasets
If you would like to train the three big datasets, please use the codes in the branch "big_datasets".
In this branch accelerate the whole training process by optimising the I/O process and omitting unnecessary inductive validation and only using testing_from_begin setting.

You should first save the graphs and sub-graphs by running for different (parameters): `--datasets`, `--part_exp`, `--gpu`, `--divide_method` and `--seed`, you may also would like to set a different `--prefix`:
```
python ddp_train_self_supervised.py --gpu 0,1,2,3 --data [DATA] --part_exp [2/4] --[jodie/tgn/tgat/dyrep/tige] --prefix [add_your_prefered_prefix] --top_k [0/1/5/10/-1] --seed [0/1/2...] --sync_mode [last/none/average] --divide_method pre --backup_memory_to_cpu --testing_on_cpu --no_ind_val --save_mode save
```

Then you can reuse the saved graphs by setting `--save_mode read` for training for different top_k or other parameters.

```
python ddp_train_self_supervised.py --gpu 0,1,2,3 --data [DATA] --part_exp [2/4] --[jodie/tgn/tgat/dyrep/tige] --prefix [add_your_prefered_prefix] --top_k [0/1/5/10/-1] --seed [0/1/2...] --sync_mode [last/none/average] --divide_method pre --backup_memory_to_cpu --testing_on_cpu --no_ind_val --save_mode read
```

```
python train_self_supervised.py --data [DATA] --msg_src [left/right] --upd_src [left/right] --restarter [seq/static] --restart_prob [0/0.001/...]
```
If you want to use mooc/lastfm datasets, please pass one more argument: `--dim 100`.

Temporal Link Prediction with multi-GPU
```
python train_self_supervised_ddp.py --gpu 0,1,2,3 [...and other arguments]
```

Node Classification
```
python train_supervised.py --code [CODE]
```
Here, [CODE] is the HASH code of a trained model with `train_self_supervised.py`.

# Baselines 

Use presets:

```
python train_self_supervised.py --data [DATA] [--tige/--jodie/--tgn/--dyrep/--tgat]
```


# Exps


```
python train_self_supervised.py --data [DATA] [--tige/--jodie/--tgn/--dyrep/--tgat] --top_k [0/1/5/10/-1] --sync_mode [none/average/last]
```

```
python train_self_supervised.py --data [DATA] [--tige/--jodie/--tgn/--dyrep/--tgat] --top_k [0/1/5/10/-1] --static_shared_nodes
```

## baseline_kl

```
python train_self_supervised.py --data [DATA] [--tige/--jodie/--tgn/--dyrep/--tgat] --top_k 0 --static_shared_nodes --divide_method pre_kl --part_exp 2 --gpu 0,1,2,3
```
