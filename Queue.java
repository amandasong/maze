// ==============================================================
//  COMS 3137 Spring 2014
//  Homework 2 - Programming Problem 1
//
//  Finding and displaying maze paths
//  ---Queue class (with code from class material)
//
//
//  By Amanda Song (UNI: as4513)
// ===============================================================

import java.util.*;

public class Queue {
        private LinkedList queue;

        public Queue(){
                queue = new LinkedList();
        }

        public Boolean isEmpty(){
                return queue.isEmpty();
        }

        public void enQueue(CellObject c){
                queue.addLast(c);
        }

        public CellObject deQueue(){
                if(queue.isEmpty()){
                        System.out.println("Queue is empty.");
                        return null;
                }
                else{
                        CellObject result = (CellObject) queue.getFirst();
                                queue.removeFirst();
                                return result;
                }
                    }
        
        public void printQueue(){
                System.out.println(queue.toString());
        }
}
        
