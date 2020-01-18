public class GuessCharacterException extends Exception
{
   private String message;
   
   public GuessCharacterException(String s)
   {
      super();
      message = s;
   }
   
   public String getMessage()
   {
      return message;
   }
}