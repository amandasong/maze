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

import java.util.*;
        
import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.*;
    
public class MazeShell implements ActionListener{
        
    private CellObject start;
    private CellObject goal;
    private CellObject[][] cellArray = new CellObject[8][8];
    private Boolean pathsExist;

        // Fields
        private JFrame frame;
        private DrawingCanvas canvas;
        private JTextArea messageArea;
        private Color color; 
        private final int CELL_SIZE=30;
        private File file;
    
        private BufferedReader diskInput;
        private boolean pathfound=false;
        final int WIDTH=8;
        final int HEIGHT=WIDTH;
    
        // To store original ascii maze values
        private String[][] mazeArray = new String[8][8];

        // To store distances for each cell
        private int[][] distanceArray = new int[8][8];


  public MazeShell(File f){

          file=f;
          frame = new JFrame("Maze");
          frame.setSize(500, 500);

          //The graphics area
          canvas = new DrawingCanvas();
          frame.getContentPane().add(canvas, BorderLayout.CENTER);

    //The message area, mainly for debugging.
    messageArea = new JTextArea(1, 80);     
    //one line text area for messages.
    frame.getContentPane().add(messageArea, BorderLayout.SOUTH);

    JPanel buttonPanel = new JPanel(new java.awt.GridLayout(2,0));
    frame.getContentPane().add(buttonPanel, BorderLayout.NORTH);

    addButton(buttonPanel, "Draw Initial Grid").setForeground(Color.black);
    addButton(buttonPanel, "Calculate Distances").setForeground(Color.black);
    addButton(buttonPanel, "Show Path").setForeground(Color.black);
    addButton(buttonPanel, "Quit").setForeground(Color.black);

    frame.setVisible(true);
  }

  /** Helper method for adding buttons */
  private JButton addButton(JPanel panel, String name){
    JButton button = new JButton(name);
    button.addActionListener(this);
    panel.add(button);
    return button;
  }

 /** Respond to button presses */
    
  public void actionPerformed(ActionEvent e){
    String cmd = e.getActionCommand();
    if (cmd.equals("Draw Initial Grid") ){
       initialize();
       return;
    }  else
    if (cmd.equals("Calculate Distances") ){
       pathfound = calcDistances();
       return;
    }  else
    if(cmd.equals("Show Path")) {
        if (pathfound) outputPath();   
           else messageArea.insert("no Path found!",0);
        return;
    } else
    if (cmd.equals("Quit") ){
       frame.dispose();
        return;
    } else
        throw new RuntimeException("No such button: "+cmd);
  }
    
    public void initialize(){
       
       canvas.clear();
       drawGrid();
       
       try{   
          diskInput = new BufferedReader(new InputStreamReader(
                  new FileInputStream(file)));
        
          messageArea.insert("Maze File Name: "+ file.toString(),0);
        
          // Reading the ascii matrix and entering into a 2D array
         
          String[] oneLine = new String[8];
          String line = "";
          String element;
          int obstacleCounter = 0;
        
          // Array of obstacles: each index contains a CellObject obstacle
          // storing the x-coordinate and y-coordinate of its location
          CellObject[] obstacleArray = new CellObject[64];
       
          int obIndex = 0;
          for(int i = 0; i < 8; i++){
                  oneLine[i] = diskInput.readLine();
                  Scanner in = new Scanner(oneLine[i]);
                  for (int j = 0; j < 8; j++){
                          element = in.next();
                          if(element==" "){
                                  element = in.next();
                          }
                          mazeArray[i][j] = element;
                          CellObject cell = new CellObject(j,i);
                          cellArray[j][i] = cell;

                          if(element.equals("1")){
                                  obstacleCounter++;
                                  CellObject obstacle = new CellObject(j,i);
                                          obstacle.setObstacle(true);
                                          cellArray[j][i] = obstacle; 
                                          obstacleArray[obIndex] = obstacle;              $
                                          obIndex++;
                                  }
                                  if(element.equals("S")){
                                          // storing original start coordinates
                                          start = new CellObject(j,i);
                                  }
                                  if(element.equals("G")||element.equals("T")){
                                          goal = new CellObject(j,i);                     $
                        }
                  }
          }

           // Marking out Start and Goal
          int xCoord;
          int yCoord;
                                          
          xCoord = start.getXCoord();
          yCoord = start.getYCoord();
          drawText("S",xCoord,yCoord,Color.red);
                                   
          xCoord = goal.getXCoord();
          yCoord = goal.getYCoord();
          drawText("G",xCoord,yCoord,Color.red);
                                   
          // Filling cells for obstacles
          for (int i = 0; i < obstacleCounter; i++){
                  CellObject obstacle = obstacleArray[i];
                  xCoord = obstacle.getXCoord();
                  yCoord = obstacle.getYCoord();
                  fillRectangle(xCoord,yCoord,CELL_SIZE,CELL_SIZE,Color.blue);
          }               
       }
       catch (IOException e){
            System.out.println("IO Exception!");
       }                                  
          
       canvas.display();
    }
                                   
