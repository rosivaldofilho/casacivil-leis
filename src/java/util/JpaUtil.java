
package util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Rosivaldo Souza
 */
public class JpaUtil {

    private static final EntityManagerFactory emf;
    private static EntityManager em;

    static {
        try {
            emf = Persistence.createEntityManagerFactory("leis_estaduais_PU");
        } catch (Throwable ex) {
            // Log exception!   
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {

        em = emf.createEntityManager();

        return em;
    }
}
