# VisualRT
This application applies various tests to check the level of randomness of the contents of a file (sequence of bytes)
It also allows you to view the "fingerprint" of the file, a sort of hash value in the form of a square image, where any patterns that could escape a statistical analysis emerge and helps establish the probability that the sequence is random or not.

![Alt Text](https://raw.githubusercontent.com/matteo65/VisualRT/main/Resource/screenshot1.png)<br>

## Byte Frequency Analysis
The following values ​​are displayed:
- Expected mean frequency (total number of bytes / 256)
- Minimum frequency
- Maximum frequency
- Variance
- Standard deviation
- Coefficient of variation
- Chi square

## Arithmetic Mean
This is simply the result of summing the all the bytes in the file and dividing by the file length. If the data are close to random, this should be about 127.5. If the mean departs from this value, the values are consistently high or low

## Entropy
Defines quantifies the average level of uncertainty or information, expressed as the number of bits per byte. A random sequence the entropy value is (at most) 8 bits per byte; lower values ​​indicate a lower level of randomness
