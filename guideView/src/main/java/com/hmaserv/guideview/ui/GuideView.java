package com.hmaserv.guideview.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hmaserv.guideview.R;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class GuideView extends FrameLayout {


    private int rowsSize = 0;
    private int columnsSize = 0;
    private int rowCount = 6;
    private int columnCount = 4;
    private int firstRowPosition = 0;
    private int firstColumnPosition = 0;
    private int rowHeaderLayout = 0;
    private int columnHeaderLayout = 0;
    private int cellLayout = 0;
    private int cornerLayout = 0;
    //    ArrayList<ArrayList<ViewItem>> items = new ArrayList<>();
    onBindView mOnBindView;

    public GuideView(@NonNull Context context) {
        super(context);
        initialDefaultValues(null);
        initialize();

    }

    public GuideView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialDefaultValues(attrs);
        initialize();
    }

    public GuideView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialDefaultValues(attrs);
        initialize();
    }


    private void initialize() {
        createView();
        setOnTouchListener(new LayoutTouchListener(getContext(), touchListenerCallBack));
        bindView();
    }


    private void initialDefaultValues(@Nullable AttributeSet attrs) {
        if (attrs == null) {
            // That means TableView is created programmatically.
            return;
        }

        // Get values from xml attributes
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable
                .TvGuideView, 0, 0);
        try {
            rowCount = (int) a.getInt(R.styleable.TvGuideView_rowCount, rowCount);
            columnCount = (int) a.getInt(R.styleable.TvGuideView_columnCount, columnCount);


            rowHeaderLayout = a.getResourceId(R.styleable.TvGuideView_rowHeaderLayout, rowHeaderLayout);
            columnHeaderLayout = a.getResourceId(R.styleable.TvGuideView_columnHeaderLayout, columnHeaderLayout);
            cellLayout = a.getResourceId(R.styleable.TvGuideView_cellLayout, cellLayout);
            cornerLayout = a.getResourceId(R.styleable.TvGuideView_cornerLayout, cornerLayout);
            String onBindViewName = a.getString(R.styleable.TvGuideView_onBindView);
            // Create the onBindView if specified.
            createOnBindView(getContext(), onBindViewName, attrs);

        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            if (FocusedPosition != null) {
                findViewWithTag(FocusedPosition).requestFocus();
                Log.e("ahmedA1", FocusedPosition.getRow() + " " + FocusedPosition.getColumn());
                FocusedPosition = null;
            }
        }
    }

    ViewPosition FocusedPosition;

    public void saveFocus() {
        View view2 = ((Activity) getContext()).getCurrentFocus();
        FocusedPosition = null;
        if (view2 != null)
            FocusedPosition = (ViewPosition) view2.getTag();
    }

    public void createView() {
        LinearLayout verticalMainLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams mainLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        verticalMainLayout.setLayoutParams(mainLayoutParams);
        verticalMainLayout.setOrientation(LinearLayout.VERTICAL);
        for (int y = 0; y < rowCount; y++) {
            int yy = firstRowPosition + y;

            LinearLayout horizontalLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams horizontalLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
            horizontalLayoutParams.weight = 1.0f;
            horizontalLayoutParams.topMargin = getPixels(8, getContext());
            if (y == 0) horizontalLayoutParams.weight = 0.8f;
            horizontalLayout.setLayoutParams(horizontalLayoutParams);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int x = 0; x < columnCount; x++) {
                int xx = firstColumnPosition + x;
                LinearLayout.LayoutParams ViewLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                ViewLayoutParams.weight = 1.0f;
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = null;
                if (y != 0 && x != 0) {
                    //cell
                    view = inflater.inflate(cellLayout, null);
//                    ViewLayoutParams.leftMargin = getPixels(8, getContext());
                } else {
                    if (y == 0 && x > 0) {
                        //column header
                        view = inflater.inflate(columnHeaderLayout, null);
//                        if (x == (columnCount - 1)) {
//                            ViewLayoutParams.weight = 1.1f;
//                        }
                    } else if (y > 0) {
                        //row header
                        view = inflater.inflate(rowHeaderLayout, null);
                        ViewLayoutParams.weight = 1.3f;

                    } else {
                        //corner
                        view = inflater.inflate(cornerLayout, null);
                        ViewLayoutParams.weight = 0.8f;
//                        ViewLayoutParams.leftMargin = getPixels(4, getContext());
                    }
                }
                view.setLayoutParams(ViewLayoutParams);
                view.setTag(new ViewPosition(y, x));
                horizontalLayout.addView(view);
                if (y == 0 && x == (columnCount - 1)) {
                    LinearLayout.LayoutParams ViewLayoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                    View view1 = view = inflater.inflate(columnHeaderLayout, null);
                    ViewLayoutParams1.weight = .5f;
                    view.setLayoutParams(ViewLayoutParams1);
                    horizontalLayout.addView(view1);
                }
            }
            verticalMainLayout.addView(horizontalLayout);
        }
        addView(verticalMainLayout);
    }

    public void bindView() {

        View view2 = ((Activity) getContext()).getCurrentFocus();
        ViewPosition position22 = null;
        if (view2 != null)
            position22 = (ViewPosition) view2.getTag();

        if (mOnBindView == null) return;
        rowsSize = mOnBindView.getRowsSize();
        columnsSize = mOnBindView.getColumnsSize();
        if (rowsSize == 0) return;
        for (int y = 0; y < rowCount; y++) {
            int yy = firstRowPosition + y;
            if (yy >= rowsSize) return;
            if (yy < 0) return;
            for (int x = 0; x < columnCount; x++) {
                int xx = firstColumnPosition + x;
                if (xx >= columnsSize) return;
                if (xx < 0) return;
                ViewPosition position = new ViewPosition(y, x);
                View view = findViewWithTag(position);
                view.clearFocus();
                if (y != 0 && x != 0) {
//                    if (x == 1)
//                        Log.e("GuideView", "firstRowPosition:"+firstRowPosition+" firstColumnPosition:"+firstColumnPosition+" y:"+y+" x:"+x+" yy:"+yy+" xx:"+xx+" columnSpan:"+columnSpan);
                    columnSpanHandler(y, x, yy, xx, view);
//                    int columnSpan = mOnBindView.getColumnSpan(yy, xx -1);
//                    if(columnSpan>1){
//                        mOnBindView.BindCellView(view, new ViewPosition(yy, (xx+columnSpan)-1));
//                    }else {
//
//                        mOnBindView.BindCellView(view, new ViewPosition(yy, xx));
//                    }
                } else {
                    if (y == 0 && x > 0) {
                        mOnBindView.BindColumnHeaderView(view, new ViewPosition(y, xx));
                    } else if (y > 0) {
                        mOnBindView.BindRowHeaderView(view, new ViewPosition(yy, x));
                    } else {
                        mOnBindView.BindCornerView(view, new ViewPosition(y, xx));
                    }
                }
            }
        }

        if (position22 != null) {
            findViewWithTag(position22).requestFocus();
//            Log.e("ahmedA", position22.getRow() + " " + position22.getColumn());
        }
        if (goRight) {
            for (int i = columnCount; i > 0; i--) {
                final View view1 = findViewWithTag(new ViewPosition(rightIndex, i));
                Log.e("ahmed12", "tt " + i);
                if (view1 != null) {
                    if (view1.getVisibility() == VISIBLE) {
                        Log.e("ahmed10", "tt " + i);
                        view1.post(new Runnable() {
                            @Override
                            public void run() {
                                view1.requestFocus();

                            }
                        });
                        break;
                    }
                }
            }
        }
//        goRight = false;

    }

    private void columnSpanHandler2(int y, int x, int yy, int xx) {
        int tarika = columnCount - 1;
        for (int i = 0; i < columnCount; i++) {
            View view = findViewWithTag(new ViewPosition(y, x + i));
            if (view != null) {
                int columnSpan = mOnBindView.getColumnSpan(yy, xx + i);
                if (tarika > 0) {
                    view.setVisibility(VISIBLE);
                    columnSpan = Math.min(columnSpan, tarika);
                    ((LinearLayout.LayoutParams) view.getLayoutParams()).weight = columnSpan;
                    tarika = tarika - columnSpan;
                } else {
                    if (view.hasFocus()) {
                        int xi = (x + i) - 1;
                        for (int iii = xi; iii > 0; iii--) {
//                            Log.e("ahmed10", "tt "+iii);
                            final View view1 = findViewWithTag(new ViewPosition(y, iii));
                            if (view1 != null) {
                                if (view1.getVisibility() == VISIBLE) {

                                    Log.e("ahmed10", "tt " + iii);
                                    view1.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            view1.requestFocus();

                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    }
                    view.setVisibility(GONE);
                }
            }
        }
    }
//    2
//    1
//    2
//    1
//    3

    private void columnSpanHandler(int y, int x, int yy, int xx, View view) {

        view.setVisibility(VISIBLE);
        ArrayList<Integer> columnsSpan = new ArrayList<>();
        for (int i = 1; i < columnCount; i++) {
            int columnSpan = mOnBindView.getColumnSpan(yy, firstColumnPosition + i);
            columnsSpan.add(columnSpan);
        }
        int tarika = columnCount - 1;
        int currentSpanPosition = x - 1;
        int currentSpan = columnsSpan.get(currentSpanPosition);


        if (currentSpanPosition == 0) {
            currentSpan = Math.min(currentSpan, tarika);
            ((LinearLayout.LayoutParams) view.getLayoutParams()).weight = currentSpan;
            mOnBindView.BindCellView(view, new ViewPosition(yy, xx), x);
            return;
        }

        if (currentSpan < columnsSpan.get(currentSpanPosition - 1)) {
            mOnBindView.BindCellView(view, new ViewPosition(yy, xx), x);
//            handelFocus(y, x, yy, xx);
            view.setVisibility(GONE);
            return;
        }

        int tmt = tarika;
        for (int t = 0; t < currentSpanPosition; t++) {
            int m = 0;
            if (t == 0) {
                m = columnsSpan.get(t);
            } else if (columnsSpan.get(t) >= columnsSpan.get(t - 1)) {
                m = columnsSpan.get(t);
            }
            tmt = tmt - m;
        }
        if (tmt > 0) {
            ((LinearLayout.LayoutParams) view.getLayoutParams()).weight = Math.min(currentSpan, tmt);
        }
        mOnBindView.BindCellView(view, new ViewPosition(yy, xx), x);

//        mOnBindView.BindCellView(view, new ViewPosition(yy, xx));

//        int tarika = columnCount - 1;
//        for (int i = 0; i < columnCount; i++) {
//            View view = findViewWithTag(new ViewPosition(y, x + i));
//            if (view != null) {
//                int columnSpan = mOnBindView.getColumnSpan(yy, xx + i);
//                if (tarika > 0) {
//                    view.setVisibility(VISIBLE);
//                    columnSpan = Math.min(columnSpan, tarika);
//                    ((LinearLayout.LayoutParams) view.getLayoutParams()).weight = columnSpan;
//                    tarika = tarika - columnSpan;
//                } else {
//                    if(view.hasFocus()){
//                        int xi = (x + i)-1;
//                        for(int iii = xi;iii>0;iii--){
////                            Log.e("ahmed10", "tt "+iii);
//                            final View view1 = findViewWithTag(new ViewPosition(y, iii));
//                            if(view1 !=null){
//                                if(view1.getVisibility()==VISIBLE){
//
//                                    Log.e("ahmed10", "tt "+iii);
//                                    view1.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            view1.requestFocus();
//
//                                        }
//                                    });
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    view.setVisibility(GONE);
//                }
//            }
//        }
    }

    private void handelFocus(int y, int x, int yy, int xx) {
        final View view = findViewWithTag(new ViewPosition(y, x));
        view.setVisibility(GONE);
//        Log.e("55", " y:"+y +" x:"+x);
//        int TT = 5;
//
//        View view2 = ((Activity) getContext()).getCurrentFocus();
//        if(view2!=null){
//
//            ViewPosition position = (ViewPosition) view2.getTag();
//            Log.e("66", " y:"+position.getRow()+" x:"+position.getColumn() );
//        }
//
//        if (view.hasFocus()) {
//            Log.e("ahmed10", "tt " + x);
//            int mm = 121+TT;
//            int mm2 = 121;
//            int mm3 = mm+ mm2;
//            for(int i =columnCount;i>0;i--){
//                final View view1   = findViewWithTag(new ViewPosition(y, i));
//                Log.e("ahmed12", "tt " + i);
//                if(view1!=null){
//                    if(view1.getVisibility()==VISIBLE){
//                        Log.e("ahmed10", "tt " + i);
//                        view1.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                view1.requestFocus();
//
//                            }
//                        });
//                        break;
//                    }
//                }
//            }
//        }
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        view.setVisibility(GONE);
//                    }
//                },
//                1000);
//        view.setVisibility(GONE);
//        int mmt = x ;
//        if (view.hasFocus()) {
//            for (int iii = x; iii > 0; iii--) {
////                            Log.e("ahmed10", "tt "+iii);
//                final View view1 = findViewWithTag(new ViewPosition(y, iii));
//                if (view1 != null) {
//                    if (view1.getVisibility() == VISIBLE) {
//
//                        Log.e("ahmed10", "tt " + iii);
//                        view1.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                view1.requestFocus();
//
//                            }
//                        });
//                        break;
//                    }
//                }
//            }
//        }
    }

    public interface onBindView {

        void BindCornerView(View view, ViewPosition position);

        void BindRowHeaderView(View view, ViewPosition position);

        void BindColumnHeaderView(View view, ViewPosition position);

        void BindCellView(View view, ViewPosition position, int x);

        int getRowsSize();

        int getColumnsSize();

        int getColumnSpan(int yy, int xx);
    }

    public void setOnBindView(onBindView onBindView) {
        this.mOnBindView = onBindView;
    }

    int rightIndex = 1;
    Boolean goRight = false;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        View view = ((Activity) getContext()).getCurrentFocus();
        ViewPosition position = (ViewPosition) view.getTag();
        if (position == null) return super.onKeyDown(keyCode, event);
//        Log.e("ttttt", view.getText().toString());
        goRight = false;
        rightIndex = position.getRow();
        switch (keyCode) {

            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Toast.makeText(getContext(), "ItemClicked: row=" + position.getRow() + " column=" + position.getColumn(), Toast.LENGTH_SHORT).show();
                Log.e("ItemClicked", "row=" + position.getRow() + " column=" + position.getColumn());
                bindView();
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (position.getColumn() == 1) {
                    goLeft();
                    Log.e("ItemClicked", "Go Left row=" + position.getRow() + " column=" + position.getColumn());
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                for (int x = columnCount; x > 0; x--) {
                    View view1 = findViewWithTag(new ViewPosition(position.getRow(), x));
                    if (view1 != null) {
                        if (view1.getVisibility() == VISIBLE) {
                            if (position.getColumn() == x) {
                                goRight = true;
                                rightIndex = position.getRow();
                                gorRight();
                                return true;
                            }
                            break;
                        }
                    }
                }
//                boolean bool = false;
//                View view1 = findViewWithTag(new ViewPosition(position.getRow(), position.getColumn() - 1));
//                if (view1 != null)
//                    if (view1.getVisibility() == GONE)
//                        bool = true;
//                if (position.getColumn() == columnCount - 1 || bool) {
//                    gorRight();
//                    if (bool)
//                        Log.e("ItemClicked", "Go Right row=" + position.getRow() + " column=" + position.getColumn());
//                    return true;
//                }
                break;

            case KeyEvent.KEYCODE_DPAD_UP:
                if (position.getRow() == 1) {
                    goUp();
                    Log.e("ItemClicked", "Go Up row=" + position.getRow() + " column=" + position.getColumn());
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (position.getRow() == rowCount - 1) {
                    goRight = true;
                    goDown();
                    Log.e("ItemClicked", "Go Down row=" + position.getRow() + " column=" + position.getColumn());
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void goUp() {
        if (firstRowPosition > 0) {
            firstRowPosition--;
            bindView();
        }
    }

    public void goUpPage() {
        if (firstRowPosition == 0) return;
        int page = rowCount - 1;
        if (firstRowPosition > page) {
            firstRowPosition = firstRowPosition - page;
            bindView();
        } else {
            firstRowPosition = 0;
            bindView();
        }
    }

    public void goDown() {
        if (rowsSize > (firstRowPosition + rowCount)) {
            firstRowPosition++;
            bindView();
        }
    }

    public void goDownPage() {
        if (firstRowPosition >= rowsSize) return;
        int page = rowCount - 1;
        if (rowsSize > (firstRowPosition + rowCount + page)) {
            firstRowPosition = firstRowPosition + page;
            bindView();
        } else {
            firstRowPosition = rowsSize - rowCount;
            bindView();
        }
    }

    public void gorRight() {
        if (columnsSize > firstColumnPosition + columnCount) {
            firstColumnPosition++;
            bindView();
        }
    }

    public void gorRightPage() {
        if (firstRowPosition >= rowsSize) return;
        int page = columnCount - 1;
        if (columnsSize > firstColumnPosition + columnCount + page - 1) {
            firstColumnPosition = firstColumnPosition + page;
            bindView();
        } else {
            firstColumnPosition = columnsSize - columnCount;
            bindView();
        }
    }

    public void goLeft() {
        if (firstColumnPosition > 0) {
            firstColumnPosition--;
            bindView();
        }
    }

    public void goLeftPage() {
        if (firstColumnPosition == 0) return;
        int page = columnCount - 1;
        if (firstColumnPosition > page) {
            firstColumnPosition = firstColumnPosition - page;
            bindView();
        } else {
            firstColumnPosition = 0;
            bindView();
        }
    }

    private LayoutTouchListener.TouchListenerCallBack touchListenerCallBack = new LayoutTouchListener.TouchListenerCallBack() {
        @Override
        public void gorRight() {
            gorRightPage();
        }

        @Override
        public void goLeft() {
            goLeftPage();
        }

        @Override
        public void goUp() {
            goUpPage();
        }

        @Override
        public void goDown() {
            GuideView.this.goDownPage();
        }
    };

    private void createOnBindView(Context context, String className, AttributeSet attrs) {
        if (className != null) {
            className = className.trim();
            if (!className.isEmpty()) {
                className = getFullClassName(context, className);
                try {
                    ClassLoader classLoader;
                    if (isInEditMode()) {
                        classLoader = this.getClass().getClassLoader();
                    } else {
                        classLoader = context.getClassLoader();
                    }
                    Class<? extends onBindView> onBindViewClass =
                            Class.forName(className, false, classLoader)
                                    .asSubclass(onBindView.class);
                    Constructor<? extends onBindView> constructor;
                    Object[] constructorArgs = null;
                    try {
                        constructor = onBindViewClass
                                .getConstructor(BIND_VIEW_CONSTRUCTOR_SIGNATURE);
                        constructorArgs = new Object[]{context, attrs};
                    } catch (NoSuchMethodException e) {
                        try {
                            constructor = onBindViewClass.getConstructor();
                        } catch (NoSuchMethodException e1) {
                            e1.initCause(e);
                            throw new IllegalStateException(attrs.getPositionDescription()
                                    + ": Error creating bindViewInterfaceClass " + className, e1);
                        }
                    }
                    constructor.setAccessible(true);
                    setOnBindView(constructor.newInstance(constructorArgs));
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Unable to find GuideBindView " + className, e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Could not instantiate the GuideBindView: " + className, e);
                } catch (InstantiationException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Could not instantiate the GuideBindView: " + className, e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Cannot access non-public GuideBindView " + className, e);
                } catch (ClassCastException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Class is not a GuideBindView " + className, e);
                }
            }
        }
    }

    private static final Class<?>[] BIND_VIEW_CONSTRUCTOR_SIGNATURE =
            new Class<?>[]{Context.class, AttributeSet.class, int.class, int.class};

    private String getFullClassName(Context context, String className) {
        if (className.charAt(0) == '.') {
            return context.getPackageName() + className;
        }
        if (className.contains(".")) {
            return className;
        }
        return GuideView.class.getPackage().getName() + '.' + className;
    }

    public static int getPixels(float dp, Context context) {
        return (int) (dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public void setFirstRowPosition(int firstRowPosition) {
        this.firstRowPosition = firstRowPosition;
    }

    public void setFirstColumnPosition(int firstColumnPosition) {
        this.firstColumnPosition = firstColumnPosition;
    }

    public void setRowHeaderLayout(int rowHeaderLayout) {
        this.rowHeaderLayout = rowHeaderLayout;
    }

    public void setColumnHeaderLayout(int columnHeaderLayout) {
        this.columnHeaderLayout = columnHeaderLayout;
    }

    public void setCellLayout(int cellLayout) {
        this.cellLayout = cellLayout;
    }

    public void setCornerLayout(int cornerLayout) {
        this.cornerLayout = cornerLayout;
    }

    public int getFirstRowPosition() {
        return firstRowPosition;
    }

    public int getFirstColumnPosition() {
        return firstColumnPosition;
    }

    public onBindView getmOnBindView() {
        return mOnBindView;
    }
}
