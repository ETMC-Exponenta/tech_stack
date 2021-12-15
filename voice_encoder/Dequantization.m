function [output] = Dequantization(input)

alpha = 0.85;

for i=1:length(input)
    output(i) = 1/(1 - alpha*input(i)^(-1));
end

input(isinf(output) | isnan(output)) = 0;