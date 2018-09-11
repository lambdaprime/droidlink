package id.droidlink;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class EntitiesClassLoader extends ClassLoader {

    private Context context;

    public EntitiesClassLoader(Context context, ClassLoader parentLoader) {
        super(parentLoader);
        this.context = context;
    }

    @Override
    public InputStream getResourceAsStream(String resName) {
        try {
            return context.getAssets().open("entities/" + resName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
}
