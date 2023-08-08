# big_datasets branch

If you would like to use the codes in this branch, please download the main branch and then substitute the duplicated files by using this branch.

In this branch, the whole training process is being accelerated by optimising the I/O process and omitting unnecessary inductive validation and only using `testing_from_begin` setting.

You should (optionally) first save the graphs and sub-graphs by running `save_graphs.py` for different (parameters): `--data`, `--part_exp`, `--gpu`, `--divide_method`, `--top_k` and `--seed` (the saved graphs will be the same if these parameters are the same), you may also would like to set a different `--prefix`:
```
python save_graphs.py --gpu 0,1,2,3 --data [DATA] --part_exp [1/2/3...] --prefix [add_your_prefered_prefix] --top_k [0/1/5/10/-1] --seed [0/1/2...] --divide_method pre --save_mode save
```

Then you can reuse the saved graphs by setting `--save_mode read` for training for different backbone models or other parameters. 

```
python ddp_train_self_supervised.py --gpu 0,1,2,3 --data [DATA] --part_exp [1/2/3...] --[jodie/tgn/tgat/dyrep/tige] --prefix [add_your_prefered_prefix] --top_k [0/1/5/10/-1] --seed [0/1/2...] --sync_mode [last/none/average] --divide_method pre --backup_memory_to_cpu --testing_on_cpu --no_ind_val --dim 100 --save_mode read
```
