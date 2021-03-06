package edu.cis.sensational.Controller.Colorize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.cis.sensational.Model.Colorize.GameConstants;
import edu.cis.sensational.R;

/**
 * The main page is where the game will occur
 */
public class ColorizeMainActivity extends AppCompatActivity
{

    Button answerOne, answerTwo, stopButton;
    TextView timeLabel, scoreLabel, colorWord;
    ImageView backgroundColor;
    ArrayList<String> colorWords;
    ArrayList<Integer> colorInts;
    HashMap<Integer, String> answers;
    String word, correctAnswer, wrongAnswer;
    long timeLeft;
    int color, backColor, colorIndex, seconds;

    CountDownTimer counter;


    /**
     * Creates and identifies the various components on screen such as buttons, labels and buttons
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorize_main);
        answerOne = findViewById(R.id.answerOne);
        answerTwo = findViewById(R.id.answerTwo);
        stopButton = findViewById(R.id.stopButton);
        timeLabel = findViewById(R.id.timeLabel);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        colorWord = findViewById(R.id.colorWord);
        backgroundColor = findViewById(R.id.backgroundColor);
        colorWords = new ArrayList<String>();
        answers = new HashMap<Integer, String>();
        colorInts = new ArrayList<Integer>();

        setUpButtons();
        setUpTimer();
        addColors();
        setUpGame();
        play();
    }

    /**
     * Identifies actions once the corresponding buttons on the screen is pressed
     */
    private void setUpButtons()
    {
        stopButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view) {
                counter.cancel();
                startActivity(new Intent(ColorizeMainActivity.this,
                        ColorizeEndActivity.class));

            }
        });
    }

    /**
     * Sets the timer on the screen
     */
    private void setUpTimer()
    {
        getTime();
        counter = new CountDownTimer(GameConstants.TIME,GameConstants.INTERVAL)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                timeLeft = millisUntilFinished / GameConstants.INTERVAL;
                timeLabel.setText("" +timeLeft);
            }

            @Override
            public void onFinish()
            {
                finish();
                startActivity(new Intent(ColorizeMainActivity.this,
                        ColorizeEndActivity.class));

                //https://www.youtube.com/watch?v=e7sHAYYubJo
                TastyToast.makeText(getApplicationContext(), GameConstants.TOAST,
                        TastyToast.LENGTH_LONG, TastyToast.WARNING);
            }
        };
    }

    /**
     * Retrieves bundle that was passed on from ColorizeStartActivity
     */
    private void getTime()
    {
        //get the data by String key
        if (getIntent().getExtras() != null)
        {
            Bundle bundle = getIntent().getExtras();
            seconds = bundle.getInt(GameConstants.TIMESTRING);
            GameConstants.TIME = seconds;
        }
        else
        {
            GameConstants.TIME = GameConstants.DEFAULTTIME;
        }
    }

    /**
     * Adds colors and data that needs to be used and processed internally in the corresponding data
     * structures
     */
    private void addColors()
    {
        //clears all data structures first before constructing it again to avoid duplication of data
        answers.clear();
        colorInts.clear();
        colorWords.clear();

        //Hashmap<Integer, String> to define the corresponding correct answers
        answers.put(Color.RED, GameConstants.RED);
        answers.put(Color.YELLOW, GameConstants.YELLOW);
        answers.put(Color.GREEN, GameConstants.GREEN);
        answers.put(Color.BLACK, GameConstants.BLACK);
        answers.put(Color.GRAY, GameConstants.GRAY);
        answers.put(Color.MAGENTA, GameConstants.PINK);
        answers.put(Color.BLUE, GameConstants.BLUE);

        //Arraylist<Integer> containing android's predefined color constants
        colorInts.add(Color.RED);
        colorInts.add(Color.YELLOW);
        colorInts.add(Color.GREEN);
        colorInts.add(Color.BLACK);
        colorInts.add(Color.GRAY);
        colorInts.add(Color.MAGENTA);
        colorInts.add(Color.BLUE);

        //Arraylist<String> containing all the words that will be displayed on UI
        colorWords.add(GameConstants.RED);
        colorWords.add(GameConstants.YELLOW);
        colorWords.add(GameConstants.GREEN);
        colorWords.add(GameConstants.BLACK);
        colorWords.add(GameConstants.GRAY);
        colorWords.add(GameConstants.PINK);
        colorWords.add(GameConstants.BLUE);
    }


    /**
     * starts the timer, generates random text, color of text and background color onto screen, as
     * well as the buttons for the users to click on
     */
    private void setUpGame()
    {
        // start time
        counter.start();

        //generate random word
        int index = new Random().nextInt(colorWords.size());
        word = colorWords.get(index);

        //generate random color for word
        colorIndex = new Random().nextInt(colorInts.size());
        color = colorInts.get(colorIndex);

        //set text to random word
        colorWord.setText(word);

        //set word to random color
        colorWord.setTextColor(color);

        //loop through colors to find corresponding int, then get value from hashmap for the
        // correct answer
        for (int i = 0; i <= colorInts.size() - 1; i ++)
        {
            if (color == colorInts.get(i))
            {
                correctAnswer = answers.get(color);
            }
        }

        // set incorrect answer
        colorWords.remove(correctAnswer);
        int wrongIndex = new Random().nextInt(colorWords.size());
        wrongAnswer = colorWords.get(wrongIndex);

        setBackgroundColor();

        //place answer options in list
        ArrayList<String> randomAnswers = new ArrayList<>();
        randomAnswers.add(correctAnswer);
        randomAnswers.add(wrongAnswer);

        //Randomize the answers in the list and display on screen
        String answer = randomAnswers.get(new Random().nextInt(GameConstants.BOUND));
        answerOne.setText(answer);
        randomAnswers.remove(answer);
        answerTwo.setText(randomAnswers.get(GameConstants.ZERO));
    }

    /**
     * Checks user background preference by checking game constant
     */
    private void setBackgroundColor()
    {
        //if user didn't select to have background, set background to white
        if (!GameConstants.BACKGROUND)
        {
            backgroundColor.setBackgroundColor(Color.WHITE);
        }

        //if user did select to have background, generate random background color
        else if (GameConstants.BACKGROUND)
        {
            // background color cannot be the same as color of word
            colorInts.remove(colorIndex);
            int backgroundIndex = new Random().nextInt(colorInts.size());
            backColor = colorInts.get(backgroundIndex);
            backgroundColor.setBackgroundColor(backColor);
        }
    }

    /**
     * Identifies actions once the buttons are clicked depending if the user chose the right button.
     * If it is the right button, adds score, reset timer and repeats the game until the user
     * presses the wrong button, then the user will be directed to the end page
     */
    private void play()
    {
        answerOne.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                counter.cancel();

                // if the user chose the correct answer and is within time
                if (answerOne.getText() == correctAnswer && timeLeft > GameConstants.ZERO )
                {
                    counter.cancel();
                    GameConstants.SCORE ++;
                    scoreLabel.setText(""+ GameConstants.SCORE);

                    //generate new question
                    addColors();
                    setUpGame();
                }

                else
                {
                    TastyToast.makeText(getApplicationContext(), GameConstants.WRONGANSWER,
                            TastyToast.LENGTH_SHORT, TastyToast.ERROR);

                    //direct user to end page
                    startActivity(new Intent(ColorizeMainActivity.this,
                            ColorizeEndActivity.class));
                    finish();
                }
            }
        });

        answerTwo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                counter.cancel();

                if (answerTwo.getText() == correctAnswer && timeLeft > GameConstants.ZERO)
                {

                    GameConstants.SCORE++;
                    scoreLabel.setText(""+ GameConstants.SCORE);
                    counter.cancel();
                    addColors();
                    setUpGame();
                }
                else
                {
                    TastyToast.makeText(getApplicationContext(), GameConstants.WRONGANSWER,
                            TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    startActivity(new Intent(ColorizeMainActivity.this,
                            ColorizeEndActivity.class));
                    finish();

                }
            }
        });
    }
}
