package service;

import dominio.Lei;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import repository.LeiRepository;
import util.FacesUtil;

/**
 *
 * @author rosivaldo.adm
 */
public class LeiService implements Serializable {

    @Inject
    private LeiRepository leiRep;

    @Transactional
    public Lei cadastrar(Lei lei){
        lei.setDataCadastro(new Date());
        return leiRep.guardar(lei);
    }

    @Transactional
    public Lei atualizar(Lei lei) {
        return leiRep.guardar(lei);
    }

    @Transactional
    public void remover(Lei lei) {
        leiRep.remover(lei);
    }

    @Transactional
    public Lei desativar(Lei lei) {
        lei.setAtivo(false);
        return leiRep.guardar(lei);
    }

    @Transactional
    public List<Lei> listarTodos() {
        return leiRep.listAllAtivos();
    }
    
    @Transactional
    public Lei buscaUltimoLei() {
        return leiRep.getUltimoLei();
    }
    
    @Transactional
    public List<Lei> buscaUltimosLeis() {
        return leiRep.getUltimosLeis();
    }

    @Transactional
    public List<Lei> completaNome(String query) {
        return leiRep.completaNome(query);
    }

    @Transactional
    public Lei buscaLeiPeloNumero(String query) throws NoResultException {
        return leiRep.getLeibyNumero(query);
    }

    @Transactional
    public Integer numeroTotalDeLeis() {
        return leiRep.getNumeroTotalDeLeisAtivos();
    }

    public void retornaParaInfoLei(Lei lei) throws IOException {
        FacesUtil.redirecionarPara("/lei/informacao.xhtml?lei=" + lei.getId());
    }

    public void retornaParaInicio() throws IOException {
        FacesUtil.redirecionarPara("/tonacorrida");
    }

    public void retornaParaLogin() throws IOException {
        FacesUtil.redirecionarPara(FacesUtil.getExternalContext().getRequestContextPath()+"/login.xhtml");
    }

    public void encaminharSucesso() throws IOException {
        FacesUtil.redirecionarPara("/site/success.xhtml");
    }

    public List<Lei> filtraNumero(List<Lei> leis, Integer numeroLei) {
        if (numeroLei != null && !leis.isEmpty()) {
            return leis.stream().filter(d -> d.getNumero().equals(numeroLei)).collect(Collectors.toList());
        }
        return leis;
    }

    public List<Lei> filtraConteudo(List<Lei> leis, String conteudo) {
        if (!conteudo.equals("") && !leis.isEmpty()) {
            return leis.stream().filter(d -> d.getConteudo().toLowerCase().contains(conteudo.toLowerCase())).collect(Collectors.toList());
        }
        return leis;
    }

}
