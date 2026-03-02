package com.invertec.stock_web.repository;

import com.invertec.stock_web.model.StockDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio para operaciones de consulta de stock en el dashboard IN01 / 1014
 */
@Repository
public class StockRepository {

    private final JdbcTemplate jdbcTemplate;

    public StockRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    // ------------------- AUTOCOMPLETE -------------------
    public List<Map<String, Object>> buscarCoincidenciasRapidas(String centro, String almacen, String buscar, int size) {
        String sql = """
            SELECT MATNR, Descripcion, ROW_NUMBER() OVER(ORDER BY MATNR) AS fila
            FROM vw_stock_area_final
            WHERE Centro = ? AND Almacen = ?
            AND (MATNR LIKE ? OR Descripcion LIKE ?)
            """;

        String like = "%" + buscar + "%";
        List<Object> params = List.of(centro, almacen, like, like);

        List<Map<String, Object>> resultados = jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("matnr", rs.getString("MATNR"));
            map.put("descripcion", rs.getString("Descripcion"));
            map.put("fila", rs.getInt("fila"));
            return map;
        });

        return resultados.size() > size ? resultados.subList(0, size) : resultados;
    }

    // ------------------- STOCK FILTRADO -------------------
    public List<StockDTO> obtenerFiltradoDashboard(String buscar) {
        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM vw_stock_area_final
                """);

        List<Object> params = new ArrayList<>();
        if (buscar != null && !buscar.isEmpty()) {
            sql.append(" AND (MATNR LIKE ? OR Descripcion LIKE ?)");
            String like = "%" + buscar + "%";
            params.add(like);
            params.add(like);
        }

        sql.append(" ORDER BY MATNR");

        return jdbcTemplate.query(sql.toString(), params.toArray(), rowMapper);
    }

    public List<StockDTO> obtenerFiltradoPaginadoDashboard(String buscar, int pagina, int size) {
        int offset = pagina * size;

        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM vw_stock_area_final
                """);

        List<Object> params = new ArrayList<>();
        if (buscar != null && !buscar.isEmpty()) {
            sql.append(" AND (MATNR LIKE ? OR Descripcion LIKE ?)");
            String like = "%" + buscar + "%";
            params.add(like);
            params.add(like);
        }

        sql.append(" ORDER BY MATNR OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(size);

        return jdbcTemplate.query(sql.toString(), params.toArray(), rowMapper);
    }

    public int contarDashboard(String buscar) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*)
                FROM vw_stock_area_final
                """);

        List<Object> params = new ArrayList<>();
        if (buscar != null && !buscar.isEmpty()) {
            sql.append(" AND (MATNR LIKE ? OR Descripcion LIKE ?)");
            String like = "%" + buscar + "%";
            params.add(like);
            params.add(like);
        }

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }

    // ------------------- STOCK BAJO -------------------
    public List<StockDTO> obtenerStockBajoDashboard() {
        String sql = """
                SELECT *
                FROM vw_stock_area_final
                WHERE stock_minimo IS NOT NULL
                AND StockLibre < stock_minimo
                ORDER BY MATNR
                """;

        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<StockDTO> obtenerStockBajoPaginadoDashboard(int pagina, int size) {
        int offset = pagina * size;
        String sql = """
                SELECT *
                FROM vw_stock_area_final
                WHERE stock_minimo IS NOT NULL
                AND StockLibre < stock_minimo
                ORDER BY MATNR
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;

        return jdbcTemplate.query(sql, new Object[]{offset, size}, rowMapper);
    }

    public int contarStockBajoDashboard() {
        String sql = """
                SELECT COUNT(*)
                FROM vw_stock_area_final
                WHERE
                stock_minimo IS NOT NULL
                AND StockLibre < stock_minimo
                """;

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public BigDecimal sumarValorTotalAlertasDashboard() {
        String sql = """
                SELECT SUM(ValorTotal)
                FROM vw_stock_area_final
                WHERE stock_minimo IS NOT NULL
                AND StockLibre < stock_minimo
                """;

        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    // ------------------- STOCK MINIMO -------------------
    public void guardarStockMinimo(String material, BigDecimal stockMinimo, String usuario) {
        String sql = """
                INSERT INTO dbo.stock_minimo_historial
                (material, centro, almacen, stock_minimo, usuario, fecha_modificacion)
                VALUES (?, '?', '?', ?, ?, GETDATE())
                """;

        jdbcTemplate.update(sql, material, stockMinimo, usuario);
    }

    public BigDecimal sumarValorTotalDashboard() {
        String sql = """
                SELECT SUM(ValorTotal)
                FROM vw_stock_area_final
                """;

        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

}