    public void drawText(String s, int i, int j,Color color) {
       Color col=color;
       canvas.setForeground(col);
       canvas.setFont(new Font("Helvetica", Font.PLAIN, 10));
       canvas.drawString(s, (i)*CELL_SIZE+10,(j+1)*CELL_SIZE-10,false);
    }
                  
    public void fillRectangle(int i,int j,int height,int width,Color color){
                  
       Color col=color;
       canvas.setForeground(col);
       canvas.fillRect(i*CELL_SIZE,j*CELL_SIZE,height,width,false);
    }
            
    public void drawGrid(){               
          
      color=Color.black;
      canvas.setForeground(color);
                                   
    //draw horizontal grid lines
      for(int i=0;i<9;i++) {
          canvas.drawLine(0,i*CELL_SIZE,WIDTH*CELL_SIZE,i*CELL_SIZE,false);
      }
    //draw vertical grid lines
      for(int i=0;i<9;i++) {
          canvas.drawLine(i*CELL_SIZE,0,i*CELL_SIZE,HEIGHT*CELL_SIZE,false);
      }
  }               
       
    // returns true and displays distances from start to goal if a path exists
    // returns false if no path exists between start and goal
    public boolean calcDistances() {
            
        pathsExist = false;               
        Queue myQueue = new Queue();
        int distanceGoal = goal.getDistance();
        // small CellObject array to hold neighbors of a cell
        CellObject[] neighbors = new CellObject[4];
    
       int currentX = 0;
       int currentY = 0;
       int distance = 0;
       int neighborX, neighborY;
       Boolean coordValid = true;
       int neighborDistance;
           Boolean visited;
           Boolean isObstacle;
           int goalX = goal.getXCoord();
           int goalY = goal.getYCoord();
    
       int startX = start.getXCoord();
       int startY = start.getYCoord();
       CellObject current = new CellObject();
        
       // Enqueuing the start cell, setting distance and marking as visited
       current = new CellObject(startX,startY);
       current.setDistance(distance,true);
       cellArray[startX][startY] = current;
       myQueue.enQueue(current);
       
       while(!myQueue.isEmpty() && coordValid){
       
           // checking that current coordinates are within range
           if(currentX<8 && currentX>=0 && currentY < 8 && currentY >=0){
                   coordValid = true;
           }
           else{
                   coordValid = false;
                   System.out.println("Invalid coord!");
                   System.out.println("Current: " + current.toString());
                   break;
           }
        
           current = myQueue.deQueue();
           distance = current.getDistance();   
           currentX = current.getXCoord();
           currentY = current.getYCoord(); 
           isObstacle = current.isObstacle();
       
           // Initializing neighbor objects
           // with xCoord = 10, yCoord = 10
           // (see readMe for justification)
           CellObject north = new CellObject();
           CellObject south = new CellObject();
           CellObject east = new CellObject();
           CellObject west = new CellObject();
           CellObject neighbor = new CellObject();
           if(currentY-1>=0){
                   north = cellArray[currentX][currentY-1];
           }
           if(currentY+1<=7){
                   south = cellArray[currentX][currentY+1];
           }
           if(currentX+1<=7){
                   east = cellArray[currentX+1][currentY];
           }
           if(currentX-1>=0){
                   west = cellArray[currentX-1][currentY];  
           }
                                        
           neighbors[0] = north;
           neighbors[1] = south;
           neighbors[2] = east;   
           neighbors[3] = west;  

           // For all unvisited neighbors, update distance and enQueue.
           for(int k = 0; k < 4; k++){
                   neighbor = neighbors[k];
                   neighborX = neighbor.getXCoord();
                   neighborY = neighbor.getYCoord();
                   isObstacle = neighbor.isObstacle();
                   visited = neighbor.getVisitedStatus();
                   neighborDistance = neighbor.getDistance();
                   
                   if(neighborX == goalX && neighborY == goalY){
                           // Goal found
                                neighbor.setDistance(distance+1, true);
                                distanceGoal = distance + 1;
                                        cellArray[neighborX][neighborY] = neighbor;
                                        pathsExist = true;
                                        break;
                   }
                   if(pathsExist){
                    break;
                    }
                   if(neighborX!=10){ 
                           if(neighborX==startX && neighborY == startY){
                                   // already visited start point
                                   neighbor.setDistance(0, true);
                                }
                   
                           if(neighborDistance==0 && visited==false && isObstacle==false){
                                   neighbor.setDistance(distance+1, true);
                                   cellArray[neighborX][neighborY] = neighbor;
                                   myQueue.enQueue(neighbor);
                           }
                        }
           }
       }
                                        
       if(pathsExist){
       int cellDistance;
                String distanceString;
                   
                for(int m=0; m<8;m++){
                        for(int n = 0; n < 8; n++){
                                CellObject newObj = cellArray[m][n];
                                cellDistance = newObj.getDistance();
                                if(!newObj.isObstacle() && cellDistance!=0 && cellDistance$
                                        distanceString = Integer.toString(cellDistance);  
                                drawText(distanceString,m,n,Color.black); 
                                }
                        }
                }
                canvas.display();
                return true;
       }
       else{                            
           return false;
       }          
       }
                
