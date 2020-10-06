/*
 *  Created by SpyderScript on 21.09.2020, 10:12.
 *  Project: Effner.
 *  Copyright (c) 2020.
 */

package de.effnerapp.effner.data.utils;

import de.effnerapp.effner.data.model.DataResponse;

public interface ApiCallback {
    void onFinish(boolean isSuccess, DataResponse data);
}
