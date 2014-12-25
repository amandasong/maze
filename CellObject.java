// ==============================================================
//  COMS 3137 Spring 2014
//  Homework 2 - Programming Problem 1
//
//  Finding and displaying maze paths
//  ---Custom object: CellObject - represents a cell in the maze
//  that is either the start, goal, or an obstacle
//
//
//  By Amanda Song (UNI: as4513)
// ===============================================================

import java.lang.Math;

public class CellObject {

        private int xCoord;
        private int yCoord;
        private int distance;
        private Boolean visited;
        private Boolean isObstacle;

        // see readMe for reasons for these initial values
        CellObject(){
                xCoord = 10;
                yCoord = 10;
                distance = 0;
                visited = false;
                isObstacle = false;
        }

        CellObject(int x, int y){
                xCoord = x;
                yCoord = y;
                distance = 0;
                visited = false;
                isObstacle = false;
        }

        public void setDistance(int n, Boolean b){
                distance = n;
                visited = b;
        }
                
        public void setObstacle(Boolean b){
                isObstacle = true;
        }
                
        public void setCoord(int x, int y){
                xCoord = x;
                yCoord = y;
        }
                
        public int getXCoord(){
                return xCoord;  
        }
         
        public int getYCoord(){
                return yCoord;
        }
                
        public int getDistance(){
                return distance;
        }
                
        public Boolean getVisitedStatus(){
                return visited;
        }
                
        public Boolean isObstacle(){
                return isObstacle;
        }       
        
        public String toString(){
                String result = " ";
                result = "X coord: " + xCoord + " Y coord: "
                      + yCoord + " Distance: " + distance;
                return result;
        }
}            
