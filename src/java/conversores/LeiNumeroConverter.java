package conversores;

import dominio.Lei;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import repository.LeiRepository;
import util.FacesUtil;

/**
 *
 * @author rosivaldo.adm
 */
@FacesConverter("leiNumeroConverter")
public final class LeiNumeroConverter implements Converter {

    @Inject
    private LeiRepository leiRep;


    @PostConstruct
    public void init() {
    }

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                Lei lei = leiRep.getLeibyNumero(value);
                return lei;
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de Conversão", "Identificador de lei inválido."));
            } catch (Exception e) {
                try {
                    FacesUtil.redirecionarPara("/erro");
                } catch (IOException ex) {
                    Logger.getLogger(LeiNumeroConverter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            return null;
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null) {
            return String.valueOf(((Lei) object).getId());
        } else {
            return null;
        }
    }

}
