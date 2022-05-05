import pandas as pd
import numpy as np
import torch
import torch.nn as nn


def downloadFile(time_step = 703001, feature = 5, toTensor = True,
                 m_path=None,
                 Output_delay_path=None,
                 P_CONST_path=None,
                 R_CONST_path=None,
                 Ref_path=None,
                 Target_path=None):
    
    tmp_x_tensor = np.zeros((1, time_step, feature))
    tmp_y_tensor = np.zeros((1, time_step, 1))
    

    M_data = pd.read_csv(m_path, header = None)
    Ref_data = pd.read_csv(Ref_path, header = None)
    P_CONST_data = pd.read_csv(P_CONST_path, header =None)
    R_CONST_data = pd.read_csv(R_CONST_path, header = None)
    Output_delayed_data = pd.read_csv(Output_delay_path, header = None)
    Delta_Output_data = pd.read_csv(Target_path, header = None)

    x_data = M_data.assign(Ref = Ref_data,
                           Output_delayed = Output_delayed_data,
                           P_CONST = P_CONST_data,
                           R_CONST = R_CONST_data
                           )

    x_data = np.array([x_data.to_numpy()])
    y_data = np.array([Delta_Output_data.to_numpy()])

    tmp_x_tensor = np.concatenate((tmp_x_tensor, x_data), axis=0)
    tmp_y_tensor = np.concatenate((tmp_y_tensor, y_data), axis=0)
    
    tmp_x_tensor = tmp_x_tensor[1:,:,:]
    tmp_y_tensor = tmp_y_tensor[1:,:,:]
    
    if (toTensor == True):
        tmp_x_tensor = torch.FloatTensor(tmp_x_tensor)
        tmp_y_tensor = torch.FloatTensor(tmp_y_tensor)
    
    print('Getting data is Done!')
    
    return tmp_x_tensor, tmp_y_tensor