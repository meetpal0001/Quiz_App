package com.example.quiz_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // Variable declarations
    int correct_ans = 0;
    EditText txt;
    int ttl = 0;
    int curr_qtn=0;
    int ttl_qtns=5;
    static int correct = 0;
    static int colour[]= {Color.BLUE,Color.RED,Color.YELLOW,Color.GREEN
    ,Color.MAGENTA,Color.CYAN,Color.LTGRAY};

    static Random rand= new Random();
    ArrayList<Question> qtns = new ArrayList<Question>();

    Button tru,fal;
    ProgressBar bar;

    private AlertDialog.Builder builder;

    ResultsDatabase resultsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);    //Generates the app bar
        setSupportActionBar(myToolbar);

        loadqtns();// loads questions into the system

        Collections.shuffle(qtns);  // shuffle the questions
        String qs=qtns.get(curr_qtn).getQuestion();
        QuestionFragment qtnFragment = QuestionFragment.newInstance(qs,null); // Adds a fragment with the new question
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainerView, qtnFragment);
        fragmentTransaction.commit();

        tru=findViewById(R.id.right);
        fal=findViewById(R.id.wrong);

        bar=findViewById(R.id.progressBar);

        resultsDatabase= Room.databaseBuilder(getApplicationContext(),ResultsDatabase.class,"results-db").build();  // Building database

        builder = new AlertDialog.Builder(this);

        tru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qtns.get(curr_qtn).isAns()){ // checks whether the answer is correct or not
                    Toast.makeText(v.getContext(),getResources().getString(R.string.correct),Toast.LENGTH_SHORT).show();
                    correct++;
                }
                else{
                    Toast.makeText(v.getContext(),getResources().getString(R.string.incorrect),Toast.LENGTH_SHORT).show();
                }

                bar.setProgress(++curr_qtn); // updates the Progress bar

                if (curr_qtn<ttl_qtns){ // Calls new question fragment if there are still more questions to go
                    QuestionFragment qtnFragment = QuestionFragment.newInstance(qtns.get(curr_qtn).getQuestion(),null);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.fragmentContainerView, qtnFragment);
                    fragmentTransaction.commit();
                }
                else {
                    savePrompt(correct,ttl_qtns);   // Prompts the user to save
                }
            }
        });


        fal.setOnClickListener(new View.OnClickListener() { //When user clicks the False button
            @Override
            public void onClick(View v) {
                if (!qtns.get(curr_qtn).isAns()){   // checks whether the answer is correct or not
                    Toast.makeText(v.getContext(),getResources().getString(R.string.correct),Toast.LENGTH_SHORT).show();
                    correct++;
                }
                else{
                    Toast.makeText(v.getContext(),getResources().getString(R.string.incorrect),Toast.LENGTH_SHORT).show();
                }

                bar.setProgress(++curr_qtn);

                if (curr_qtn<ttl_qtns){
                    QuestionFragment qtnFragment = QuestionFragment.newInstance(qtns.get(curr_qtn).getQuestion(),null);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.fragmentContainerView, qtnFragment);
                    fragmentTransaction.commit();
                }
                else {
                    savePrompt(correct,ttl_qtns);
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Inflate the Menu
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {  // Listeners for the Menu items
        int itemId = item.getItemId();
        if (itemId == R.id.average) {   // Get Average option

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    List<Results> reslts=resultsDatabase.resultsDao().getResults(); // get previous results
                    if (!reslts.isEmpty()){ // Sets the values to 0 if not list is returned from the query
                        correct_ans =reslts.get(0).getCorrect();
                        ttl =reslts.get(0).getTotal();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            avgPrompt(correct_ans,ttl);
                        }
                    });

                }
            });


            return true;
        } else if (itemId == R.id.reset) {  // reset the saved scores
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    List<Results> reslts=resultsDatabase.resultsDao().getResults();

                    if(!reslts.isEmpty()){  // Checks if the database is not already empty
                    resultsDatabase.resultsDao().deleteResults(reslts.get(0));
                    correct_ans=0;
                    ttl=0;
                    }
                }
            });
            return true;
        } else if (itemId == R.id.question) {   // Reset the number of questions
            final EditText editTextName1 = new EditText(MainActivity.this);
            editTextName1.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setMessage(getResources().getString(R.string.new_Qtn_body)+"\n"+getResources().getString(R.string.lower)+String.valueOf(curr_qtn+1)
                            +"\n"+getResources().getString(R.string.upper)+"25")
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            txt = editTextName1; // variable to collect user input
                            String getInput = txt.getText().toString();

                            // ensure that user input bar is not empty
                            if (getInput ==null || getInput.trim().equals("")){
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.wrong_input), Toast.LENGTH_LONG).show();
                            }    // add input into an data collection arraylist
                            else {
                                int input=Integer.parseInt(getInput);
                                // Ensure that the values are correct
                                if (input<curr_qtn+1||input>25){
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.wrong_input), Toast.LENGTH_LONG).show();
                                }
                                // reset the questions and the progress bar
                                else {
                                    ttl_qtns=input;
                                    bar.setMax(input);
                                }
                            }

                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.setView(editTextName1);
            LinearLayout lay = new LinearLayout(this);
            lay.setOrientation(LinearLayout.VERTICAL);
            lay.addView(editTextName1); // displays the user input bar
            alert.setView(lay);
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

