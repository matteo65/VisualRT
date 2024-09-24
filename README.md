# VisualRT Overview
**VisualRT** is an advanced application designed to analyze the contents of a file by performing a series of statistical tests on the distribution of its bytes. It is used to evaluate the randomness or predictability of the file’s data, which can be useful in cryptography, compression analysis, and other fields where data distribution is important. 
It also allows you to view the "fingerprint" of the file, a sort of hash value in the form of a square image, where any patterns that could escape a statistical analysis emerge and helps establish the probability that the sequence is random or not.

![Alt Text](https://raw.githubusercontent.com/matteo65/VisualRT/main/Resource/screenshot1.png)<br>

## Statistical tests

### Min, Max and Average μ of Byte Frequency
VisualRT calculates the minimum and maximum frequency of bytes within the file. This test measures how often the least frequent and most frequent byte values (ranging from 0 to 255) occur in the file. A high maximum frequency might indicate that certain byte values are overrepresented, while a low minimum frequency could suggest that some byte values are nearly absent.

### Variance σ<sup>2</sup> of Byte Distribution
The **variance** measures how much the byte frequency distribution spreads out from the mean frequency. It gives an indication of the dispersion of the byte values. A low variance means that the byte frequencies are relatively uniform, while a high variance indicates that some byte values appear much more frequently than others.

### Standard Deviation σ
**Standard deviation** is the square root of the variance and provides an easy-to-understand measure of dispersion. It indicates how much, on average, the byte frequencies deviate from the mean. If the standard deviation is high, the distribution is uneven, with significant outliers, while a low value suggests more uniformity in byte usage.

### Coefficient of Variation <sup>σ</sup>/<sub>μ</sub>
The **coefficient of variation** (CV) is calculated as the ratio of the standard deviation to the mean byte frequency. This relative measure of variability allows comparison across different datasets. A high CV indicates that the byte frequencies vary greatly relative to the average frequency, suggesting a non-random or skewed distribution of bytes.

### Chi-Square Test 𝛘<sup>2</sup>
The **chi-square** test is used to evaluate whether the byte distribution significantly deviates from a uniform distribution. It compares the observed frequencies of each byte with the expected frequencies under the assumption of uniform randomness. A high chi-square value means the distribution deviates significantly from randomness, suggesting non-randomness or structured data in the file.

### Arithmetic Mean
This is simply the result of summing the all the bytes in the file and dividing by the file length. If the data are close to random, this should be about 127.5. If the mean departs from this value, the values are consistently high or low

### Entropy (Bits per Character)
**Entropy** is a key measure of randomness. It calculates the average number of bits needed to represent each byte in the file, based on the byte frequencies. The higher the entropy, the closer the byte distribution is to uniform, indicating higher randomness. Lower entropy values suggest that the file is more predictable or contains redundant information. Entropy is expressed in bits per character (or per byte).

### Estimated Compressed Length
This test estimates the possible compressed length of the file, assuming an optimal compression algorithm based on the file’s entropy. The idea is that if each character could be represented by its entropy-based bit length, this would give a lower bound on the file's compressed size. It provides insight into the compressibility of the file’s data.

### Estimation of Pi (π)
One of the more intriguing tests in VisualRT is the estimation of π using the contents of the file. The file is interpreted as a series of 6-byte sequences, with the first 3 bytes used as the x-coordinate and the next 3 bytes as the y-coordinate on a 2D plane. The number of points that fall inside a unit circle (with radius 1) is used to estimate π, drawing from **Monte Carlo** methods. The closer the estimate is to the true value of π, the more uniform the distribution of bytes in the file might be.

### Average of Contiguous Byte Pairs
This test calculates the average of all contiguous byte pairs in the file, treating each pair as a 16-bit unsigned number. In a file of length n (n > 2), there are n − 1 overlapping 2-byte sequences. By interpreting every two adjacent bytes as a single 16-bit value, ranging from 0 to 65535, the test evaluates the overall distribution of these values. For a file with a truly random byte distribution, the expected average should approach 32767.5, which is the midpoint of the possible 16-bit range. Deviations from this expected average can indicate non-random patterns or structured data, helping to assess the file's randomness level.

### 4-Byte Sequence Collision Test
This test analyzes the file by dividing it into 4-byte sequences and counting how many times each sequence appears (collisions). Each 4-byte sequence is treated as a unique 32-bit value, with possible values ranging from 0 to 2<sup>32</sup>-1. For a file with a completely random byte distribution, the expected number of collisions can be estimated using the **birthday problem** analogy. ​This test helps evaluate the randomness of the file. A number of collisions significantly different from the expected number could indicate non-random patterns or redundancy in the data, while a number of collisions close to the expected value suggests a higher degree of randomness.

### Highlighting outliers
If the results of the tests fall outside a threshold or range deemed appropriate for a uniform byte distribution, they are highlighted in red. This visual cue helps users quickly identify irregularities or deviations from expected randomness in the file's data.

### Print result on stdout
In order to have a text version of the test results, VisualRT also prints the values ​​to the standard output.

## Visual fingerprint
**VisualRT** also includes a graphical panel that provides a visual **"fingerprint"** of the file's byte content. This square panel, with adjustable dimensions limited by the screen size, displays the byte value distribution, offering a visual representation of potential patterns in the data. These patterns might indicate non-uniform distributions that could go unnoticed by statistical indices alone. Additionally, the panel includes a contrast adjustment feature, allowing users to amplify subtle, nearly imperceptible patterns, providing deeper insight into the structure of the file’s content.

## Conclusion
VisualRT provides a comprehensive suite of statistical and visual tools for analyzing the randomness and structure of a file's data. By combining traditional statistical measures (like variance and chi-square) with entropy calculations and even a unique approach to estimating π, it offers deep insights into the byte distribution, helping to evaluate the file’s compressibility, predictability, and overall randomness. Whether for cryptographic evaluation or data analysis, VisualRT is a powerful tool for assessing the underlying characteristics of any binary data.
