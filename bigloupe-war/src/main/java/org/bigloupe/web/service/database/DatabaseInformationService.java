package org.bigloupe.web.service.database;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.bigloupe.web.dto.Treemap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Create database information for KARMA Database
 * 
 * @author bigloupe
 * 
 */
@Service
public class DatabaseInformationService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private final String QUERY_TABLE_NAME = "SELECT TNAME FROM TAB WHERE TABTYPE='TABLE' AND NOT TNAME LIKE '%BIN%' ORDER BY TNAME";

	private String QUERY_COUNT_RECORDS = "SELECT COUNT(*) FROM ";

	public void prepareDatabaseInformation() throws Exception {

		Treemap treemap = new Treemap("database-information");

		List<String> tables = jdbcTemplate.query(QUERY_TABLE_NAME,
				new RowMapper<String>() {

					@Override
					public String mapRow(ResultSet rs, int line)
							throws SQLException {
						return rs.getString(1);
					}

				});
		System.out
				.println("DatabaseInformationService.prepareDatabaseInformation()"
						+ tables);

		for (final String table : tables) {
			Treemap children = jdbcTemplate.query(QUERY_COUNT_RECORDS + table,
					new ResultSetExtractor<Treemap>() {

						@Override
						public Treemap extractData(ResultSet rs)
								throws SQLException, DataAccessException {
							if (rs.next()) {
								Treemap treemap = new Treemap();
								treemap.setName(table);
								treemap.setSize(rs.getString(1));
								return treemap;
							}
							return null;
						}

					});
			treemap.addChildren(children);
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(new File("database-information.json"), treemap);

	}
}
