package com.invertec.stock_web.repository;

import com.invertec.stock_web.model.StockDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StockRepository {

    private final JdbcTemplate jdbcTemplate;

    public StockRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<StockDTO> rowMapper = (rs, rowNum) -> {
        StockDTO s = new StockDTO();
        s.setMatnr(rs.getString("MATNR"));
        s.setDescripcion(rs.getString("Descripcion"));
        s.setCentro(rs.getString("Centro"));
        s.setAlmacen(rs.getString("Almacen"));
        s.setStockLibre(rs.getBigDecimal("StockLibre"));
        s.setPrecio(rs.getBigDecimal("Precio"));
        s.setValorTotal(rs.getBigDecimal("ValorTotal"));
        s.setArea(rs.getString("Area"));
        s.setStockMinimo(rs.getBigDecimal("stock_minimo"));
        return s;
    };

    // ------------------- FILTRADO Y PAGINADO -------------------
    public List<StockDTO> obtenerFiltradoDashboard(String buscar, String centro, String almacen, String area) {
        return ejecutarConsultaBase(buscar, centro, almacen, area, -1, -1);
    }

    public List<StockDTO> obtenerFiltradoPaginadoDashboard(String buscar, String centro, String almacen, String area, int pagina, int size) {
        int offset = pagina * size;
        return ejecutarConsultaBase(buscar, centro, almacen, area, offset, size);
    }

    private List<StockDTO> ejecutarConsultaBase(String buscar, String centro, String almacen, String area, int offset, int size) {
        StringBuilder sql = new StringBuilder("""
            SELECT v.MATNR, v.Descripcion, v.Centro, v.Almacen, v.StockLibre, v.Precio, v.ValorTotal, 
                   a.nombre AS Area, sm.stock_minimo
            FROM dbo.vw_stock_actual AS v
            INNER JOIN dbo.material_centro_almacen AS mca 
                ON v.MATNR = mca.material AND v.Centro = mca.centro AND v.Almacen = mca.almacen
            LEFT JOIN dbo.material_area AS ma ON v.MATNR = ma.material
            LEFT JOIN dbo.area AS a ON ma.area_id = a.id
            LEFT JOIN dbo.vw_stock_minimo_actual AS sm 
                ON v.MATNR = sm.material AND v.Centro = sm.centro AND v.Almacen = sm.almacen
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (buscar != null && !buscar.isBlank()) {
            sql.append(" AND (v.MATNR LIKE ? OR v.Descripcion LIKE ?)");
            String like = "%" + buscar + "%";
            params.add(like);
            params.add(like);
        }
        if (centro != null && !centro.isBlank()) {
            sql.append(" AND v.Centro = ?");
            params.add(centro);
        }
        if (almacen != null && !almacen.isBlank()) {
            sql.append(" AND v.Almacen = ?");
            params.add(almacen);
        }
        if (area != null && !area.isBlank()) {
            sql.append(" AND a.nombre = ?");
            params.add(area);
        }

        sql.append(" ORDER BY v.MATNR");

        if (offset >= 0 && size > 0) {
            sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            params.add(offset);
            params.add(size);
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), rowMapper);
    }

    public int contarDashboard(String buscar, String centro, String almacen, String area) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM dbo.vw_stock_actual AS v
            INNER JOIN dbo.material_centro_almacen AS mca 
                ON v.MATNR = mca.material AND v.Centro = mca.centro AND v.Almacen = mca.almacen
            LEFT JOIN dbo.material_area AS ma ON v.MATNR = ma.material
            LEFT JOIN dbo.area AS a ON ma.area_id = a.id
            LEFT JOIN dbo.vw_stock_minimo_actual AS sm 
                ON v.MATNR = sm.material AND v.Centro = sm.centro AND v.Almacen = sm.almacen
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();
        if (buscar != null && !buscar.isBlank()) {
            sql.append(" AND (v.MATNR LIKE ? OR v.Descripcion LIKE ?)");
            String like = "%" + buscar + "%";
            params.add(like);
            params.add(like);
        }
        if (centro != null && !centro.isBlank()) {
            sql.append(" AND v.Centro = ?");
            params.add(centro);
        }
        if (almacen != null && !almacen.isBlank()) {
            sql.append(" AND v.Almacen = ?");
            params.add(almacen);
        }
        if (area != null && !area.isBlank()) {
            sql.append(" AND a.nombre = ?");
            params.add(area);
        }

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }

    // ------------------- AUTOCOMPLETE -------------------
    public List<Map<String, Object>> buscarCoincidenciasRapidas(String buscar, String centro, String almacen, String area, int size) {
        StringBuilder sql = new StringBuilder("""
            SELECT v.MATNR, v.Descripcion, ROW_NUMBER() OVER(ORDER BY v.MATNR) AS fila
            FROM dbo.vw_stock_actual AS v
            INNER JOIN dbo.material_centro_almacen AS mca 
                ON v.MATNR = mca.material AND v.Centro = mca.centro AND v.Almacen = mca.almacen
            LEFT JOIN dbo.material_area AS ma ON v.MATNR = ma.material
            LEFT JOIN dbo.area AS a ON ma.area_id = a.id
            LEFT JOIN dbo.vw_stock_minimo_actual AS sm 
                ON v.MATNR = sm.material AND v.Centro = sm.centro AND v.Almacen = sm.almacen
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();
        if (buscar != null && !buscar.isBlank()) {
            sql.append(" AND (v.MATNR LIKE ? OR v.Descripcion LIKE ?)");
            String like = "%" + buscar + "%";
            params.add(like);
            params.add(like);
        }
        if (centro != null && !centro.isBlank()) { sql.append(" AND v.Centro = ?"); params.add(centro);}
        if (almacen != null && !almacen.isBlank()) { sql.append(" AND v.Almacen = ?"); params.add(almacen);}
        if (area != null && !area.isBlank()) { sql.append(" AND a.nombre = ?"); params.add(area);}

        List<Map<String, Object>> resultados = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("matnr", rs.getString("MATNR"));
            map.put("descripcion", rs.getString("Descripcion"));
            map.put("fila", rs.getInt("fila"));
            return map;
        });

        return resultados.size() > size ? resultados.subList(0, size) : resultados;
    }

