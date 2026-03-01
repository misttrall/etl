package com.invertec.stock_web.repository;

import com.invertec.stock_web.model.StockDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("!local")
public class StockRepository implements IStockRepository {

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

    @Override
    public List<StockDTO> obtenerFiltradoPaginado(
            String centro, String almacen, String buscar, int pagina, int size) {

        int offset = pagina * size;

        StringBuilder sql = new StringBuilder("""
                SELECT *
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

        if (buscar != null) {
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

    @Override
    public int contar(String centro, String almacen, String buscar) {

        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(*)
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

        if (buscar != null) {
            sql.append(" AND (MATNR LIKE ? OR Descripcion LIKE ?)");
            params.add("%" + buscar + "%");
            params.add("%" + buscar + "%");
        }

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }

    @Override
    public List<StockDTO> obtenerStockBajoFiltrado(String centro, String almacen) {
        return new ArrayList<>();
    }

    @Override
    public int contarStockBajo(String centro, String almacen) {
        return 0;
    }

    @Override
    public BigDecimal sumarValorTotalAlertas(String centro, String almacen) {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumarValorTotal(String centro, String almacen) {
        return BigDecimal.ZERO;
    }

    @Override
    public void guardarStockMinimo(
            String material, String centro, String almacen,
            BigDecimal stockMinimo, String usuario) {
    }
}