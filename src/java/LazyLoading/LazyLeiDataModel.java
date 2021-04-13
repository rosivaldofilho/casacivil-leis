/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LazyLoading;

import dominio.Lei;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import repository.LeiRepository;

/**
 *
 * @author rosivaldo.adm
 */
@Named
@ViewScoped
public class LazyLeiDataModel extends LazyDataModel<Lei> implements Serializable {

    @Inject
    private LeiRepository leiRep;

    private List<Lei> datasource;
    private FiltroBusca filtro = new FiltroBusca();

    public LazyLeiDataModel() {
    }

    @Override
    public Lei getRowData(String rowKey) {
        for (Lei lei : datasource) {
            if (lei.getNumero().toString().equals(rowKey)) {
                return lei;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(Lei lei) {
        return lei.getNumero();
    }

    @Override
    public List<Lei> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

        getFiltro().setPrimeiroRegistro(first);
        getFiltro().setQuantidadeRegistros(pageSize);
        getFiltro().setAscendente(SortOrder.ASCENDING.equals(sortOrder));
        getFiltro().setPropriedadeOrdenacao(sortField);
        getFiltro().setFilters(filters);

        setRowCount(leiRep.quantidadeFiltrados(getFiltro()));

        return datasource = leiRep.filtrados(getFiltro());
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
}
