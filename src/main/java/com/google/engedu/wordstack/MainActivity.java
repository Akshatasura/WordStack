package com.google.engedu.wordstack;

import android.content.res.AssetManager;
import java.io.*;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1, word2;
    private Stack<LetterTile> placedTiles = new Stack<>();
    ArrayList<Character> Arranged_word1=new ArrayList<Character>();
    ArrayList<Character> Arranged_word2=new ArrayList<Character>();
    int cnt1,cnt2,cnt_scr=0;
    String Scramble_word;
    String str1=new String();
    String str2=new String();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();

                if (word.length() == WORD_LENGTH)
                    words.add(word);
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        View word1LinearLayout = findViewById(R.id.word1);
        //word1LinearLayout.setOnTouchListener(new TouchListener());
        word1LinearLayout.setOnDragListener(new DragListener());
        View word2LinearLayout = findViewById(R.id.word2);
        //word2LinearLayout.setOnTouchListener(new TouchListener());
        word2LinearLayout.setOnDragListener(new DragListener());
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }
                /** code**/

                placedTiles.push(tile);

                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);
                    TextView messageBox = (TextView) findViewById(R.id.message_box);

                    if(v.getId()==R.id.word1)
                    {
                        str1+=Scramble_word.charAt(cnt_scr);
                        cnt_scr++;
                        messageBox.setText("Word1: "+str1);
                    }
                    if(v.getId()==R.id.word2)
                    {
                        str2+=Scramble_word.charAt(cnt_scr);
                        cnt_scr++;
                        messageBox.setText("Word2: "+str2);
                    }
                    if (stackedLayout.empty()) {

                        //messageBox.setText(word1 + " " + word2);
                        checkWin();
                    }

                    placedTiles.push(tile);
                    return true;
            }
            return false;
        }
    }

    public boolean onStartGame(View view) throws IOException {
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");

        LinearLayout word1LinearLayout = (LinearLayout)findViewById(R.id.word1);
        word1LinearLayout.removeAllViews();

        LinearLayout word2LinearLayout = (LinearLayout)findViewById(R.id.word2);
        word2LinearLayout.removeAllViews();

        stackedLayout.clear();

        messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");



        int temp=((words.size())+1);
        Random random1=new Random();
        int randomindex=random1.nextInt( temp - 1);
        int randomindex1=random1.nextInt( temp - 1);
        word1=words.get(randomindex);
        word2=words.get(randomindex1);

        Random random2=new Random();
        int i=0;
        int j=0;
        Scramble_word=new String();

        boolean f=false;
        while(f!=true) {
            int ran_index=random2.nextInt(3 - 1)+1;
            if (ran_index == 1) {
                Scramble_word += word1.charAt(i);
                if (word1.length() <= (i + 1)) {
                    while (j < word2.length()) {
                        Scramble_word += word2.charAt(j);
                        j++;
                    }
                    f = true;
                } else {
                    i++;
                }
            } else {
                Scramble_word += word2.charAt(j);
                if (word2.length() <= (j + 1)) {
                    while (i < word1.length()) {
                        Scramble_word += word1.charAt(i);
                        i++;
                    }
                    f = true;
                } else {
                    j++;
                }
            }
        }

        messageBox.setText(Scramble_word);

        for(i = Scramble_word.length() - 1; i >= 0; i--) {

            stackedLayout.push(new LetterTile(this, Scramble_word.charAt(i)));
        }


        return true;
    }

    public boolean onUndo(View view) {

        if(!placedTiles.empty()) {
            LetterTile popped = placedTiles.pop();
            if(view.getId()==R.id.word1)
            {
                Arranged_word1.remove(cnt1-1);
                cnt_scr--;
            }
            if(view.getId()==R.id.word1)
            {
                Arranged_word2.remove(cnt2-1);
                cnt_scr--;
            }
            (popped).moveToViewGroup((ViewGroup) stackedLayout);
            return true;
        }
        return false;
    }



    protected void checkWin() {

        TextView messageBox = (TextView) findViewById(R.id.message_box);
        if(word1.equals(str1) && word2.equals(str2))
            messageBox.setText("You win! " + word1 + " " + word2);
        else if(words.contains(str1) && words.contains(str2)){
            messageBox.setText("You found alternative words! " + str1 + " " + str2);
        }
        else{
            messageBox.setText("You Lost \n correct words are: "+word1+" "+word2);
        }
    }

}





