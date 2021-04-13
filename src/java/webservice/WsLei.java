package webservice;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dominio.Lei;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import repository.LeiRepository;
import service.LeiService;
import util.HtmlToText;

/**
 *
 * @author rosivaldo.adm
 */
@Path("/v1")
@RequestScoped
public class WsLei implements Serializable {

    @Inject
    private LeiRepository initRep;

    @Inject
    private LeiService leiService;
    
    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();

    //Recebe a notificação de um lei do Boleto fácil
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Path("/notificacao")
    public void wsServerPostBoleto(@FormParam("paymentToken") String paymentToken) {
        System.out.println("paymentToken: " + paymentToken);
    }
    
    //Lista leis
    @GET
    @Path("/leis")
    @Produces({"application/json; charset=UTF-8"})
    public String resposta() {
        List<Lei> leis = leiService.buscaUltimosLeis();
        return gson.toJson(leis);
    }

    //Busca lei pelo número
    @GET
    @Path("/lei/{numero}")
    @Produces({"application/json; charset=UTF-8"})
    public String confirmaLei(@PathParam("numero") String numero) throws Exception {
        HtmlToText htmlToText = new HtmlToText();
        Lei lei = leiService.buscaLeiPeloNumero(numero);
        lei.setPessoa(null);
        htmlToText.parse(gson.toJson(lei));
        return htmlToText.getText();
    }



}
