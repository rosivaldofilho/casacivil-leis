package dominio;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author rosivaldo.adm
 */
@Entity
public class Lei implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 10, nullable = false, unique = true)
    private Integer numero;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(length = 255, nullable = false)
    private Date dataLei;

    @Column(length = 65535, columnDefinition="TEXT", nullable = false)
    private String ementa;

    @Column(length = 16777215, columnDefinition="MEDIUMTEXT", nullable = false)
    private String conteudo;

    @Column(length = 255, nullable = true)
    private String linkArquivo;
    
    @Column(length = 255, nullable = true)
    private String doe;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(nullable = false)
    private Date dataCadastro;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa = new Pessoa();
    
    @Column(nullable = false)
    private boolean ativo = true;

    /**
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    /**
     * @return the dataLei
     */
    public Date getDataLei() {
        return dataLei;
    }

    /**
     * @param dataLei the dataLei to set
     */
    public void setDataLei(Date dataLei) {
        this.dataLei = dataLei;
    }

    /**
     * @return the ementa
     */
    public String getEmenta() {
        return ementa;
    }

    /**
     * @param ementa the ementa to set
     */
    public void setEmenta(String ementa) {
        this.ementa = ementa;
    }

    /**
     * @return the conteudo
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * @param conteudo the conteudo to set
     */
    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    /**
     * @return the linkArquivo
     */
    public String getLinkArquivo() {
        return linkArquivo;
    }

    /**
     * @param linkArquivo the linkArquivo to set
     */
    public void setLinkArquivo(String linkArquivo) {
        this.linkArquivo = linkArquivo;
    }

    /**
     * @return the dataCadastro
     */
    public Date getDataCadastro() {
        return dataCadastro;
    }

    /**
     * @param dataCadastro the dataCadastro to set
     */
    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the pessoa
     */
    public Pessoa getPessoa() {
        return pessoa;
    }

    /**
     * @param pessoa the pessoa to set
     */
    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    /**
     * @return the ativo
     */
    public boolean isAtivo() {
        return ativo;
    }

    /**
     * @param ativo the ativo to set
     */
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    /**
     * @return the doe
     */
    public String getDoe() {
        return doe;
    }

    /**
     * @param doe the doe to set
     */
    public void setDoe(String doe) {
        this.doe = doe;
    }

}
