path = 'C:\Users\Artemy\Desktop\Exponenta\asr_calls_2_val\0\00\2f29b7d43246.wav';
[audioFile, fs] = audioread(path);

%% PRE-PROCESSING
speech_Q = Quantization(audioFile(t));
noise = randn(length(audioFile(t)));
mixed_speech_Q = (speech_Q + noise);
mixed_speech = Dequantization(mixed_speech_Q);

PredictValue = LPC(path);

for t = 18:(length(audioFile) - 18)
    e(t) = audioFile(t) - PredictValue(t-17);
end

e_Q = Quantization(e);
p_Q = Quantization(p);

%% NOISE INJECTION PROCEDURE

% Length of batch - 10 ms
% 10 ms batch - 20-dimensional vector - 128-dimensional vector 
% 128-dimensional vector + 1-dim current predict (from 18-band cepstrum) +
% 1-dim last signal + 1-dim last predict error = 131-dim vector
% 131-dim vector - 1-dim current predict error

%X_Train = mixed_speech
%Y_train = audioFile
