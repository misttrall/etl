package com.invertec.stock_web.repository;

import com.invertec.stock_web.model.StockDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para operaciones de consulta de stock.
 * 
 * <p>
 * Este repositorio interactúa con la vista `vw_stock_area_final` para obtener
 * información de stock de materiales. Permite:
 * </p>
 * <ul>
 *     <li>Consultar stock filtrado por centro y almacén con paginación.</li>
 *     <li>Contar la cantidad de registros según filtros.</li>
 *     <li>Consultar y contar materiales con stock bajo (menor que el mínimo).</li>
 * </ul>
 */
@Repository
public class StockRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor que inyecta el JdbcTemplate para la conexión a base de datos.
     *
     * @param jdbcTemplate instancia de JdbcTemplate configurada en Spring
     */
    public StockRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * RowMapper que convierte cada fila de la consulta en un objeto StockDTO.
     */
    private RowMapper<StockDTO> rowMapper = (rs, rowNum) -> {
        StockDTO s = new StockDTO();
        s.setMatnr(rs.getString("MATNR"));
        s.setDescripcion(rs.getString("Descripcion"));
        s.setCentro(rs.getString("Centro"));
        s.setAlmacen(rs.getString("Almacen"));
        s.setStockLibre(rs.getBigDecimal("StockLibre"));
        s.setPrecio(rs.getBigDecimal("Precio"));
        s.setValorTotal(rs.getBigDecimal("ValorTotal"));
        s.setStockMinimo(rs.getBigDecimal("stock_minimo"));
        return s;
    };

    /**
     * Obtiene una lista de StockDTO filtrada por centro, almacén y texto de búsqueda
     * con paginación.
     *
     * @param centro  filtro por centro (opcional)
     * @param almacen filtro por almacén (opcional)
     * @param buscar  texto a buscar en MATNR o Descripcion (opcional)
     * @param pagina  número de página (0-indexado)
     * @param size    cantidad de registros por página
     * @return lista de StockDTO que cumplen los filtros y paginación
     */
    public List<StockDTO> obtenerFiltradoPaginado(
            String centro,
            String almacen,
            String buscar,
            int pagina,
            int size) {

        int offset = pagina * size;

        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM vw_stock_area_final
                WHERE Centro = 'IN01'
                AND ALMACEN = '1014'
                """);

        List<Object> params = new ArrayList<>();

        if (centro != null && !centro.isEmpty()) {
            sql.append(" AND Centro = ?");
            params.add(centro);
        }

        if (almacen != null && !almacen.isEmpty()) {
            sql.append(" AND Almacen = ?");
            params.add(almacen);
        }

        if (buscar != null && !buscar.isEmpty()) {
            sql.append(" AND (MATNR LIKE ? OR Descripcion LIKE ?)");
            params.add("%" + buscar + "%");
            params.add("%" + buscar + "%");
        }

        sql.append(" ORDER BY MATNR");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        params.add(offset);
        params.add(size);

        return jdbcTemplate.query(sql.toString(), params.toArray(), rowMapper);
    }

    /**
     * Cuenta la cantidad de registros de stock según los filtros proporcionados.
     *
     * @param centro  filtro por centro (opcional)
     * @param almacen filtro por almacén (opcional)
     * @param buscar  texto a buscar en MATNR o Descripcion (opcional)
     * @return número total de registros que cumplen los filtros
     */
    public int contar(String centro, String almacen, String buscar) {

        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*)
                FROM vw_stock_area_final
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (centro != null && !centro.isEmpty()) {
            sql.append(" AND Centro = ?");
            params.add(centro);
        }

        if (almacen != null && !almacen.isEmpty()) {
            sql.append(" AND Almacen = ?");
            params.add(almacen);
        }

        if (buscar != null && !buscar.isEmpty()) {
            sql.append(" AND (MATNR LIKE ? OR Descripcion LIKE ?)");
            params.add("%" + buscar + "%");
            params.add("%" + buscar + "%");
        }

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }

    /**
     * Obtiene la lista de StockDTO que tienen stock bajo (StockLibre < stock_minimo)
     * filtrada por centro y almacén, con paginación.
     *
     * @param centro  filtro por centro (opcional)
     * @param almacen filtro por almacén (opcional)
     * @param pagina  número de página (0-indexado)
     * @param size    cantidad de registros por página
     * @return lista de StockDTO con stock bajo
     */
    public List<StockDTO> obtenerStockBajoPaginado(
            String centro,
            String almacen,
            int pagina,
            int size) {

        int offset = pagina * size;

        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM vw_stock_area_final
                WHERE stock_minimo IS NOT NULL
                AND StockLibre < stock_minimo
                """);

        List<Object> params = new ArrayList<>();

        if (centro != null && !centro.isEmpty()) {
            sql.append(" AND Centro = ?");
            params.add(centro);
        }

        if (almacen != null && !almacen.isEmpty()) {
            sql.append(" AND Almacen = ?");
            params.add(almacen);
        }

        sql.append(" ORDER BY MATNR");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        params.add(offset);
        params.add(size);

        return jdbcTemplate.query(sql.toString(), params.toArray(), rowMapper);
    }
public List<StockDTO> obtenerStockBajoFiltrado(
        String centro,
        String almacen) {

    StringBuilder sql = new StringBuilder("""
            SELECT *
            FROM vw_stock_area_final
            WHERE stock_minimo IS NOT NULL
            AND StockLibre < stock_minimo
            """);

    List<Object> params = new ArrayList<>();

    if (centro != null && !centro.isEmpty()) {
        sql.append(" AND Centro = ?");
        params.add(centro);
    }

    if (almacen != null && !almacen.isEmpty()) {
        sql.append(" AND Almacen = ?");
        params.add(almacen);
    }

    sql.append(" ORDER BY MATNR");

    return jdbcTemplate.query(sql.toString(), params.toArray(), rowMapper);
}
    /**
     * Cuenta la cantidad de registros con stock bajo según centro y almacén.
     *
     * @param centro  filtro por centro (opcional)
     * @param almacen filtro por almacén (opcional)
     * @return número total de registros con stock bajo
     */
    public int contarStockBajo(String centro, String almacen) {

        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*)
                FROM vw_stock_area_final
                WHERE stock_minimo IS NOT NULL
                AND StockLibre < stock_minimo
                """);

        List<Object> params = new ArrayList<>();

        if (centro != null && !centro.isEmpty()) {
            sql.append(" AND Centro = ?");
            params.add(centro);
        }

        if (almacen != null && !almacen.isEmpty()) {
            sql.append(" AND Almacen = ?");
            params.add(almacen);
        }

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }
    public BigDecimal sumarValorTotal(String centro, String almacen) {

    StringBuilder sql = new StringBuilder("""
            SELECT SUM(ValorTotal)
            FROM vw_stock_area_final
            WHERE 1=1
            """);

    List<Object> params = new ArrayList<>();

    if (centro != null) {
        sql.append(" AND Centro = ?");
        params.add(centro);
    }

    if (almacen != null) {
        sql.append(" AND Almacen = ?");
        params.add(almacen);
    }

    return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), BigDecimal.class);
}

}