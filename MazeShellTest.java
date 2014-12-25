
// ==============================================================
//  COMS 3137 Spring 2014
//  Homework 2 - Programming Problem 1
//
//  Finding and displaying maze paths
//  ---Test class
//
//
//  By Amanda Song (UNI: as4513)
// ===============================================================

import java.io.File;
import java.awt.*;
import java.swing.*;

public class MazeShellTest {

        public static void main(String[] args){

            //  args[0] is maze file name from command line

                File file = new File(args[0]);
                MazeShell P = new MazeShell(new File(args[0]));


        }
}
