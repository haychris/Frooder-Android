package liberum.cibum.frooder;

import android.app.Application;

public class FrooderApplication extends Application {
    private static FrooderApplication me;

    @Override
    public void onCreate() {        
        super.onCreate();
        me = this ;

    }
    public static FrooderApplication getInstance() {
         return me;
    }
}
