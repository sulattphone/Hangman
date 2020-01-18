import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class HangmanGUI extends Application {

	private String currentWord; // the randomly selected word
	private TextField guessField; // the user enters their guess here
	private Text currentWordText; // show the current word (with - for unguessed letters)
	private Text outcomeText; // show the outcome of each guess and the game
	private Text wrongGuessesText; // show a list of incorrect guesses
	private Text wrongGuessNumberText; // show how many incorrect guesses (or how many guesses remain)
   private Button againButton;
	private final static int MAX_WRONG_GUESSES = 10;
	private static final Color TITLE_AND_OUTCOME_COLOR = Color.rgb(221, 160, 221);
	private static final Color INFO_COLOR = Color.rgb(224, 255, 255);
	private static final Color WORD_COLOR = Color.rgb(224, 255, 255);
   private static Scanner inputFile;
   private static ArrayList<String> wrongGuesses = new ArrayList<String>();
   private static String[] currentDisplay;
   
	public void start(Stage primaryStage) {

		VBox mainVBox = new VBox();
		mainVBox.setStyle("-fx-background-color: royalblue");
		mainVBox.setAlignment(Pos.CENTER);
		mainVBox.setSpacing(10);

		Text welcomeText = new Text("Welcome to Hangman!");
		welcomeText.setFont(Font.font("Helvetica", FontWeight.BOLD, 36));
		welcomeText.setFill(TITLE_AND_OUTCOME_COLOR);
		Text introText1 = new Text("Guess a letter.");
		Text introText2 = new Text("You can make " + MAX_WRONG_GUESSES + " wrong guesses!");
		introText1.setFont(Font.font("Helvetica", 24));
		introText1.setFill(INFO_COLOR);
		introText2.setFont(Font.font("Helvetica", 24));
		introText2.setFill(INFO_COLOR);

		VBox introBox = new VBox(welcomeText, introText1, introText2);
		introBox.setAlignment(Pos.CENTER);
		introBox.setSpacing(10);
		mainVBox.getChildren().add(introBox);

		// create before game is started
		outcomeText = new Text("");
		guessField = new TextField();
		wrongGuessNumberText = new Text("");
      try
      {
		   currentWord = chooseWord();
      }
      catch(FileNotFoundException e)
      {
         System.out.println("File Not Found.");
      }
      
      currentWordText = new Text();
      
      makeCurrentDashes();          //trying to make dashes from the chosen word
      
		wrongGuessesText = new Text("Wrong Guesses: []");

		
		currentWordText.setFont(Font.font("Helvetica", FontWeight.BOLD, 48));
		currentWordText.setFill(WORD_COLOR);
		HBox currentBox = new HBox(currentWordText);
		currentBox.setAlignment(Pos.CENTER);
		currentBox.setSpacing(10);
		mainVBox.getChildren().add(currentBox);

		Text guessIntroText = new Text("Enter your guess: ");
		guessIntroText.setFont(Font.font("Helvetica", 26));
		guessIntroText.setFill(INFO_COLOR);
      
      guessField.setOnAction(this::handleGuessField);
		
		HBox guessBox = new HBox(guessIntroText, guessField);
		guessBox.setAlignment(Pos.CENTER);
		guessBox.setSpacing(10);
		mainVBox.getChildren().add(guessBox);

      outcomeText = new Text("Start Guessing!");
		outcomeText.setFont(Font.font("Helvetica", 28));
		outcomeText.setFill(TITLE_AND_OUTCOME_COLOR);
		HBox outcomeBox = new HBox(outcomeText);
		outcomeBox.setAlignment(Pos.CENTER);
		outcomeBox.setSpacing(10);
		mainVBox.getChildren().add(outcomeBox);

		wrongGuessesText.setFont(Font.font("Helvetica", 24));
		wrongGuessesText.setFill(INFO_COLOR);
		HBox wrongGuessesBox = new HBox(wrongGuessesText);
		wrongGuessesBox.setAlignment(Pos.CENTER);
		wrongGuessesBox.setSpacing(10);
		mainVBox.getChildren().add(wrongGuessesBox);

      wrongGuessNumberText = new Text("Number of Guesses Remaining: "+ MAX_WRONG_GUESSES);
		wrongGuessNumberText.setFont(Font.font("Helvetica", 24));
		wrongGuessNumberText.setFill(INFO_COLOR);
		HBox wrongGuessNumberBox = new HBox(wrongGuessNumberText);
		wrongGuessNumberBox.setAlignment(Pos.CENTER);
		mainVBox.getChildren().add(wrongGuessNumberBox);
      
      //the play again button (created but hidden at first)
      againButton = new Button ("Play Again");
      againButton.setVisible(false);
      againButton.setOnAction(this::restart);
      HBox againButtonBox = new HBox(againButton);
      againButtonBox.setAlignment(Pos.CENTER);
      mainVBox.getChildren().add(againButtonBox);

		Scene scene = new Scene(mainVBox, 550, 500);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private void handleGuessField(ActionEvent event)
   {
      try
      {
         checkEmptyField();
      
         String oneGuess = guessField.getText();
         
         checkValidity(oneGuess);     
         //not making it a boolean isValid() since it throws exception if invalid and nothing happens if valid
         
         
         updateDisplay(oneGuess);
      
         guessField.clear();
      
         //if the player wins the game,
         if(guessedAll())
         {
            outcomeText.setText("Yes! You did it!");
            guessField.setDisable(true);
            againButton.setVisible(true);

         }
      
         //if the player loses the game,
         if(allWrong())
         {
            outcomeText.setText("Oh no! Better luck next time! ("+ currentWord+")");
            guessField.setDisable(true);
            againButton.setVisible(true);

         }
      }     //end try block
      
      catch(EmptyGuessException e)     //for the empty guess field
      {
         outcomeText.setText(e.getMessage());
      }
      catch(GuessCharacterException e)    //both for one character exception and only letter exception
      {
         outcomeText.setText(e.getMessage());
         guessField.clear();
      }
      
	}
   
   
   private void checkEmptyField() throws EmptyGuessException
   {
      if(guessField.getText().isEmpty())
      {
         throw new EmptyGuessException();
      }
   }
   
   
   
   private void checkValidity(String s) throws GuessCharacterException
   {
      for(int i=0; i<s.length(); i++)
      {
         if(!Character.isLetter(s.charAt(i)))
            throw new GuessCharacterException("Error: Guess Not A Letter");
      }
      
      if(s.length()>1)
         throw new GuessCharacterException("Error: Guess Exceeding ONE Character");
      
   }
   
   
   
   private void updateDisplay(String s)
   {
      //if the player guesses it correctly,
      if(checkGuess(s))
      {
        if(!isRightDuplicate(s))    //and if it is not already guessed
        {
           for(int i=0; i<currentWord.length(); i++)
            {
               String cString = Character.toString(currentWord.charAt(i));

               if(cString.equals(s))         //at the appropriate index
                  currentDisplay[i] = s;     //updating the currentDisplay array( originally all "-")
            }
            currentWordText.setText(getCurrentDisplay());     //changing array into String and displaying it
            outcomeText.setText("There you go!");
         }
      }
      //if the player guesses it incorrectly,
      else
      {
         if(!isWrongDuplicate(s))         //if it's not in the wrong letters list yet,
         {
            wrongGuesses.add(s);          //add it to the list
            wrongGuessesText.setText("Wrong Guesses: "+ Arrays.toString(wrongGuesses.toArray()));
            outcomeText.setText("Damn. Tough Luck!");
            wrongGuessNumberText.setText("Number of Guesses Remaining: "+ (MAX_WRONG_GUESSES-wrongGuesses.size()));
         }
      }
      
   }
   
   
   private boolean isRightDuplicate(String s)         //checking duplicate for correctly guessing
   {
      for(String i: currentDisplay)
      {
         if(i.equals(s))
         {
            outcomeText.setText("Already got that right!");
            return true;
         }
      }
      return false;
   }
   
   
   
   private boolean isWrongDuplicate(String s)           //checking duplicates for incorrectly guesing
   {
      for(String i: wrongGuesses)
      {
         if(i.equals(s))
         {
            outcomeText.setText("Oops. Already got it wrong.");
            return true;
         }
      }
      return false;
   }
   
   
   
   
   private String getCurrentDisplay()        //constructing a String out of the currentDisplay array
   {
      String currentString = "";
      for(String i: currentDisplay)
      {
         currentString += i;
      }
      return currentString;
   }
   
   
   
   private boolean checkGuess(String s)         //checking if the guess is correct or not
   {
      for(int i=0; i<currentWord.length(); i++)
      {
         String cString = Character.toString(currentWord.charAt(i));
         if(cString.equals(s))
            return true;
      }
      return false;
   }
   
   
   
   private boolean guessedAll()        //checking if the player wins
   {
      for(String i: currentDisplay)
      {
         if(i.equals("-"))
            return false;
      }
      return true;
   }
   
   private boolean allWrong()          //checking if the player loses
   {
      return (wrongGuesses.size()==MAX_WRONG_GUESSES);
      
   }
   
   
	private String chooseWord() throws FileNotFoundException
   {

      File newFile = new File("words.txt");
      Scanner inputFile = new Scanner(newFile);
   
      ArrayList<String> wordsList = new ArrayList<String>();
      
 
      boolean hasLine = inputFile.hasNextLine();
      
      while(hasLine)
      {
         String s = inputFile.nextLine();

         wordsList.add(s);
         hasLine = inputFile.hasNextLine();

      }
      
      int random = (int) (Math.random()*wordsList.size())+1;

      
      return wordsList.get(random);

	}

   private void makeCurrentDashes()          //making the dashes out of the currently chosen word
   {
      currentDisplay = new String[currentWord.length()];
      
      for(int i=0; i<currentWord.length(); i++)
      {  
         currentDisplay[i] = "-";
      }
      	
      currentWordText.setText(getCurrentDisplay());
   }


   private void restart(ActionEvent event)
   {
      againButton.setVisible(false);
      guessField.setDisable(false);
      
      outcomeText.setText("Start Guessing!");
      wrongGuessesText.setText("Wrong Guesses: []");
      wrongGuessNumberText.setText("Number of Remaining Wrong Guesses: "+MAX_WRONG_GUESSES);
      wrongGuesses.clear();
      
      try{currentWord = chooseWord();}
      catch(FileNotFoundException e){System.out.println("File Not Found.");}
      
      makeCurrentDashes();
   }



	public static void main(String[] args) {
		launch(args);

	}

}
