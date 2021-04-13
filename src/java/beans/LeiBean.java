package beans;

import security.Seguranca;
import dominio.Lei;
import dominio.Pessoa;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;
import repository.LeiRepository;
import LazyLoading.FiltroBusca;
import java.util.Date;
import org.primefaces.event.TabChangeEvent;
import service.LeiService;
import util.FacesUtil;
import static util.FacesUtil.redirecionarPara;
import util.Utilitarios;

/**
 *
 * @author rosivaldo
 */
@Named
@ViewScoped
public class LeiBean implements Serializable {

    @Inject
    private LeiService leiService;
    @Inject
    private LeiRepository leiRep;

    private UploadedFile upFile;
    private CroppedImage croppeFoto;
    private String imageTemp = "";
    private String pdfTemp = "";

    private Pessoa usuarioLogado = new Pessoa();
    private final Seguranca seguranca = new Seguranca();

    private String confSenha;

    private Lei lei;

    private List<Lei> leis;

    private List<Lei> leisFiltrados;

    private List<Lei> ultimosLeis;

    private FiltroBusca filtro = new FiltroBusca();
    private LazyDataModel<Lei> model;
    private FacesContext facesContext;
    private Lei ultimoLei;
    private Integer qtdLeis;

    public LeiBean() {
        lei = new Lei();
        leis = new ArrayList();
        usuarioLogado = seguranca.getUsuario();

        model = new LazyDataModel<Lei>() {

            private static final long serialVersionUID = 1L;

            @Override
            public Object getRowKey(Lei lei) {
                return lei.getNumero();
            }

            @Override
            public Lei getRowData(String rowKey) {
                for (Lei lei : model) {
                    if (lei.getNumero().toString().equals(rowKey)) {
                        return lei;
                    }
                }

                return null;
            }

            @Override
            public List<Lei> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                getFiltro().setPrimeiroRegistro(first);
                getFiltro().setQuantidadeRegistros(pageSize);
                getFiltro().setAscendente(SortOrder.ASCENDING.equals(sortOrder));
                getFiltro().setPropriedadeOrdenacao(sortField);
                getFiltro().setFilters(filters);

                setRowCount(leiRep.quantidadeFiltrados(getFiltro()));

                return leiRep.filtrados(getFiltro());
            }

        };
    }

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void saindo() {
        limpaArquivoTemporario();
    }
    
    public Date getDataAtual() {
        return new Date();
    }

    public void limparCamposBusca(TabChangeEvent event) {
        lei = new Lei();
        filtro = new FiltroBusca();
    }
    public void filtraLeisIndex() {

        getFiltro().setDescricao(lei.getConteudo());
        getFiltro().setNumero(lei.getNumero());
        getFiltro().setAscendente(false);

        //setQtdLeis(leiRep.quantidadeFiltrados(getFiltro()));
        leisFiltrados = leiRep.criarCriteriaParaFiltroIndex(getFiltro());
    }

    public void onRowSelect(SelectEvent event) throws IOException {
        lei = (Lei) event.getObject();
        redirecionarPara(FacesUtil.getExternalContext().getRequestContextPath() + "/lei/cadastro.xhtml?lei=" + lei.getNumero());
    }

    public void excluirLei(Lei lei) {
        leiService.remover(lei);
    }

    public void desativarLei() {
        leiService.desativar(lei);
        FacesUtil.addInfoMessage("Lei desativado com sucesso!");
    }

    public void inicializarLeis() throws IOException {
        if (!FacesUtil.isPostback()) {
            leis = leiService.listarTodos();
            leisFiltrados = leis;
        }
    }

    public void inicializarUltimoLei() throws IOException {
        if (!FacesUtil.isPostback()) {
            ultimoLei = leiService.buscaUltimoLei();
        }
    }

    public void inicializarUltimasLeis() throws IOException {
        if (!FacesUtil.isPostback()) {
            ultimosLeis = leiService.buscaUltimosLeis();
            ultimoLei = ultimosLeis.get(0);
            ultimosLeis.remove(0);
        }
    }

    public String getDataLeiExtenso() {
        if (lei.getId() != null) {
            return Utilitarios.formataDataBRExtenso(lei.getDataLei());
        } else {
            return "";
        }
    }

    public List<Lei> completaNome(String query) {
        List<Lei> sugestoes = leiService.completaNome(query);
        return sugestoes;
    }

    public void salvarLei() {
        if (lei.getId() != null) {
            atualizarLei();
        } else {
            cadastrarLei();
        }
        pdfTemp = "";
    }

    public void cadastrarLei() {

        try {
            lei.setPessoa(usuarioLogado);
            lei = leiService.cadastrar(lei);
            actionGuardarArquivo();
            FacesUtil.addInfoMessage("Usuário Cadastrado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErrorMessage("Erro ao salvar informações!");
        }
    }

    public void atualizarLei() {
        try {
            lei = leiService.atualizar(lei);
            if (!this.getImageTemp().isEmpty()) {
                actionGuardarArquivo();
            }
            FacesUtil.addInfoMessage("Informações salvas com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErrorMessage("Erro ao salvar informações!");
        }
    }

    public void filtraLeis() {
        leisFiltrados = leiService.listarTodos();
        leisFiltrados = leiService.filtraConteudo(leisFiltrados, lei.getConteudo());
        leisFiltrados = leiService.filtraNumero(leisFiltrados, lei.getNumero());
    }

    //Atualizando contexto para limpar cache do pdfView
    public void atualizaContexto() {
        facesContext = FacesContext.getCurrentInstance();
    }

    // ####### UPLOAD DE ARQUIVO PDF ########
    public void carregaArquivo(FileUploadEvent event) {
        upFile = event.getFile();
    }

    public boolean uploadArquivo(FileUploadEvent event) {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/temp");
            String archivo = path + File.separator + event.getFile().getFileName();
            pdfTemp = "/resources/temp/" + event.getFile().getFileName();

            InputStream inputStream;
            try (FileOutputStream fileOutputStream = new FileOutputStream(archivo)) {
                byte[] buffer = new byte[6124];
                int bulk;
                inputStream = event.getFile().getInputstream();
                while (true) {
                    bulk = inputStream.read(buffer);
                    if (bulk < 0) {
                        break;
                    }
                    fileOutputStream.write(buffer, 0, bulk);
                    fileOutputStream.flush();
                }
            }
            inputStream.close();
            this.setImageTemp(event.getFile().getFileName());

        } catch (IOException e) {
            e.printStackTrace();
            util.FacesUtil.addErrorMessage("Erro ao subir arquivo");
            return false;
        }
        return true;
    }

    public void actionFoto() {
        this.croppeFoto = null;
        this.imageTemp = null;
    }

    public void actionGuardarArquivo() {
        String relativePath = "/resources/pdf";
        String nomeArquivo = "lei_" + lei.getNumero().toString() + ".pdf";
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath(relativePath);
        String archivo = path + File.separator + nomeArquivo;
        try {

            InputStream inputStream;
            try (OutputStream outStream = new FileOutputStream(new File(archivo))) {
                inputStream = new FileInputStream(path + "../../temp/" + this.getImageTemp());
                byte[] buffer = new byte[6124];
                int bulk;
                while (true) {
                    bulk = inputStream.read(buffer);
                    if (bulk < 0) {
                        break;
                    }
                    outStream.write(buffer, 0, bulk);
                    outStream.flush();
                }
            }
            inputStream.close();

            lei.setLinkArquivo(relativePath + File.separator + nomeArquivo);
            leiService.atualizar(lei);
            limpaArquivoTemporario();
            actionFoto();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void limpaArquivoTemporario() {
        if (!this.getImageTemp().isEmpty()) {
            String pathTemp = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/temp");
            String archivoTemp = pathTemp + File.separator + this.getImageTemp();
            File arquivoTemp = new File(archivoTemp);
            arquivoTemp.delete();
        }
    }

    /**
     * @return the lei
     */
    public Lei getLei() {
        return lei;
    }

    /**
     * @param lei the lei to set
     */
    public void setLei(Lei lei) {
        this.lei = lei;
    }

    /**
     * @return the leis
     */
    public List<Lei> getLeis() {
        return leis;
    }

    /**
     * @param leis the leis to set
     */
    public void setLeis(List<Lei> leis) {
        this.leis = leis;
    }

    /**
     * @return the leiService
     */
    public LeiService getLeiRep() {
        return leiService;
    }

    /**
     * @param leiService the leiService to set
     */
    public void setLeiRep(LeiService leiService) {
        this.leiService = leiService;
    }

    /**
     * @return the upFile
     */
    public UploadedFile getUpFile() {
        return upFile;
    }

    /**
     * @param upFile the upFile to set
     */
    public void setUpFile(UploadedFile upFile) {
        this.upFile = upFile;
    }

    /**
     * @return the leisFiltrados
     */
    public List<Lei> getLeisFiltrados() {
        return leisFiltrados;
    }

    /**
     * @param leisFiltrados the leisFiltrados to set
     */
    public void setLeisFiltrados(List<Lei> leisFiltrados) {
        this.leisFiltrados = leisFiltrados;
    }

    /**
     * @return the croppeFoto
     */
    public CroppedImage getCroppeFoto() {
        return croppeFoto;
    }

    /**
     * @param croppeFoto the croppeFoto to set
     */
    public void setCroppeFoto(CroppedImage croppeFoto) {
        this.croppeFoto = croppeFoto;
    }

    /**
     * @return the imageTemp
     */
    public String getImageTemp() {
        return imageTemp;
    }

    /**
     * @param imageTemp the imageTemp to set
     */
    public void setImageTemp(String imageTemp) {
        this.imageTemp = imageTemp;
    }

    /**
     * @return the confSenha
     */
    public String getConfSenha() {
        return confSenha;
    }

    /**
     * @param confSenha the confSenha to set
     */
    public void setConfSenha(String confSenha) {
        this.confSenha = confSenha;
    }

    /**
     * @return the filtro
     */
    public FiltroBusca getFiltro() {
        return filtro;
    }

    /**
     * @param filtro the filtro to set
     */
    public void setFiltro(FiltroBusca filtro) {
        this.filtro = filtro;
    }

    /**
     * @return the model
     */
    public LazyDataModel<Lei> getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(LazyDataModel<Lei> model) {
        this.model = model;
    }

    /**
     * @return the pdfTemp
     */
    public String getPdfTemp() {
        return pdfTemp;
    }

    /**
     * @param pdfTemp the pdfTemp to set
     */
    public void setPdfTemp(String pdfTemp) {
        this.pdfTemp = pdfTemp;
    }

    /**
     * @return the facesContext
     */
    public FacesContext getFacesContext() {
        atualizaContexto();
        return facesContext;
    }

    /**
     * @param facesContext the facesContext to set
     */
    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    /**
     * @return the ultimoLei
     */
    public Lei getUltimoLei() {
        return ultimoLei;
    }

    /**
     * @param ultimoLei the ultimoLei to set
     */
    public void setUltimoLei(Lei ultimoLei) {
        this.ultimoLei = ultimoLei;
    }

    /**
     * @return the ultimosLeis
     */
    public List<Lei> getUltimosLeis() {
        return ultimosLeis;
    }

    /**
     * @param ultimosLeis the ultimosLeis to set
     */
    public void setUltimosLeis(List<Lei> ultimosLeis) {
        this.ultimosLeis = ultimosLeis;
    }

    /**
     * @return the qtdLeis
     */
    public Integer getQtdLeis() {
        return qtdLeis;
    }

    /**
     * @param qtdLeis the qtdLeis to set
     */
    public void setQtdLeis(Integer qtdLeis) {
        this.qtdLeis = qtdLeis;
    }

}