// En StockRepository
public List<String> listarAreasDisponibles(String centro, String almacen) {
    StringBuilder sql = new StringBuilder("""
        SELECT DISTINCT a.nombre
        FROM dbo.area AS a
        LEFT JOIN dbo.material_area AS ma ON ma.area_id = a.id
        LEFT JOIN dbo.material_centro_almacen AS mca ON ma.material = mca.material
        WHERE 1=1
    """);

    List<Object> params = new ArrayList<>();
    if (centro != null && !centro.isBlank()) {
        sql.append(" AND mca.centro = ?");
        params.add(centro);
    }
    if (almacen != null && !almacen.isBlank()) {
        sql.append(" AND mca.almacen = ?");
        params.add(almacen);
    }

    sql.append(" ORDER BY a.nombre");

    return jdbcTemplate.queryForList(sql.toString(), params.toArray(), String.class);
}

    // ------------------- STOCK MINIMO -------------------
    public void guardarStockMinimo(String material, String centro, String almacen, BigDecimal stockMinimo, String usuario) {
        String sql = """
            INSERT INTO dbo.stock_minimo_historial
            (material, centro, almacen, stock_minimo, usuario, fecha_modificacion)
            VALUES (?, ?, ?, ?, ?, GETDATE())
        """;
        jdbcTemplate.update(sql, material, centro, almacen, stockMinimo, usuario);
    }

    // ------------------- ALERTAS -------------------
    public List<StockDTO> obtenerStockBajoDashboard(String centro, String almacen) {
        StringBuilder sql = new StringBuilder("""
            SELECT v.MATNR, v.Descripcion, v.Centro, v.Almacen, v.StockLibre, v.Precio, v.ValorTotal, 
                   a.nombre AS Area, sm.stock_minimo
            FROM dbo.vw_stock_actual AS v
            INNER JOIN dbo.material_centro_almacen AS mca 
                ON v.MATNR = mca.material AND v.Centro = mca.centro AND v.Almacen = mca.almacen
            LEFT JOIN dbo.material_area AS ma ON v.MATNR = ma.material
            LEFT JOIN dbo.area AS a ON ma.area_id = a.id
            LEFT JOIN dbo.vw_stock_minimo_actual AS sm 
                ON v.MATNR = sm.material AND v.Centro = sm.centro AND v.Almacen = sm.almacen
            WHERE sm.stock_minimo IS NOT NULL AND v.StockLibre < sm.stock_minimo
        """);

        List<Object> params = new ArrayList<>();
        if (centro != null && !centro.isBlank()) { sql.append(" AND v.Centro = ?"); params.add(centro);}
        if (almacen != null && !almacen.isBlank()) { sql.append(" AND v.Almacen = ?"); params.add(almacen);}

        return jdbcTemplate.query(sql.toString(), params.toArray(), rowMapper);
    }

    public int contarStockBajoDashboard(String centro, String almacen) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM dbo.vw_stock_actual AS v
            INNER JOIN dbo.material_centro_almacen AS mca 
                ON v.MATNR = mca.material AND v.Centro = mca.centro AND v.Almacen = mca.almacen
            LEFT JOIN dbo.vw_stock_minimo_actual AS sm 
                ON v.MATNR = sm.material AND v.Centro = sm.centro AND v.Almacen = sm.almacen
            WHERE sm.stock_minimo IS NOT NULL AND v.StockLibre < sm.stock_minimo
        """);

        List<Object> params = new ArrayList<>();
        if (centro != null && !centro.isBlank()) { sql.append(" AND v.Centro = ?"); params.add(centro);}
        if (almacen != null && !almacen.isBlank()) { sql.append(" AND v.Almacen = ?"); params.add(almacen);}

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }

    public BigDecimal sumarValorTotalDashboard(String centro, String almacen) {
        StringBuilder sql = new StringBuilder("""
            SELECT SUM(v.ValorTotal)
            FROM dbo.vw_stock_actual AS v
            INNER JOIN dbo.material_centro_almacen AS mca 
                ON v.MATNR = mca.material AND v.Centro = mca.centro AND v.Almacen = mca.almacen
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();
        if (centro != null && !centro.isBlank()) { sql.append(" AND v.Centro = ?"); params.add(centro);}
        if (almacen != null && !almacen.isBlank()) { sql.append(" AND v.Almacen = ?"); params.add(almacen);}

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), BigDecimal.class);
    }

    public BigDecimal sumarValorTotalAlertasDashboard(String centro, String almacen) {
        StringBuilder sql = new StringBuilder("""
            SELECT SUM(v.ValorTotal)
            FROM dbo.vw_stock_actual AS v
            INNER JOIN dbo.material_centro_almacen AS mca 
                ON v.MATNR = mca.material AND v.Centro = mca.centro AND v.Almacen = mca.almacen
            LEFT JOIN dbo.vw_stock_minimo_actual AS sm 
                ON v.MATNR = sm.material AND v.Centro = sm.centro AND v.Almacen = sm.almacen
            WHERE sm.stock_minimo IS NOT NULL AND v.StockLibre < sm.stock_minimo
        """);

        List<Object> params = new ArrayList<>();
        if (centro != null && !centro.isBlank()) { sql.append(" AND v.Centro = ?"); params.add(centro);}
        if (almacen != null && !almacen.isBlank()) { sql.append(" AND v.Almacen = ?"); params.add(almacen);}

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), BigDecimal.class);
    }
}