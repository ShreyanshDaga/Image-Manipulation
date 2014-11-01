
/////////////////////////////////////////////
//  Author:            Shreyansh Daga
//  USC ID:            6375-3348-33
//  Email:              sdaga@usc.edu
//  Assignment:     CSCI_576_Assignment_1
//  File:                 CSCI_Assognment_1.java
//  OS:                  Windows 8.1
//  IDE:                  NetBeans 7.4
//  Date:               2/16/2014
////////////////////////////////////////////

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import javax.swing.*;

public class CSCI_576_Assignment_1 {

    //Frames to display two windows one for input and one for output
    static JFrame frame_1;// = new JFrame();
    static JFrame frame_2;// = new JFrame();
    
    //Label to display The images in form of icon
    static JLabel imOp;
    static JLabel imIp;

    public static void main(String[] args) throws InterruptedException {
        String fileName = args[0];
        int iAngle = Integer.parseInt(args[1]) + 1;
        float fScale = Float.parseFloat(args[2]);
        int iAA = Integer.parseInt(args[3]);
        int iFps = 1;
        int iTime = 1;

        //If Animation also included
        if (args.length > 4) {
            iFps = Integer.parseInt(args[4]);
            iTime = Integer.parseInt(args[5]);
        }

        BufferedImage imgIP = ReadImage(fileName, 512, 512);
        BufferedImage[] imgOP = new BufferedImage[iFps * iTime];

        //Init JFrame and JLabel
        frame_1 = new JFrame();
        frame_2 = new JFrame();

        frame_1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame_2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        imIp = new JLabel(new ImageIcon(imgIP));
        imOp = new JLabel();

        frame_1.getContentPane().add(imIp, BorderLayout.CENTER);
        frame_1.pack();

        frame_1.setVisible(true);
        
        if (iFps * iTime == 1) {
            imgOP[0] = ShowImageOutput(imgIP, iAngle, fScale, iAA);
            System.out.println("Displaying Output.!");
            imOp.setIcon(new ImageIcon(imgOP[0]));
            frame_2.getContentPane().add(imOp, BorderLayout.CENTER);
            frame_2.setLocation(frame_1.getWidth(), 0);
            frame_2.pack();
            frame_2.setVisible(true);
        } else {
            imgOP = AnimateImage(imgIP, iAngle, fScale, iAA, iTime * iFps);
            
            System.out.println("Displaying Output.!");
            for (int i = 0; i < iFps * iTime; i++) {
                imOp.setIcon(new ImageIcon(imgOP[i]));
                frame_2.getContentPane().removeAll();
                frame_2.getContentPane().add(imOp, BorderLayout.CENTER);
                frame_2.setLocation(frame_1.getWidth(), 0);
                frame_2.pack();
                frame_2.setVisible(true);
                try {
                    Thread.sleep((int) (1000.00 / (float) iFps));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    //For Static Image Output
    public static BufferedImage ShowImageOutput(BufferedImage imgIP, int iAngle, float fScale, int iAA) {

        BufferedImage imgOP;

        if (iAA == 0) {
            
            imgOP = Scale(RotateImg(imgIP, iAngle), 1 / fScale);
        } else {
            imgOP = Scale(RotateImg(Filter(imgIP,3), iAngle), 1 / fScale);            
        }

        return imgOP;
    }

    //For Animation
    public static BufferedImage[] AnimateImage(BufferedImage imgIP, int iAngle, float fScale, int iAA, int iFrames) throws InterruptedException {
        BufferedImage[] imgOPs = new BufferedImage[iFrames];
        BufferedImage imgTemp;

        if (iAA == 1) {
            imgTemp = Filter(imgIP, 3);
        } else {
            imgTemp = imgIP;
        }
        
        System.out.println("Generating Image Frames...");
        fScale = 1/fScale;       
        
        for (int i = 0; i < iFrames; i++) {            
            System.out.println("Percent Complete: " + ((i+1)/(double)iFrames*100) + "%");
            imgOPs[i] = Scale(RotateImg(imgTemp,(double) (i * iAngle )/ ((double) (iFrames-1))),fScale*(i+1)/iFrames);                        
        }
        
        System.out.println("Generation Complete !!");
        return imgOPs;
    }

    //Reads the raw SGI image
    public static BufferedImage ReadImage(String strImageName, int iW, int iH) {
        BufferedImage imgNew = new BufferedImage(iW, iH, BufferedImage.TYPE_INT_RGB);

        try {
            File file = new File(strImageName);
            InputStream is = new FileInputStream(file);

            long len = file.length();
            byte[] bytes = new byte[(int) len];

            int offset = 0;
            int numRead = 0;

            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            int ind = 0;

            for (int y = 0; y < iH; y++) {
                for (int x = 0; x < iW; x++) {
                    //byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind + iH * iW];
                    byte b = bytes[ind + iH * iW * 2];

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                    imgNew.setRGB(x, y, pix);
                    ind++;
                    //System.out.println("Ind: " + ind + " X: " + x + " Y: " + y);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgNew;
    }

    //Rotate Image Function
    public static BufferedImage RotateImg(BufferedImage imgIP, double dAngle) {
        BufferedImage imgOP = null;
        int iH = imgIP.getHeight();
        int iW = imgIP.getWidth();
        double dAngR = dAngle / 180.00 * 3.14159;

        int dH = 724;//(int) (sin(Math.toRadians(45 + dAngle)) * 724);
        int dW = 724;//(int) (cos(Math.toRadians(dAngle - 45)) * 724);

        imgOP = new BufferedImage(dW, dH, BufferedImage.TYPE_INT_RGB);

        // Drawing the rotated image at the required drawing locations       
        for (int i = 0; i < dH; i++) {
            for (int j = 0; j < dW; j++) {
                double i_shift = i - dH / 2.0;
                double j_shift = j - dW / 2.0;
                double i_rot = i_shift * cos(dAngR) + j_shift * sin(dAngR);
                double j_rot = i_shift * sin(-dAngR) + j_shift * cos(dAngR);
                int i_new = (int) (i_rot + iH / 2.0);
                int j_new = (int) (j_rot + iW / 2.0);

                if (i_new < 0 || j_new < 0 || i_new >= iH || j_new >= iW) {
                    imgOP.setRGB(j, i, 0);
                } else {
                    imgOP.setRGB(j, i, BilinearInter(imgIP,j_new, i_new));
                }
            }
        }
        return imgOP;
    }

    //Window Averaging Filter
    public static BufferedImage Filter(BufferedImage imgIP, int iWinSize) {
        int iW = imgIP.getWidth();
        int iH = imgIP.getHeight();
        int iEx = (iWinSize - 1) / 2;

        System.out.println("\nAntiAlising Filter of window 3x3...");
        //Extending the image by the window size used for averaging the pixels
        BufferedImage imgExt = ImExtend(imgIP, iWinSize);
        BufferedImage imgOP = new BufferedImage(iW, iH, imgIP.getType());

        for (int i = iEx; i < iH + iEx; i++) {
            for (int j = iEx; j < iW + iEx; j++) {
                int R = 0, G = 0, B = 0;
                for (int l = 0; l < iWinSize; l++) {
                    for (int m = 0; m < iWinSize; m++) {
                        int iR = (imgExt.getRGB(l + i - iEx, m + j - iEx) >> 16) & 0x000000FF;
                        int iG = (imgExt.getRGB(l + i - iEx, m + j - iEx) >> 8) & 0x000000FF;
                        int iB = imgExt.getRGB(l + i - iEx, m + j - iEx) & 0x000000FF;

                        R += iR;
                        G += iG;
                        B += iB;
                    }
                }
                
                //Averaging the pixels in the window of WinSize
                R /= (iWinSize * iWinSize);
                G /= (iWinSize * iWinSize);
                B /= (iWinSize * iWinSize);

                int iPixel = 0xff000000 | ((R & 0xff) << 16) | ((G & 0xff) << 8) | (B & 0xff);

                imgOP.setRGB(i - iEx, j - iEx, iPixel);
            }
        }
        System.out.println("\nAntiAlising Complete !!");
        return imgOP;
    }

    //Scale Image Function
    public static BufferedImage Scale(BufferedImage imgIP, float fScale) {
        int iW = imgIP.getWidth();
        int iH = imgIP.getHeight();

        BufferedImage imgOP = new BufferedImage(iW, iH, imgIP.getType());

        for (int i = 0; i < iH; i++) {
            for (int j = 0; j < iW; j++) {
                imgOP.setRGB(i, j, 0);
            }
        }

        for (int i = 0; i < iH; i++) {
            for (int j = 0; j < iW; j++) {
                double i_shift = i - iH / 2.0;
                double j_shift = j - iW / 2.0;
                double i_scale = i_shift * fScale;
                double j_scale = j_shift * fScale;
                int i_new = (int) (i_scale + iH / 2.0);
                int j_new = (int) (j_scale + iW / 2.0);

                if (i_new < 0 || j_new < 0 || i_new >= 724 || j_new >= 724) {
                    imgOP.setRGB(j, i, 0);
                } else {
                    imgOP.setRGB(j, i, BilinearInter(imgIP,j_new, i_new));
                }
            }
        }

        return imgOP;
    }

    //Boundary Extension for AA filter
    public static BufferedImage ImExtend(BufferedImage imgIP, int iWinSize) {
        int iH = imgIP.getHeight();
        int iW = imgIP.getWidth();
        int iEx = (iWinSize - 1) / 2;

        int iNewH = iH + iEx * 2;
        int iNewW = iW + iEx * 2;

        BufferedImage imgNew = new BufferedImage(iNewW, iNewH, imgIP.getType());

        //Center
        for (int i = iEx; i < iNewH - iEx; i++) {
            for (int j = iEx; j < iNewW - iEx; j++) {
                imgNew.setRGB(i, j, imgIP.getRGB(i - iEx, j - iEx));
            }
        }

        //Corners
        for (int i = 0; i < iEx; i++) {
            for (int j = 0; j < iEx; j++) {
                //Top Left
                imgNew.setRGB(i, j, imgIP.getRGB(0, 0));

                //Top Right
                imgNew.setRGB(i, j + iW + iEx, imgIP.getRGB(0, iW - 1));
                //imgNew.setRGB(i, j + iW + iEx, 0x00FF0000);

                //Bottom Left
                imgNew.setRGB(i + iH + iEx, j, imgIP.getRGB(iH - 1, 0));
                //imgNew.setRGB(i + iH + iEx, j, 0x0000FF00);

                //Bottom Right
                imgNew.setRGB(i + iH + iEx, j + iW + iEx, imgIP.getRGB(iH - 1, iW - 1));
                //imgNew.setRGB(i + iH + iEx, j + iW + iEx, 0x000000FF);
            }
        }

        //Left Column
        for (int i = 0; i < iH; i++) {
            int iRGB = imgIP.getRGB(i, 0);
            for (int j = 0; j < iEx; j++) {
                imgNew.setRGB(i + iEx, j, iRGB);
            }
        }

        //Right Column
        for (int i = 0; i < iH; i++) {
            int iRGB = imgIP.getRGB(i, iW - 1);
            for (int j = iW + iEx; j < iW + iEx * 2; j++) {
                imgNew.setRGB(i + iEx, j, iRGB);
            }
        }

        //Top Row
        for (int j = 0; j < iW; j++) {
            int iRGB = imgIP.getRGB(0, j);
            for (int i = 0; i < iEx; i++) {
                imgNew.setRGB(i, j + iEx, iRGB);
            }
        }

        //Bottom Row
        for (int j = 0; j < iW; j++) {
            int iRGB = imgIP.getRGB(iH - 1, j);
            for (int i = iH + iEx; i < iH + iEx * 2; i++) {
                imgNew.setRGB(i, j + iEx, iRGB);
            }
        }

        return imgNew;
    }
    
    //Bilinear Interpolation
    public static int BilinearInter(BufferedImage imgIP, double dX, double dY)
    {
        int iX1 = (int) dX;
        int iX2 = iX1 + 1;
        int iY1 = (int) dY;
        int iY2 = iY1 + 1;
        
        if(iX1 == imgIP.getHeight() - 1 ||iY1 == imgIP.getWidth() - 1)
            return imgIP.getRGB(iX1, iY1);
        
        int iR1 = (imgIP.getRGB(iX1, iY1)>>16) & 0x000000FF;
        int iG1 = (imgIP.getRGB(iX1, iY1)>>8) & 0x000000FF;
        int iB1 = (imgIP.getRGB(iX1, iY1)) & 0x000000FF;
        
        int iR2 = (imgIP.getRGB(iX1, iY2)>>16) & 0x000000FF;
        int iG2 = (imgIP.getRGB(iX1, iY2)>>8) & 0x000000FF;
        int iB2 = (imgIP.getRGB(iX1, iY2)) & 0x000000FF;
        
        int iR3 = (imgIP.getRGB(iX2, iY2)>>16) & 0x000000FF;
        int iG3 = (imgIP.getRGB(iX2, iY2)>>8) & 0x000000FF;
        int iB3 = (imgIP.getRGB(iX2, iY2)) & 0x000000FF;
        
        int iR4 = (imgIP.getRGB(iX2, iY1)>>16) & 0x000000FF;
        int iG4 = (imgIP.getRGB(iX2, iY1)>>8) & 0x000000FF;
        int iB4 = (imgIP.getRGB(iX2, iY1)) & 0x000000FF;
                
        double dDiffX = dX - iX1;
        double dDiffY = dY - iY1;
        
        int iR =(int) (iR1*(1 - dDiffX)*(1 - dDiffY) + iR2*(1 - dDiffX)*(dDiffY) + iR3*(dDiffX)*(dDiffY) + iR4*(dDiffX)*(1 - dDiffY));
        int iG =(int) (iG1*(1 - dDiffX)*(1 - dDiffY) + iG2*(1 - dDiffX)*(dDiffY) + iG3*(dDiffX)*(dDiffY) + iG4*(dDiffX)*(1 - dDiffY));
        int iB = (int) (iB1*(1 - dDiffX)*(1 - dDiffY) + iB2*(1 - dDiffX)*(dDiffY) + iB3*(dDiffX)*(dDiffY) + iB4*(dDiffX)*(1 - dDiffY));
        
        int iRGB = (iR & 0xFF)<<16 | (iG & 0xFF)<<8 | iB & 0xFF;
        
        return iRGB;        
    }
}
