import torch
import torch.nn as nn

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

def getResult(net, x_data, y_data, delta, device, tag):
    
    for item_index in range(delta):
        
        print(f'For {item_index+1} sequence\n')

        value_pred = []
        value_target = []

        feature = x_data[item_index].to(device)
        target = y_data[item_index].to(device)

        prediction = net(feature)

        #for tensor_ in prediction[0]:
        #    value_pred.append(tensor_.item())
        #for tensor_ in target[0]:
        #    value_target.append(tensor_.item())
            
        #Cut sequences
        value_pred = prediction[0,:,0].cpu().detach()
        value_target = target[0,:,0].cpu().detach()
        print(value_pred.shape)

        #Comparison
        fig, ax = plt.subplots()
        ax.plot(value_pred, label = 'Prediction Signal', linestyle = '--', color = 'r')
        ax.plot(value_target, label = 'Reference Signal', linestyle = ':', color = 'b')
        ax.legend()
        fig.set_figheight(8)
        fig.set_figwidth(12)
        plt.grid()
        #plt.savefig('Result/Plot/Result_Seq'+str(item_index+1)+'.png')
        plt.show()
