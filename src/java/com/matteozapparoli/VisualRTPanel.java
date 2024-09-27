package com.matteozapparoli.visualrt;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Matteo Zapparoli
 * 18 set 2024
 */
public class VisualRTPanel extends JPanel {
	
	private final static int BUFFER_SIZE = 1048576;
	
	private final static int MAX_SIDE = 1800;
	private final static int RGB_WHITE_COLOR = Color.WHITE.getRGB();
	private static final long IN_CIRCLE = 0xFFFFFFL * 0xFFFFFFL; // In-circle distance for Monte Carlo Pi
	private static final long IN_SPHERE = 0xFFFFFFL * 0xFFFFFFL; // In-sphere distance for Monte Carlo Pi
	
	private int[] values;
	private int[][] matrix = new int[MAX_SIDE][MAX_SIDE];
	private int side;
	private JSlider contrastSlider;

	private int contrast = 1;
	private final JTextField filePathFieldTxt = new JTextField();
	private final JTextField fileLengthTxt = new JTextField();
	private final JTextField minByteFreqTxt = new JTextField();
	private final JTextField maxByteFreqTxt = new JTextField();
	private final JTextField avgByteFreqTxt = new JTextField();
	private final JTextField varianceTxt = new JTextField();
	private final JTextField stdDevTxt = new JTextField();
	private final JTextField chiSquaredTxt = new JTextField();
	private final JTextField coefVarTxt = new JTextField();
	private final JTextField avgBytesValue = new JTextField();
	private final JTextField compressedLen = new JTextField();
	private final JTextField entropyTxt = new JTextField();
	private final JTextField monteCarloPi2D = new JTextField();
	private final JTextField monteCarloPi2DErr = new JTextField();
	private final JTextField monteCarloPi3D = new JTextField();
	private final JTextField monteCarloPi3DErr = new JTextField();
	private final JTextField avgPairBytesTxt = new JTextField();
	private final JTextField avgPairBytesErrTxt = new JTextField();
	private final JTextField collisionsTxt = new JTextField();
	private final JTextField collisionsExpTxt = new JTextField();
	
	private final Color defaultForeground;
	
	private JLabel randomTxt = new JLabel();
	
