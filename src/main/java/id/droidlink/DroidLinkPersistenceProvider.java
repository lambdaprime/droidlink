package id.droidlink;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.PersistenceProvider;

import android.content.Context;

public class DroidLinkPersistenceProvider {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public EntityManagerFactory createEntityManagerFactory(Context context, String persistenceUnit, Map properties) {
        EntitiesClassLoader classLoader = new EntitiesClassLoader(context, getClass().getClassLoader());
        properties.put("eclipselink.classloader", classLoader);
        properties.put("eclipselink.persistencexml", "persistence.xml");
        Logger.getLogger("org").setLevel(Level.ALL);
        return new PersistenceProvider().createEntityManagerFactory(persistenceUnit, properties);
    }

}
