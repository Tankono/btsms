package com.mcuhq.simplebluetooth.ui;

import android.view.View;

public interface ItemClick<E> {
    void onClickItem(View itemView,E entity, int pos);
}