// Saves the data
    void savePrompt(int par,int par2){
        builder.setMessage(getResources().getString(R.string.finish_body) + String.valueOf(correct)+"/"+String.valueOf(ttl_qtns))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Results res=new Results();
                        res.setKey(1);
                        res.setCorrect(par);
                        res.setTotal(par2);


                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                List<Results> reslts=resultsDatabase.resultsDao().getResults();
                                // insert a new result to the list if it is not empty otherwise update the existing one
                                if (reslts.isEmpty()){
                                    resultsDatabase.resultsDao().insertResults(res);
                                }
                                else{
                                    res.setCorrect(reslts.get(0).getCorrect()+par);
                                    res.setTotal(reslts.get(0).getTotal()+par2);
                                    resultsDatabase.resultsDao().updateResults(res);
                                }
                            }
                        });
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton(getResources().getString(R.string.ignore), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(getResources().getString(R.string.result));
        alert.show();

        // Restarts the quiz
        curr_qtn=0;
        correct=0;
        Collections.shuffle(qtns);

        QuestionFragment qtnFragment = QuestionFragment.newInstance(qtns.get(curr_qtn).getQuestion(),null);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainerView, qtnFragment);
        fragmentTransaction.commit();

        bar.setProgress(0);
    }
    
    void loadqtns(){
            qtns.add(new Question(getResources().getString(R.string.q1),false));
            qtns.add(new Question(getResources().getString(R.string.q2),false));
            qtns.add(new Question(getResources().getString(R.string.q3),true));
            qtns.add(new Question(getResources().getString(R.string.q4),false));
            qtns.add(new Question(getResources().getString(R.string.q5),true));
            qtns.add(new Question(getResources().getString(R.string.q6),true));
            qtns.add(new Question(getResources().getString(R.string.q7),false));
            qtns.add(new Question(getResources().getString(R.string.q8),false));
            qtns.add(new Question(getResources().getString(R.string.q9),true));
            qtns.add(new Question(getResources().getString(R.string.q10),false));
            qtns.add(new Question(getResources().getString(R.string.q11),true));
            qtns.add(new Question(getResources().getString(R.string.q12),true));
            qtns.add(new Question(getResources().getString(R.string.q13),false));
            qtns.add(new Question(getResources().getString(R.string.q14),true));
            qtns.add(new Question(getResources().getString(R.string.q15),false));
            qtns.add(new Question(getResources().getString(R.string.q16),false));
            qtns.add(new Question(getResources().getString(R.string.q17),true));
            qtns.add(new Question(getResources().getString(R.string.q18),true));
            qtns.add(new Question(getResources().getString(R.string.q19),true));
            qtns.add(new Question(getResources().getString(R.string.q20),false));
            qtns.add(new Question(getResources().getString(R.string.q21),true));
            qtns.add(new Question(getResources().getString(R.string.q22),false));
            qtns.add(new Question(getResources().getString(R.string.q23),false));
            qtns.add(new Question(getResources().getString(R.string.q24),true));
            qtns.add(new Question(getResources().getString(R.string.q25),true));
    }

    void avgPrompt(int p1,int p2){
        builder.setMessage(getResources().getString(R.string.avg_body) + String.valueOf(p1)+"/"+String.valueOf(p2))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Toast.makeText(MainActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.show();
    }


}