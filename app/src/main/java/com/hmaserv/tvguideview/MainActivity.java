package com.hmaserv.tvguideview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hmaserv.guideview.ui.GuideView;
import com.hmaserv.guideview.ui.ViewPosition;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GuideView.onBindView {
    GuideView guideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        guideView = findViewById(R.id.guideView);
        guideView.setOnBindView(this);
        fillDate();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (guideView.hasFocus()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    guideView.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    ArrayList<ArrayList<ViewItem>> items = new ArrayList<>();

    public void fillDate() {
        for (int y = 0; y < 100; y++) {
            ArrayList<ViewItem> row = new ArrayList<>();
            for (int x = 0; x < 100; x++) {
                row.add(new ViewItem("row_" + y + " column_" + x));
            }
            items.add(row);
        }

        Log.e("ahmed1", " rowsSize " + items.size());
        guideView.bindView();
    }


    @Override
    public void BindCornerView(View view, ViewPosition position) {
        View tm = view;
        ViewItem item = items.get(position.getRow()).get(position.getColumn());
        FrameLayout cornerLayout = (FrameLayout)view;

        TextView text = view.findViewById(R.id.textCorner);
        text.setText(item.getTitle());
    }

    @Override
    public void BindRowHeaderView(View view, ViewPosition position) {
        View tm = view;
        ViewItem item = items.get(position.getRow()).get(position.getColumn());
        RelativeLayout rowHeaderLayout = (RelativeLayout)view;

        TextView text = view.findViewById(R.id.textRowHeader);
        text.setText(item.getTitle());
    }

    @Override
    public void BindColumnHeaderView(View view, ViewPosition position) {
        View tm = view;
        ViewItem item = items.get(position.getRow()).get(position.getColumn());
        ConstraintLayout columnHeaderLayout = (ConstraintLayout)view;
        final ViewPosition t = position;

        TextView text = view.findViewById(R.id.textColumnHeader);
        text.setText(item.getTitle());
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Log.e("ItemClicked", "Go Left row=" + t.getRow() + " column=" + t.getColumn());
//            }
//        });
    }

    @Override
    public void BindCellView(View view, ViewPosition position) {
        View tm = view;
        ViewItem item = items.get(position.getRow()).get(position.getColumn());
        LinearLayout cellLayout = (LinearLayout)view;
        TextView text = view.findViewById(R.id.textCell);
        text.setText(item.getTitle());
        final ViewPosition t = position;
        cellLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("ItemClicked", "Go Left row=" + t.getRow() + " column=" + t.getColumn());
            }
        });
    }

    @Override
    public int getRowsSize() {
        return items.size();
    }

    @Override
    public int getColumnsSize() {
        return (items.size() > 0) ? items.get(0).size() : 0;
    }

}