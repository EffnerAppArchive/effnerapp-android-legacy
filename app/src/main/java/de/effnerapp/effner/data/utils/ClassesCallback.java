/*
 *  Created by SpyderScript on 11.10.2020, 15:35.
 *  Project: Effner.
 *  Copyright (c) 2020.
 */

package de.effnerapp.effner.data.utils;

import java.util.List;

public interface ClassesCallback {
    void onSuccess(List<String> classes);

    void onFailure();
}