    public void outputPath(){ //finds the path between goal and start
                
        //find and display the path between start and goal if it exists
                                
        if(pathsExist){
                int xCoord, yCoord;
                int pathCounter = 0;
                int distance;
                                 
                int currentX, currentY;
                CellObject neighbors[] = new CellObject[4];
                int endX = goal.getXCoord();
                int endY = goal.getYCoord();
                CellObject current = cellArray[endX][endY];
                int distanceGoal = current.getDistance();
           
                CellObject[] pathArray = new CellObject[64];
                for(int i = 0; i < distanceGoal+1; i++){
    
                        distance = current.getDistance();
                        xCoord  = current.getXCoord();
                yCoord = current.getYCoord();
        
                CellObject north = new CellObject();
                CellObject south = new CellObject();
                CellObject east = new CellObject();
                CellObject west = new CellObject();
                
                if(yCoord-1>=0){
                           north = cellArray[xCoord][yCoord-1];
                   }
                   if(yCoord+1<=7){
                           south = cellArray[xCoord][yCoord+1];
                   }
                   if(xCoord+1<=7){
                   east = cellArray[xCoord+1][yCoord];
                   }
                   if(xCoord-1>=0){
                           west = cellArray[xCoord-1][yCoord];
                   }
                neighbors[0] = north;
                neighbors[1] = south;
                neighbors[2] = east;
                neighbors[3] = west;
                
                        for(int j = 0; j<4;j++){   
                                if (neighbors[j].getDistance()==distance-1){
                                        currentX = neighbors[j].getXCoord();
                                        currentY = neighbors[j].getYCoord();
                                        current = cellArray[currentX][currentY];
                                        pathArray[i] = current;
                                        pathCounter++;
                                        break;
                                }
                                }
                        if(pathCounter==distanceGoal-1){
                                break;
                        }
                }   
                
            for (int i = 0; i < pathCounter; i++){
                  CellObject path = pathArray[i];
                  xCoord = path.getXCoord();
                  yCoord = path.getYCoord();
                  fillRectangle(xCoord,yCoord,CELL_SIZE,CELL_SIZE,Color.red);
                                
            }
            canvas.display();
        }
                                        
    }
}
  

 

  







