function [PredictValue] = LPC(path)

[Cepstrum, TimePar, audioFile] = FeatureExtractor(path);

spectrum = exp(fft(Cepstrum)); %Cepstrum --> PSD
autocorr = ifft(spectrum); %PSD --> ACF
a = levinson(autocorr, 17); %ACF --> linear prediction

for t = 19:(length(audioFile))
    for k = 1:18
        p_sum(k) = a(k)*audioFile(t-k); %Linear prediction
    end
    PredictValue(t-18) = sum(p_sum);
end;


