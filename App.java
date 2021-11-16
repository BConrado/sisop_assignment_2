/**
 * Bruno Conrado
 * Lucas Salbego
 * SISOP 21/02
 * Fabiano Hessel
 */

import java.util.*;
import java.io.*;

public class App {

  private static Scanner in = new Scanner(System.in);
  private static String msg = "ESPAÇO INSUFICIENTE DE MEMÓRIA";


  private static ArrayList<process> commands = new ArrayList<process>();

  private static int SIZE_MAIN_MEMORY; 

  private static int method = -1; // 0 - FIXED
                           // 1 - VARIABLE

  // 0 - FIXED
  private static int fixedPartitionSize; // IF FIXEX PARTITIONS
  private static int [] partitions; 

  // 1 - VARIABLE
  private static int variablePolicy = -1; // 0 - FIRST FIT 
                                          // 1 - WORST FIT
  private static String [] variableArray;


  public static void setTamanho(){
    SIZE_MAIN_MEMORY = in.nextInt();
    variableArray = new String [SIZE_MAIN_MEMORY]; 
    /*for(int i = 0; i< SIZE_MAIN_MEMORY; i++ ){
      variableArray[i] = "";
    }*/
  }

  public static void setPartitions(){
    int partitionTam = SIZE_MAIN_MEMORY/fixedPartitionSize;

    partitions = new int [partitionTam];

    for (int i = 0; i < partitions.length; i++) {
      partitions[i] = fixedPartitionSize;
    }
  }

  public static void chooseMethod() {
    System.out.println("Type the size of the memory (power of 2)"); 
    setTamanho();

    System.out.println("Choose your strategy"); 
    System.out.println("0 - Fixed partitions");
    System.out.println("1 - Variable partitions");
    
    method = in.nextInt();
    

    if (method == 0){
      System.out.println("Type the size of the partitions (Fixed)"); 
      fixedPartitionSize = in.nextInt();

      setPartitions();
    } 
    if (method == 1) {
      System.out.println("Choose the policy for variable partitions"); 
      System.out.println("0 - First-Fit");
      System.out.println("1 - Worst-Fit");
      variablePolicy = in.nextInt();
    }

  }

  public static void leArquivo(String arq) throws FileNotFoundException{
    File f = new File(arq);
    Scanner in = new Scanner(f);

    while (in.hasNextLine()) {
      String line = in.nextLine();
    
    

      if(line.substring(0, 2).equals("IN")){
        String process = line.substring(3,4);
        int size = Integer.parseInt(line.substring(5,6));

        process a = new process(process, size, "IN");

        commands.add(a);
        
        
      } else{
        String process = line.substring(4,5);
        
        process b = new process(process, 0, "OUT");

        commands.add(b);
      }
    }
  }


  public static void printAvailableFixed (){ 
    int sum = 0;
    for(int i = 0; i < partitions.length; i++) {
      if (partitions[i] == fixedPartitionSize) {
        sum += partitions[i];
      }
      else {
        if (partitions[i] != 0) {
          System.out.print("| " + partitions[i] + " |" );
        }
      }
    }
    if ( sum != 0) {
      System.out.print("| " + sum + " |" );
    }
    System.out.println();
  }

  public static void fixedMethod() {
    for (process process : commands) {
      int verify = -1;
      if(process.getOp() == "IN") {
        for(int i =0; i < partitions.length; i++) {
          if (partitions[i] == fixedPartitionSize){ 
            partitions[i] -= process.getSize();
            process.setIndex(i);
            verify = 1;
            break;
          }
        }
        if (verify == -1) {
          System.out.println(msg);
        }
      }else {
        
        int indice = findIndex(process.getName());
        partitions[indice] += findValue(process.getName());
      }
      printAvailableFixed();
    }
  }

  public static int findIndex(String name) {
    int index = -1;
    for (process process : commands) {
      if (process.getName().equals(name) && process.getPartitionIndex() != -1) {
        index = process.getPartitionIndex();
        break;
      }
    }
    return index;
  }

  public static int findValue(String name){
    int value = 0;
    for (process process : commands) {
      if (process.getName().equals(name) && process.getPartitionIndex() != -1) {
        value = process.getSize();
        break;
      }
    }
    return value;
  }

  public static void exec(){
    if (method == 0) { // fixed
      fixedMethod();
    }
    if (method == 1){ // variable
      if (variablePolicy == 0) { // first fit
        firstFit();
      }
      else { // worst fit
        worstFit();
      }
    }
  }

  public static void firstFit() {
    for (process process : commands) {
      int count = 0;
      int posParada = 0;
      int verify = -1;
      String name = process.getName();
      int size = process.getSize();
      if(process.getOp() == "IN") {
        for (int i = 0; i < variableArray.length; i++ ){
          if (count == size) {
              int pos = (posParada + 1) - count;
              for (int x = 0; x < count; x++) {
                variableArray[pos+x] = name;
              }         
              verify = 1;
              break;
          } else if(variableArray[i] == null){
            count++;
            posParada = i;           
          }else {
            count = 0;
          }
          
        }
        if(verify == -1) {
          System.out.println(msg);
        }
      }
      else {
        for (int i = 0; i < variableArray.length; i++ ){
          if(variableArray[i] != null && variableArray[i].equals(process.getName())) {
            variableArray[i] = null;
          }
        }
      }
      printAvailableVariable();
    }
  }

  public static void worstFit() {
    for (process process : commands) {
      int count = 0;
      int posParada = 0;
      int verify = -1;
      int bestCount = 0;
      int bestPos = 0;
      String name = process.getName();
      int size = process.getSize();
      if(process.getOp() == "IN") {
        for (int i = 0; i < variableArray.length; i++ ){
          if (variableArray[i]==null) {
            count++;
          } 
          if (variableArray[i] != null ) {
            posParada = i+1;
            if (bestCount < count && count >=size){
              bestCount = count;
              bestPos = (posParada+1) -count;
              count = 0;
              verify = 1;
            }else {
              count = 0;
              
            }
          }
          if (i == variableArray.length-1) {
            if (bestCount < count && count >=size){
              bestCount = count;
              bestPos =posParada;
              count = 0;
              verify = 1;
            }else {
              count = 0;
              
            }
          }             
        }
       
        if (verify == 1) {
          int pos = bestPos;        

          for (int x = 0; x < size; x++) {
            variableArray[pos+x] = name;
          }   
        }
   
        if(verify == -1) {
          System.out.println(msg);
        }
      }
      else {
        for (int i = 0; i < variableArray.length; i++ ){
          if(variableArray[i] != null && variableArray[i].equals(process.getName())) {
            variableArray[i] = null;
          }
        }
      }
      printAvailableVariable();
    }
  }

  public static void printAvailableVariable(){
    int emptySpaces = 0;
    for (int i = 0; i < variableArray.length; i++) {
      if (variableArray[i] == null) {
        emptySpaces++;
      } 
      else if (emptySpaces > 0){
        System.out.print("| " + emptySpaces + " |");
        emptySpaces = 0;
      }

      if (i == variableArray.length-1) { // 16
        System.out.print("| " + emptySpaces + " |");
      }
      
    }
    System.out.println();
  }

  public static void main (String[] args) throws FileNotFoundException {
    //leArquivo(args[0]); // le o arquivo e salva num array em ordem de leitura, para executcao dos INs / OUTs
    leArquivo(args[0]);
    chooseMethod(); // escolhe o metodo, tamanhos e politicas
    
    if (method == 0) {
      printAvailableFixed(); // print
    } else {
      printAvailableVariable(); // print
    }
    

    exec();
  }
}