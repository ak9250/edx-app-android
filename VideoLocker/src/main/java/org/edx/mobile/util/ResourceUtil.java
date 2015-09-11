package org.edx.mobile.util;

import android.content.Context;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;

import com.squareup.phrase.Phrase;

import org.edx.mobile.base.MainApplication;


public class ResourceUtil {
    public static final String QuantityHolder = "quantity";

    public static String getResourceString(@StringRes int resourceId) {
        Context context = MainApplication.instance().getApplicationContext();
        return context.getString(resourceId);
    }

    public static CharSequence getFormattedString(@StringRes int resourceId, String key, String value) {
        if (value == null)
            value = "";
        return Phrase.from(ResourceUtil.getResourceString(resourceId))
                .put(key, value).format();
    }

    /**
     * mock Android's built-in context.getResources().getQuantityString API.
     * by default, quantity holder will be "quantity"
     *
     * @param resourceId
     * @param quantity
     * @return
     */
    public static CharSequence getFormattedStringForQuantity(@PluralsRes int resourceId, int quantity) {
        return getFormattedStringForQuantity(resourceId, QuantityHolder, quantity);
    }


    public static CharSequence getFormattedStringForQuantity(@PluralsRes int resourceId, String key, int quantity) {
        Context context = MainApplication.instance().getApplicationContext();
        String template = context.getResources().getQuantityString(resourceId, quantity);
        return Phrase.from(template).put(key, quantity + "").format();
    }
}
