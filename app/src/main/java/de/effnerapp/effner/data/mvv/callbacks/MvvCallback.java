package de.effnerapp.effner.data.mvv.callbacks;

import de.effnerapp.effner.data.mvv.json.MvvResponse;

public interface MvvCallback {
    void onFinish(boolean isSuccess, MvvResponse data);
}
