function [Cepstrum, TimePar, audioFile] = FeatureExtractor(path)

[audioFile, fs] = audioread(path);
cepFeatures = cepstralFeatureExtractor('SampleRate',fs); %Create cepstrum extractor
cepFeatures.NumCoeffs = 17; %18-Band cepstrum
[coeffs,delta,deltaDelta] = cepFeatures(audioFile); %Find cempstrum

Cepstrum = coeffs; %Cepstrum coeff

corr = xcorr(audioFile); %ACF

[pks,locs] = findpeaks(corr); %Find local maximums of ACF
[ii, ii] = sort(pks); %Soft for findind first and second maximum

Power = pks(ii(end-1))/pks(ii(end)); %Power of correlation
NCorr = abs((ii(end) - ii(end-1))); %Number of points between peaks
TimeCorr = 1/fs*NCorr; %Correlation time

TimePar = [Power, TimeCorr];