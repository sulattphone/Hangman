public class EmptyGuessException extends Exception
{
   private String message;
   public EmptyGuessException()
   {
      super();
      message = "Error: Empty Guess Field";
   }
   
   public String getMessage()
   {
      return message;
   }
}