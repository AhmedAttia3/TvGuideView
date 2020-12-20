package com.hmaserv.guideview.ui;

import androidx.annotation.Nullable;

public class ViewPosition {
    int row, column;

    public ViewPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewPosition that = (ViewPosition) o;
        return row == that.row && column == that.column;
    }
}
