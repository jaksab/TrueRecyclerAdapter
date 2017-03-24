package pro.oncreate.truerecycler;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */

class TrueUtils {
    static void removeParent(View v) {
        if (v != null) {
            ViewGroup oldParent = (ViewGroup) v.getParent();
            if (oldParent != null)
                oldParent.removeView(v);
        }
    }
}
