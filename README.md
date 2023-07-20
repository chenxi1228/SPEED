# TIG-PDF
Codes for the paper "TIG-PDF: Scaling and Accelerating Temporal Interaction Graph Models on Large Graphs" submitted to ICDE'24

# Data

For the datasets Wikipedia, Reddit, MOOC and LastFM, please download data from the [project homepage of JODIE](https://snap.stanford.edu/jodie/) and pre-process them with the script provided by [TGN](https://github.com/twitter-research/tgn).
For ML25m, please download data from the [grouplens](https://grouplens.org/datasets/movielens/25m/) and put the file ratings.csv into the folder [Datasets](Datasets) then, pre-process it with the [ML25m2TGN.py](ML25m2TGN.py).
For DGraphfin, please download data from the [DGraph](https://dgraph.xinye.com/dataset) and put it into the folder [Datasets](Datasets) then, pre-process it with the [DGraphFin2TGN.py](DGraphFin2TGN.py).
For ML25m, please download data from the [Tianchi](https://tianchi.aliyun.com/dataset/649) and put the file into the folder [Datasets](Datasets) then, pre-process it with the [Taobao2TGN.py](Taobao2TGN.py).

# How to use

## TIG Streaming Edge Partitioning Module with controllable shared nodes (SEP)


## large-scale Distributed Computing Module (DCM)

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