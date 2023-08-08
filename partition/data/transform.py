import pandas as pd
 
df = pd.read_csv("./ml_taobao.csv")
df = df.drop(['Unnamed: 0','label','idx'],axis=1)
df.to_csv("./taobao.csv",index=False)

fr = open('./taobao.csv', 'rt')
fw = open('./taobao_new.txt', 'w+') 
  
ls = []
 
for line in fr:
    
    line = line.replace('\n', '')
    line = line.split(',')  
    ls.append(line)  

for row in ls:
    fw.write(' '.join(row) + '\n')
  
  
fr.close() 
fw.close() 