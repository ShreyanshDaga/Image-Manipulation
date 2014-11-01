**CSCI 576 Assignment 1**

  Author:       Shreyansh Daga
  USC ID:       6375-3348-33
  Email:	sdaga@usc.edu
  Date:         2/16/2014


1) The Project contains 1 file
	
	CSCI_576_Assignment_1.java	- Java file for the program
	
2) Types of Arguments at the Command Prompt

	1) For still image output
	
	>> <image_Name.rgb> <rotation_Angle> <scaling_Factor> <anti_alising_Flag>
	example,
	>> image1.rgb 45 0.5 1

	2) For Animation

	>> <image_Name.rgb> <rotation_Angle> <scaling_Factor> <anti_alising_Flag> <frames_per_Second> <time>
	example,
	>> image1.rgb 720 0.5 1 25 5 (This would be an interesting test case !!)

3) How the program will calcuate output

	->In general there will be two windows, one showing the input and the other showing the output.
	->The program assumes a default value of height and width of 512x512 pixels, which is hardcoded.
	->AntiAliasing filtering is done using a average window filter of 3x3.
	->In case of still image the second window will show the required output.
	->In case of animation, first the program will generate all the frames, and then render it on the screen in 
	  form of animation.
	->For Animation, the program will vary the scaling factor from the minimum to the one specified in the 
	  argument list.

4) This program is developed in NetBeans IDE 7.4 on Windows 8.1