	public VisualRTPanel() {
		setLayout(null);
		defaultForeground = filePathFieldTxt.getForeground();
		
		// Selettore di file
        JFileChooser fileChooser = new JFileChooser();
		MouseAdapter openFile = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
						loadFile(selectedFile);
						repaint();
					} 
                    catch (IOException e1) {
						e1.printStackTrace();
					}
                }
            }
        };
		
		JLabel label = new JLabel("File:");
		label.setBounds(8, 8, 40, 30);
		label.setToolTipText("Open file");
		label.addMouseListener(openFile);
		add(label);
		
        filePathFieldTxt.setEditable(false);  // Campo disabilitato per evitare modifiche manuali
        filePathFieldTxt.setBounds(45, 12, 185, 21);
        add(filePathFieldTxt);
        
        label = new JLabel("Length:");
        label.setBounds(8, 40, 40, 30);
        add(label);
        
        fileLengthTxt.setEditable(false);
        fileLengthTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        fileLengthTxt.setBounds(45, 44, 60, 21);
        add(fileLengthTxt);

        filePathFieldTxt.setToolTipText("Open file");
        filePathFieldTxt.addMouseListener(openFile);

        label = new JLabel("Contrast:");
        label.setBounds(8, 72, 60, 30);
        add(label);
        
        contrastSlider = new JSlider(0, 80, 0);
        contrastSlider.setMajorTickSpacing(20);
        contrastSlider.setMinorTickSpacing(20);
        contrastSlider.setPaintTicks(true);
        contrastSlider.setPaintLabels(true);
        contrastSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            	contrast = 1 + contrastSlider.getValue();
                repaint();
            }
        });
        
        contrastSlider.setBounds(55, 77, 180, 40);
        add(contrastSlider);
        
        label = new JLabel("Min byte frequency:");
        label.setBounds(8, 140, 110, 30);
        add(label);
        
        minByteFreqTxt.setEditable(false);
        minByteFreqTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        minByteFreqTxt.setBounds(140, 145, 90, 21);
        add(minByteFreqTxt);

        label = new JLabel("Max byte frequency:");
        label.setBounds(8, 170, 110, 30);
        add(label);
        
        maxByteFreqTxt.setEditable(false);
        maxByteFreqTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        maxByteFreqTxt.setBounds(140, 175, 90, 21);
        add(maxByteFreqTxt);
        
        label = new JLabel("Avg byte frequency μ:");
        label.setBounds(8, 200, 110, 30);
        add(label);
        
        avgByteFreqTxt.setEditable(false);
        avgByteFreqTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        avgByteFreqTxt.setBounds(140, 205, 90, 21);
        add(avgByteFreqTxt);
        
        label = new JLabel("Variance σ²:");
        label.setBounds(8, 230, 110, 30);
        add(label);
        
        varianceTxt.setEditable(false);
        varianceTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        varianceTxt.setBounds(140, 235, 90, 21);
        add(varianceTxt);
        
        label = new JLabel("Standard Deviation σ:");
        label.setBounds(8, 260, 110, 30);
        add(label);
        
        stdDevTxt.setEditable(false);
        stdDevTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        stdDevTxt.setBounds(140, 265, 90, 21);
        add(stdDevTxt);

        label = new JLabel("Coefficient of Variation σ/μ:");
        label.setBounds(8, 290, 130, 30);
        add(label);
        
        coefVarTxt.setEditable(false);
        coefVarTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        coefVarTxt.setBounds(140, 295, 90, 21);
        add(coefVarTxt);
        
        label = new JLabel("Chi squared χ²:");
        label.setBounds(8, 320, 110, 30);
        add(label);
        
        chiSquaredTxt.setEditable(false);
        chiSquaredTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        chiSquaredTxt.setBounds(140, 325, 90, 21);
        add(chiSquaredTxt);
        
        label = new JLabel("Avg bytes value - 127.5:");
        label.setBounds(8, 350, 130, 30);
        add(label);
        
        avgBytesValue.setEditable(false);
        avgBytesValue.setHorizontalAlignment(SwingConstants.RIGHT);
        avgBytesValue.setBounds(140, 355, 90, 21);
        add(avgBytesValue);
        
        label = new JLabel("Entropy:");
        label.setBounds(8, 380, 130, 30);
        add(label);
        
        entropyTxt.setEditable(false);
        entropyTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        entropyTxt.setBounds(140, 385, 90, 21);
        add(entropyTxt);
        
        label = new JLabel("Compressed length:");
        label.setBounds(8, 410, 130, 30);
        add(label);
        
        compressedLen.setEditable(false);
        compressedLen.setHorizontalAlignment(SwingConstants.RIGHT);
        compressedLen.setBounds(140, 415, 90, 21);
        add(compressedLen);
        
        label = new JLabel("Monte Carlo for π 2D:");
        label.setBounds(8, 440, 130, 30);
        add(label);
        
        monteCarloPi2D.setEditable(false);
        monteCarloPi2D.setHorizontalAlignment(SwingConstants.RIGHT);
        monteCarloPi2D.setBounds(140, 445, 90, 21);
        add(monteCarloPi2D);

        label = new JLabel("Monte Carlo for π 2D Error:");
        label.setBounds(8, 470, 130, 30);
        add(label);
        
        monteCarloPi2DErr.setEditable(false);
        monteCarloPi2DErr.setHorizontalAlignment(SwingConstants.RIGHT);
        monteCarloPi2DErr.setBounds(140, 475, 90, 21);
        add(monteCarloPi2DErr);

        label = new JLabel("Monte Carlo for π 3D:");
        label.setBounds(8, 500, 130, 30);
        add(label);
        
        monteCarloPi3D.setEditable(false);
        monteCarloPi3D.setHorizontalAlignment(SwingConstants.RIGHT);
        monteCarloPi3D.setBounds(140, 505, 90, 21);
        add(monteCarloPi3D);

        label = new JLabel("Monte Carlo for π 3D Error:");
        label.setBounds(8, 530, 130, 30);
        add(label);
        
        monteCarloPi3DErr.setEditable(false);
        monteCarloPi3DErr.setHorizontalAlignment(SwingConstants.RIGHT);
        monteCarloPi3DErr.setBounds(140, 535, 90, 21);
        add(monteCarloPi3DErr);

        label = new JLabel("Avg pair bytes - 32767.5:");
        label.setBounds(8, 560, 130, 30);
        add(label);
        
        avgPairBytesTxt.setEditable(false);
        avgPairBytesTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        avgPairBytesTxt.setBounds(140, 565, 90, 21);
        add(avgPairBytesTxt);

        label = new JLabel("Avg pair bytes error:");
        label.setBounds(8, 590, 130, 30);
        add(label);
        
        avgPairBytesErrTxt.setEditable(false);
        avgPairBytesErrTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        avgPairBytesErrTxt.setBounds(140, 595, 90, 21);
        add(avgPairBytesErrTxt);

        label = new JLabel("32 bit collisions:");
        label.setBounds(8, 620, 130, 30);
        add(label);
        
        collisionsTxt.setEditable(false);
        collisionsTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        collisionsTxt.setBounds(140, 625, 90, 21);
        add(collisionsTxt);

        label = new JLabel("Expected collisions:");
        label.setBounds(8, 650, 130, 30);
        add(label);
        
        collisionsExpTxt.setEditable(false);
        collisionsExpTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        collisionsExpTxt.setBounds(140, 655, 90, 21);
        add(collisionsExpTxt);
        
        randomTxt.setBounds(40, 680, 300, 30);
        add(randomTxt);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(values == null) return;

		for (int x = 0; x < side; x++) {
            for (int y = 0; y < side; y++) {
            	matrix[x][y] = 0;
            }
		}
		side = Math.min(getWidth() - 250, getHeight());
		
		double f = 65536.0d / side;
		
		BufferedImage canvas = new BufferedImage(side, side, BufferedImage.TYPE_INT_RGB);
		
		for(int i = 0; i < values.length; i++) {
			int x = (int)((values[i] >>> 16) / f);
			int y = (int)((values[i] & 0xFFFF) / f);
			matrix[x][y]++;
		}
		
		// Calcolo il valore max e min di matrix e inizializzo i pixel del canvas
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		for (int x = 0; x < side; x++) {
            for (int y = 0; y < side; y++) {
            	if(matrix[x][y] > max) {
            		max = matrix[x][y];
            	}
            	else if(matrix[x][y] < min) {
            		min = matrix[x][y];
            	}
            	// Inizializzo i pixel del canvas
            	canvas.setRGB(x, y, RGB_WHITE_COLOR);
            }
		}
		
		// Calcolo il fattore di "normalizzazione" s
		double s = contrast * contrast * 255.0d / (max - min + 0.001d);
		
		// Disegno matrix nel canvas
		for (int x = 0; x < side; x++) {
            for (int y = 0; y < side; y++) {
            	int value = (int)Math.round((matrix[x][y] - min) * s);
            	
        		if(value < 0)
        			value = 0;
        		else if(value > 255) 
        			value = 255;

        		value = 255 - value;
        		
        		int rgb = (value << 16) | (value << 8) | value;
        		
        		canvas.setRGB(x, y, rgb);
            }
        }
		g.drawImage(canvas, 240, 8, null);
	}
	
	public void loadFile(File file) throws FileNotFoundException, IOException {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		filePathFieldTxt.setText(file.getName());
		fileLengthTxt.setText(Long.toString(file.length()));
		double filelen = file.length();
		long filelenDiv4 = file.length() / 4;
		values = new int[(int)(filelenDiv4)];
		byte[] buffer = new byte[BUFFER_SIZE];
		long[] freq = new long[256];
		
		/**
		 * 2^32 bits, 1 byte = 8 hash values
		 */
		byte[] bitTable = new byte[536870912];
		long collisionsCounter = 0L;
		
		// Variables for Monte Carlo Pi 2D
		long numSamples2D = 0L;
		long x2D = 0L, y2D = 0L;
		long pointsInsideCircle = 0L;
		int counter2D = 0;
		
		// Variables for Monte Carlo Pi 3D
		long numSamples3D = 0L;
		long x3D = 0L, y3D = 0L, z3D = 0L;
		long pointsInsideSphere = 0L;
		int counter3D = 0;
		
		long sumPairBytes = 0;
		
		try (FileInputStream fis = new FileInputStream(file)) {
			int k = 0;
			int n;
			int precByte = -1;
			int h = 0;
			int value32 = 0;
			
			while((n = fis.read(buffer)) > 0) {
				for(int i = 0; i < n; i++) {
					int b = buffer[i] & 0xFF; // b from 0 to 255
					
					freq[b]++;

					// Monte Carlo 2D
					if(++counter2D <= 3) {
						x2D = (x2D << 8) | b;
					}
					else if(counter2D <= 6) {
						y2D = (y2D << 8) | b;
						
						if(counter2D == 6) {
							if(x2D * x2D + y2D * y2D <= IN_CIRCLE) {
								pointsInsideCircle++;
							}
							numSamples2D++;
							counter2D = 0;
							x2D = y2D = 0L;
						}
					}
					
					// Monte Carlo 3D
					if(++counter3D <= 3) {
						x3D = (x3D << 8) | b;
					}
					else if(counter3D <= 6) {
						y3D = (y3D << 8) | b;
					}
					else if(counter3D <= 9) {
						z3D = (z3D << 8) | b;
						
						if(counter3D == 9) {
							if(x3D * x3D + y3D * y3D + z3D * z3D <= IN_SPHERE) {
								pointsInsideSphere++;
							}
							numSamples3D++;
							counter3D = 0;
							x3D = y3D = z3D = 0L;
						}
					}
					
					// Media 2 byte
					if(precByte != -1) {
						sumPairBytes += (b << 8) + precByte;
					}
					precByte = b;
					
					// Collisions 32 bit
					value32 = (value32 << 8) + b;
					if(h++ == 3) {
						int index = (value32 >>> 3); // byte index of bitTable (value32 / 8)
						int bit = (value32 & 0x07); // bit into byte (value32 mod 8)
						int mask = 0x01 << bit; // bit mask
						
						if((bitTable[index] & mask) != 0) {
							collisionsCounter++;
						}
						else {
							bitTable[index] |= mask;
						}
						values[k++] = value32;
						value32 = 0;
						h = 0;
					}
				}
			}
		}

		double average = filelen / 256.0d;		

		// Calcolo la frequenza minima e massima, lo scarto quadratico medio e chi quadrato
		long minFreq = Long.MAX_VALUE;
		long maxFreq = Long.MIN_VALUE;
		
		double variance = 0.0d;
		double chi_squared = 0.0d;
		long bytesum = 0L;
		double entropy = 0.0d;
		
		double mathLog2 = Math.log(2);
		
		for(int i = 0; i < 256; i++) {
			bytesum += i * freq[i];
			
			if(freq[i] < minFreq) {
				minFreq = freq[i];
			}
			if(freq[i] > maxFreq) {
				maxFreq = freq[i];
			}
			variance += (freq[i] - average) * (freq[i] - average);
			chi_squared += (freq[i] - average) * (freq[i] - average) / average;
			
			if(freq[i] > 0) {
    			double frequency = freq[i] / filelen;
    			entropy -= (frequency * Math.log(frequency)) / mathLog2; // log base 2
			}
		}
		variance /= 256.0d;
		
		double standard_deviation = Math.sqrt(variance);
		
		boolean probablyRandom = true;
		
		System.out.println("File name = " + file.getName());
		System.out.println("File length = " + file.length());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("File date = " + sdf.format(new Date(file.lastModified())));
		
		// Bytes statistics
		System.out.println("Average byte frequency = " + average);
		minByteFreqTxt.setText(Long.toString(minFreq));
		System.out.print("Minimum byte frequency = " + minFreq);
		if(average != 0 && Math.abs((average - minFreq) / average) > 0.06d) {
			System.out.println(" [*** anomaly]");
			minByteFreqTxt.setForeground(Color.RED);
			minByteFreqTxt.setToolTipText("*** Anomaly: |min value| > 6% of average value");
			probablyRandom = false;
		}
		else {
			minByteFreqTxt.setForeground(defaultForeground);
			minByteFreqTxt.setToolTipText(null);
			System.out.println();
		}
		
		System.out.print("Maximum byte frequency = " + maxFreq);
		maxByteFreqTxt.setText(Long.toString(maxFreq));
		if(average != 0 && Math.abs((average - maxFreq) / average) > 0.06d) {
			System.out.println(" [*** anomaly]");
			maxByteFreqTxt.setForeground(Color.RED);
			maxByteFreqTxt.setToolTipText("*** Anomaly: |max value| > 6% of average value");
			probablyRandom = false;
		}
		else {
			maxByteFreqTxt.setForeground(defaultForeground);
			maxByteFreqTxt.setToolTipText(null);
			System.out.println();
		}
		
		if(average != 0) {
    		avgByteFreqTxt.setText(round(average));
		}
		else {
			avgByteFreqTxt.setText("");
		}
		
		System.out.println("Variance = " + variance);
		varianceTxt.setText(round(variance));
		
		System.out.println("Standard Deviation = " + standard_deviation);
		stdDevTxt.setText(round(standard_deviation));
		
		double coefVar = standard_deviation / average * 100.0d;
		System.out.print("Coefficient of Variation = " + coefVar);
		coefVarTxt.setText(round(coefVar) + "%");
		if(coefVar > 1.9) {
			System.out.println(" [*** anomaly]");
			coefVarTxt.setForeground(Color.RED);
			coefVarTxt.setToolTipText("*** Anomaly: value > 1.9%");
			probablyRandom = false;
		}
		else {
			coefVarTxt.setForeground(defaultForeground);
			coefVarTxt.setToolTipText(null);
			System.out.println();
		}

		System.out.println("Chi-Square Test = " + chi_squared);
		chiSquaredTxt.setText(round(chi_squared));
		
		double avgbyte = bytesum / filelen;
		System.out.print("Average bytes value = " + avgbyte + " (127.5 random)");
		avgBytesValue.setText(round(avgbyte - 127.5));
		if(avgbyte < 127.1 || avgbyte > 127.9) {
			System.out.println(" [*** anomaly]");
			avgBytesValue.setForeground(Color.RED);
			avgBytesValue.setToolTipText("*** Anomaly: average < 127.1 or average > 127.9");
			probablyRandom = false;
		}
		else {
			avgBytesValue.setForeground(defaultForeground);
			avgBytesValue.setToolTipText(null);
			System.out.println();
		}

		entropyTxt.setText(round(entropy) +  " bits");
		System.out.print("Entropy = " + entropy + " bits (8 random)");
		if(filelen > 0 && entropy < 7.99) {
			System.out.println(" [*** anomaly]");
			entropyTxt.setForeground(Color.RED);
			entropyTxt.setToolTipText("*** Anomaly: entropy < 7.99");
			probablyRandom = false;
		}
		else {
			entropyTxt.setForeground(defaultForeground);
			entropyTxt.setToolTipText(null);
			System.out.println();
		}
		
		double e = Math.round(entropy * 1000.0d) / 1000.0d;
		double possibleLen = (filelen * e) / 8.0d;
		System.out.print("Estimated Compressed Length = " + Math.round(Math.ceil(possibleLen)));
		compressedLen.setText(Long.toString(Math.round(Math.ceil(possibleLen))));
		if(possibleLen < filelen) {
			System.out.println(" [*** anomaly]");
			compressedLen.setForeground(Color.RED);
			compressedLen.setToolTipText("*** Anomaly: possible len less than file length");
			probablyRandom = false;
		}
		else {
			compressedLen.setForeground(defaultForeground);
			compressedLen.setToolTipText(null);
			System.out.println();
		}

		if(numSamples2D != 0) {
    		double mcpi = (4.0d * pointsInsideCircle) / numSamples2D;
    		double mcpi_err = 100.0d * (Math.abs(Math.PI - mcpi) / Math.PI);
    		monteCarloPi2D.setText(String.format("%.12f", mcpi));
    		monteCarloPi2DErr.setText(String.format("%.8f%%", mcpi_err));
    		System.out.print("Monte Carlo for Pi 2D = " + mcpi + " (error = " + mcpi_err + "%)");
    		if(mcpi_err > 0.4) {
    			System.out.println(" [*** anomaly]");
    			monteCarloPi2D.setForeground(Color.RED);
    			monteCarloPi2DErr.setForeground(Color.RED);
    			String tooltip = "*** Anomaly: error > 0.4%";
    			monteCarloPi2DErr.setToolTipText(tooltip);
    			monteCarloPi2D.setToolTipText(tooltip);
    			probablyRandom = false;
    		}
    		else {
    			monteCarloPi2D.setForeground(defaultForeground);
    			monteCarloPi2DErr.setForeground(defaultForeground);
    			monteCarloPi2DErr.setToolTipText(null);
    			monteCarloPi2D.setToolTipText(null);
    			System.out.println();
    		}
		}
		else {
			monteCarloPi2D.setText("");
			monteCarloPi2DErr.setText("");
			monteCarloPi2D.setForeground(defaultForeground);
			monteCarloPi2DErr.setForeground(defaultForeground);
			monteCarloPi2DErr.setToolTipText(null);
			monteCarloPi2D.setToolTipText(null);
		}
		
		if(numSamples3D != 0) {
    		double mcpi = 6.0d / numSamples3D * pointsInsideSphere;
    		double mcpi_err = 100.0d * (Math.abs(Math.PI - mcpi) / Math.PI);
   
    		monteCarloPi3D.setText(String.format("%.12f", mcpi));
    		monteCarloPi3DErr.setText(String.format("%.8f%%", mcpi_err));
    		System.out.print("Monte Carlo for Pi 3D = " + mcpi + " (error = " + mcpi_err + "%)");
    		if(mcpi_err > 0.4) {
    			System.out.println(" [*** anomaly]");
    			monteCarloPi3D.setForeground(Color.RED);
    			monteCarloPi3DErr.setForeground(Color.RED);
    			String tooltip = "*** Anomaly: error > 0.4%";
    			monteCarloPi3DErr.setToolTipText(tooltip);
    			monteCarloPi3D.setToolTipText(tooltip);
    			probablyRandom = false;
    		}
    		else {
    			monteCarloPi3D.setForeground(defaultForeground);
    			monteCarloPi3DErr.setForeground(defaultForeground);
    			monteCarloPi3D.setToolTipText(null);
    			monteCarloPi3DErr.setToolTipText(null);
    			System.out.println();
    		}
		}
		else {
			monteCarloPi3D.setText("");
			monteCarloPi3DErr.setText("");
			monteCarloPi3D.setForeground(defaultForeground);
			monteCarloPi3DErr.setForeground(defaultForeground);
			monteCarloPi3D.setToolTipText(null);
			monteCarloPi3DErr.setToolTipText(null);
		}
		
		if(filelen > 1) {
			double apb = 32767.5 - (sumPairBytes / (filelen - 1));
			double apb_err = Math.abs(100 * apb / 32767.5);
			avgPairBytesTxt.setText(round(apb));
			avgPairBytesErrTxt.setText(round(apb_err) + "%");
			System.out.print("Average of Contiguous Byte Pairs = " + sumPairBytes / (filelen - 1) + " (32767.5 random) (error " + apb_err + "%)");
			if(apb_err > 0.03) {
				System.out.println(" [*** anomaly]");
				avgPairBytesTxt.setForeground(Color.RED);
				avgPairBytesErrTxt.setForeground(Color.RED);
				String tooltip = "*** Anomaly: error > 3%";
				avgPairBytesTxt.setToolTipText(tooltip);
				avgPairBytesErrTxt.setToolTipText(tooltip);
				probablyRandom = false;
			}
			else {
				avgPairBytesTxt.setForeground(defaultForeground);
				avgPairBytesErrTxt.setForeground(defaultForeground);
				avgPairBytesTxt.setToolTipText(null);
				avgPairBytesErrTxt.setToolTipText(null);
				System.out.println();
			}
		}
		else {
			avgPairBytesTxt.setText("");
			avgPairBytesErrTxt.setText("");
			avgPairBytesTxt.setForeground(defaultForeground);
			avgPairBytesErrTxt.setForeground(defaultForeground);
			avgPairBytesTxt.setToolTipText(null);
			avgPairBytesErrTxt.setToolTipText(null);
		}

		collisionsTxt.setText(Long.toString(collisionsCounter));
		double collsExp = expectedCollisions32(filelenDiv4);
		System.out.print("4 Bytes Collisions = " + collisionsCounter + " (expected collisions = " + collsExp + ")");
		if(collisionsCounter < collsExp * 0.9 - 1 || collisionsCounter > collsExp * 1.1 + 1) {
			System.out.println(" [*** anomaly]");
			collisionsTxt.setForeground(Color.RED);
			collisionsTxt.setToolTipText("*** Anomaly: collisions < " + (long)Math.floor(collsExp * 0.9 - 1) + " or collisions > " + (long)Math.floor(collsExp * 1.1 + 1));
			probablyRandom = false;
		}
		else {
			collisionsTxt.setForeground(defaultForeground);
			collisionsTxt.setToolTipText(null);
			System.out.println();
		}
		
		collisionsExpTxt.setText(round(collsExp));
	
		if(filelen > 100000.0d) {
			// Write result only if the file length is > 100 kB
    		String text;
        	if(probablyRandom) {
        	   	text = "Probably (pseudo-)random";
        	}
        	else {
        	   	text = "Non-random";
        	}
        	System.out.println("Result = " + text);
        	randomTxt.setText("<html><h3>" + text + "</h3></html>");
		}
		else {
			// Otherwise, writes nothing
			randomTxt.setText("                           ");
		}
		System.out.println();
		setCursor(Cursor.getDefaultCursor());
	}
	
	private static String round(double d) {
		return String.format("%.3f", Math.round(d * 1000.0d) / 1000.0d);
	}
	
	/**
	 * Return the expected collisions for n distinct values
	 * @return
	 */
	private static double expectedCollisions32(long n) {
		// n-m*(1-((m-1)/m)^n) where m = 2^32
		double r = 4294967295d / 4294967296d;
		return n - 4294967296d * (1 - Math.pow(r, n));
	}

}
