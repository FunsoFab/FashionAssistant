package com.funsooyenuga.fashionassistant.clientdetail;

/**
 * Created by FAB THE GREAT on 10/12/2016.
 */

public interface ClientDetailContract {

    interface View {

        void showMeasurement();
    }

    interface Presenter {

        void getMeasurement(String clientId);
    }
}
