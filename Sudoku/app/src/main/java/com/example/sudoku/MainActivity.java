package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

        private class Cell
        {
            //value present in cell
            int value;
            //if cell fixed then number cannot be changed
            boolean fixed;
            Button bt;

            public Cell(int initvalue, Context THIS)
            {
                //Set value, if not 0, set to fixed as number is present
                value=initvalue;
                if (value!=0) fixed=true;
                else fixed=false;
                bt=new Button(THIS);
                if (fixed) bt.setText(String.valueOf(value));
                //else set different colour value if fixed cell
                else bt.setTextColor(Color.RED);
                //onclick listener for each button
                bt.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (fixed) return;
                        value++;
                        if (value>9) value=1;
                        bt.setText(String.valueOf(value));
                        if (correct())
                        {
                            tv.setText("");
                        }
                        else
                        {
                            tv.setText("There's a repeated digit");
                        }
                    }
                });
            }
        }

        //check if every cell has a number present
        boolean completed()
        {
            for (int i = 0; i < 9; i++)
            {
                for (int j = 0; j < 9; j++)
                {
                    if (table[i][j].value==0)
                        return false;
                }
            }
            return true;
        }

        boolean correct(int i1,int j1, int i2, int j2)
        {
            boolean[] seen=new boolean[10];
            for (int i = 0; i < 9; i++) seen[i]=false;
            for (int i = i1; i < i2; i++) 
            {
                for (int j = j1; j < j2; j++)
                {
                    int value=table[i][j].value;
                    //if this value is different from 0
                    if (value!=0)
                    {
                        //if we have seen it before, it is not correct so return false
                        if (seen[value])
                        {
                            return false;
                        }
                        else
                        {
                            seen[value]=true;
                        }
                    }
                }
            }
            //if we have not found an inconsistency, return as true
            return true;
        }

        //checks whether the numbers are correct
        boolean correct()
        {
            for (int i = 0; i < 9; i++)
            {
                //if current row not correct
                if (!correct(i,0,i+1,9)) return false;
            }
            for (int j = 0; j < 9; j++)
            {
                //if current column not correct
                if (!correct(j,0,j+1,9)) return false;
            }
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    if (!correct(3*i,3*j,3*i+3,3*j+3))
                    {
                        //if there is an inconsistency, return false
                        return false;
                    }
                }
            }
            //if no inconsistencies, return true
            return true;
        }

        //array of cells
        Cell[][] table;
        String input;
        TableLayout tl;
        TextView tv;
        LinearLayout linlay;
        final String TAG="SUDOKU ASSETS";
        Button btSave;
        Button btLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        /*
        input = "3 ? 6 5 ? 8 4 ? ? "+
                "5 2 ? ? ? ? ? ? ? "+
                "? 8 7 ? ? ? ? 3 1 "+
                "? ? 3 ? 1 ? ? 8 ? "+
                "9 ? ? 8 6 3 ? ? 5 "+
                "? 5 ? ? 9 ? 6 ? ? "+
                "1 3 ? ? ? ? 2 5 ? "+
                "? ? ? ? ? ? ? 7 4 "+
                "? ? 5 2 ? 6 3 ? ? ";
        */
        //set input as empty string
        input="";
        //new buffered reader for try catch
        BufferedReader reader = null;
        //try catch reads in sudoku, or flags error if unable
        try
        {
            reader = new BufferedReader(new InputStreamReader
                    (getAssets().open("sudoku.txt")));
            String line;
            while ((line=reader.readLine())!=null)
            {
                //add white space behind each line
                input+=line+" ";
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //close reader
        finally
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        Log.d(TAG,input);
        //split on space, or on multiple spaces
        String[] split=input.split("[ ]+");
        //new 9 by 9 table of cells
        table=new Cell[9][9];
        //create new table layout, pass context
        tl=new TableLayout(this);
        //for each element, create new cell
        for (int i = 0; i < 9; i++)
        {
            TableRow tr=new TableRow(this);
            for (int j = 0; j < 9; j++)
            {
                String s=split[i*9+j];
                Character c=s.charAt(0);
                //If it is a ? set it to 0. otherwise set it to same number as int
                table[i][j]=new Cell(s.charAt(0)=='?'?0:c-'0',this);
                tr.addView(table[i][j].bt);
            }
            tl.addView(tr);
        }
        //save button
        btSave = new Button(this);
        btSave.setText("SAVE");
            btSave.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String text="";
                    //for each cell, add to string text
                    for (int i = 0; i < 9; i++)
                    {
                        for (int j = 0; j < 9; j++)
                        {
                            Cell cell= table[i][j];
                            //add space between each character
                            if (j<0) text+=" ";
                            text+=String.valueOf(cell.value);
                        }
                        //Create new line of cells
                        text+="\n";
                    }
                    //write the save to a file
                    File file = new File(getFilesDir(), "sudokuSave");
                    FileWriter writer = null;
                    //try catch for file writer
                    try
                    {
                        writer = new FileWriter(file);
                        writer.append(text);
                        writer.flush();
                        writer.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        // new button for load, when clicked load saved game
        btLoad = new Button(this);
        btLoad.setText("LOAD");
        btLoad.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String text = "";
                File file = new File(getFilesDir(), "sudokuSave");
                //try catch to throw exception if error
                try
                {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while((line=reader.readLine())!=null)
                    {
                        text+=line+"\n";
                    }
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                //Split on white spaces or text lines
                String[] array = text.split("[ \n]+");
                for (int i = 0; i < 9; i++)
                {
                    for (int j = 0; j < 9; j++)
                    {
                        Cell cell = table[i][j];
                        cell.value = Integer.parseInt(array[i*9+j]);
                        //if no cell value, set to empty
                        if (cell.value==0)
                        {
                            cell.bt.setText("");
                        }
                        //Else set its value to value of string in array
                        else
                        {
                            cell.bt.setText(String.valueOf(cell.value));
                        }
                    }
                }
            }
        });
        //make sure columns fit in screen
        tl.setShrinkAllColumns(true);
        //stretch to fill space
        tl.setStretchAllColumns(true);
        //new text view
        tv=new TextView(this);
        //new linear layout
        linlay=new LinearLayout(this);
        //add table layout to view
        linlay.addView(tl);
        //add view for save button
        linlay.addView(btSave);
        //add view for load button
        linlay.addView(btLoad);
        //add text view under table layout
        linlay.addView(tv);
        //centre
        linlay.setGravity(Gravity.CENTER_HORIZONTAL);
        //give vertical orientation
        linlay.setOrientation(LinearLayout.VERTICAL);
        setContentView(linlay);
    }
}