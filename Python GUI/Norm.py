import random
import torch
import torch.nn as nn


def Normalization(x_data, Delta_data, shuffle = False, Norm = True):
    
    sequence = x_data.shape[0]
    feature = x_data.shape[-1]
    mean = 0.5
    std = 0.2
    
    if (Norm == True):

        Xmax_ = torch.max(x_data, dim=1)
        Xmin_ = torch.min(x_data, dim=1)
        Xrange_ = Xmax_[0] - Xmin_[0]

        Ymax_ = torch.max(Delta_data, dim=1)
        Ymin_ = torch.min(Delta_data, dim=1)
        Yrange_ = Ymax_[0] - Ymin_[0]

        for time_seq_idx in range(sequence):
            if (Yrange_[time_seq_idx].item() != 0):
                Delta_data[time_seq_idx] = Delta_data[time_seq_idx] - Ymin_[0][time_seq_idx].item()
                Delta_data[time_seq_idx] = Delta_data[time_seq_idx] / Yrange_[time_seq_idx].item()
            for feature_idx in range(feature):
                if (Xrange_[time_seq_idx][feature_idx].item() != 0):
                    x_data[time_seq_idx][:, feature_idx:feature_idx+1] = x_data[time_seq_idx][:, feature_idx:feature_idx+1] - Xmin_[0][time_seq_idx][feature_idx].item()
                    x_data[time_seq_idx][:, feature_idx:feature_idx+1] = x_data[time_seq_idx][:, feature_idx:feature_idx+1] / Xrange_[time_seq_idx][feature_idx].item()
    
    elif (Norm == False):
        x_data = (x_data - mean)/std
        Delta_data = (Delta_data - mean)/std
        
    if shuffle == True:
        index = list(range(0,sequence))
        random.shuffle(index)
        index = torch.tensor(index, dtype=torch.long)
        x_data[:] = x_data[index]
        Delta_data[:] = Delta_data[index]
    
    print('Norm is done!')
    return x_data, Delta_data