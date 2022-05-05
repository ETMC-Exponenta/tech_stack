import torch
import torch.nn as nn
#Building custom DataSet
from torch.utils.data import TensorDataset, DataLoader
from torch.utils.data.dataset import random_split

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

#Custom modules
from GetData import downloadFile
from Norm import Normalization
from GetResult_BiGRU import getResult



x_data, y_data = downloadFile(time_step = 703001, feature = 5, toTensor = True)
x_data, y_data = Normalization(x_data, y_data, shuffle = True, Norm = False)

print('Размер x_data', x_data.shape)
print('Размер y_data', y_data.shape)

left_border = 40
right_border = -1

x_data_train = x_data[:left_border,:,:]
x_data_test = x_data[left_border:right_border,:,:]

y_data_train = y_data[:left_border,:,:]
y_data_test = y_data[left_border:right_border,:,:]

print('Размер x_train', x_data_train.shape)
print('Размер x_test', x_data_test.shape)
print('Размер y_train', y_data_train.shape)
print('Размер y_test', y_data_test.shape)

x_data_train = x_data_train.split(1, dim=0)
x_data_test = x_data_test.split(1, dim=0)

y_data_train = y_data_train.split(1, dim=0)
y_data_test = y_data_test.split(1, dim=0)


if torch.cuda.is_available():
    device = "cuda"
    #device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    torch.cuda.empty_cache()
    print(torch.cuda.memory_summary(device=None, abbreviated=False))
    

    
class DynamicSysGRU(nn.Module):
    
    def __init__(self):
        super(DynamicSysGRU, self).__init__()
        self.features = 5
        self.hidden_size = 20
        self.layers = 1
        self.rnn = nn.GRU(self.features, self.hidden_size, self.layers, bidirectional=False)
        self.linear = nn.Linear(self.hidden_size, 1) 
          
    def forward(self, x):
        
        h0 = torch.randn(1, 703001, self.hidden_size).to(device)
        output, hn = self.rnn(x)
        hn = torch.reshape(hn, (1, 703001, -1))
        output = self.linear(output) #hn
        return output
    
    
#Loading model
PATH = 'bi_gru.pt'
net = DynamicSysGRU().to(device)
net.load_state_dict(torch.load(PATH))
net.eval()


#delta shows you how many sequence to be considered
tag = 650000 #650000
delta = len(x_data_test)
getResult(net, 
          x_data_test, 
          y_data_test, 
          delta, 
          device,
          tag)