
package validadores;

import dao.DaoFactory;
import dominio.Lei;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author rosivaldo.adm
 */
@FacesValidator("numeroLeiUniqueValidator")
public class NumeroLeiUniqueValidator implements Validator {

    @Inject
    private EntityManager em;

    private DaoFactory dao;

    @PostConstruct
    public void init() {
        this.dao = new DaoFactory(em);
    }

    @Override
    public void validate(FacesContext context, UIComponent componente, Object value) throws ValidatorException {

        List<Lei> leis = dao.getLeiDao().jpqlEqualsID(Integer.valueOf(value.toString()), "numero");
        if (!leis.isEmpty()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Número já utilizado.", "Número já utilizado em outra lei");
            throw new ValidatorException(msg);
        }
    }

}
