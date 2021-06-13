package de.effnerapp.effner.data.mvv.callbacks;

import de.effnerapp.effner.data.mvv.json.FindStopResponse;

public interface FindStopCallback {
    void onFinish(FindStopResponse data);
}
