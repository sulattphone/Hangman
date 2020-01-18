import java.util.*;
import java.io.*;

public class Test
{
   private static ArrayList<String> wordsList;
   public static void main(String[] args)
   {
      try
      {
         String s = chooseWord();
         System.out.println(s);
      }
      catch(FileNotFoundException e)
      {
         
      }
   }
   
   public static String chooseWord() throws FileNotFoundException
   {
      File newFile = new File("words.txt");
      Scanner inputFile = new Scanner(newFile);
      wordsList = new ArrayList<String>();
      
      int count = 0;    //debugging
      boolean hasLine = inputFile.hasNextLine();
      
      while(hasLine)
      {
         String s = inputFile.nextLine();
         //System.out.println(s);      //debugging
         wordsList.add(s);
         hasLine = inputFile.hasNextLine();
        // count++;          //debugging
      }
      
      int random = (int) (Math.random()*wordsList.size())+1;
      System.out.println(random);         //debugging
      
      return wordsList.get(random);
      //System.out.println("count: "+ count);        //debugging
   }
}