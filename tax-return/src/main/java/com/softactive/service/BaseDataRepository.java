package com.softactive.service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.softactive.core.object.Base;
import com.softactive.core.repository.AbstractDataService;
import com.softactive.core.utils.FieldsDefinition;
import com.softactive.core.utils.JdbcColumn;
import com.softactive.core.utils.JdbcException;
import com.softactive.core.utils.JdbcFilter;
import com.softactive.core.utils.MultiJdbcFilter;
import com.softactive.core.utils.SimpleJdbcFilter;
import com.softactive.core.utils.Utils;
import com.softactive.taxreturn.object.TaxReturnConstants;
import com.softactive.utils.DataServiceUtils;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;
import lombok.Setter;

public abstract class BaseDataRepository<T extends Base> extends AbstractDataService<T> implements TaxReturnConstants {

	@Autowired @Lazy
	DataServiceUtils dataServiceUtils;

	@SuppressWarnings("unchecked")
	public Class<T> getParametrizedTypeOfT() {
		ParameterizedType superClass = null;
		Type t = getClass().getGenericSuperclass();
		boolean casted = false;
		while (!casted) {
			try {
				superClass = (ParameterizedType) t;
				casted = true;
			} catch (ClassCastException e) {
				t = ((Class<T>) t).getGenericSuperclass();
			}
		}
		return (Class<T>) superClass.getActualTypeArguments()[0];
	}

	protected T getInstanceOfT() {
		try {
			return getParametrizedTypeOfT().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@Autowired HikariDataSource dataSource;
//	@Autowired NamedParameterJdbcTemplate jdbcTemplate;
	@Getter @Setter
	@Autowired JdbcTemplate jdbcTemplate;
	protected SimpleJdbcInsert simpleJdbcInsert;
	private RowMapper<T> rowMapper;
	private List<FieldsDefinition> fieldsDefinitionList;
	private List<FieldsDefinition> keyDefinitionListJoined;
	private List<FieldsDefinition> keyDefinitionList;
	private List<FieldsDefinition> valueDefinitionList;
	private Map<String, FieldsDefinition> fieldNameFieldsDefinitionMap;

	public BaseDataRepository() {
		super();
		fieldsDefinitionList = new ArrayList<FieldsDefinition>();
		fieldNameFieldsDefinitionMap = new HashMap<String, FieldsDefinition>();
		createFieldsDefinition(getParametrizedTypeOfT(), null);
		fillKeyDefinitionListJoined();
		fillValueDefinitionList();
		fillKeyDefinitionList();
	}

	protected List<Map<String, Object>> listOfMap() {
		return jdbcTemplate.queryForList(initQuery());
	}

	public List<T> listOfObject() {
		return query(initQuery());
	}

	protected List<T> queryResultList(JdbcFilter jdbcFilter) {
		return query(initQuery() + " where " + jdbcFilter.convertToSql(),
				jdbcFilter.generateWhereClauseObject().toArray());
	}

	protected Map<String, Object> queryForMap(String q, Object[] args) {
		return jdbcTemplate.queryForMap(q, args);
	}

	protected List<Map<String, Object>> queryForList(String q, Object[] args) {
		return jdbcTemplate.queryForList(q, args);
	}

	protected T queryForObject(String q, Object[] args) {
		return jdbcTemplate.queryForObject(q, args, getRowMapper());
	}

	protected T queryForObject(String q) {
		return jdbcTemplate.queryForObject(q, getRowMapper());
	}

	protected List<T> query(String q, Object[] args) {
		return jdbcTemplate.query(q, args, getRowMapper());
	}

	public List<T> query(String q) {
		return jdbcTemplate.query(q, getRowMapper());
	}

	protected void update(String sql) {
		jdbcTemplate.update(sql);
	}

	protected void update(String sql, Object[] args) {
		jdbcTemplate.update(sql, args);
	}
	@Override
	public void delete(int id) {
		jdbcTemplate.update("delete from " + tableName() + " where id = ?", id);
	}

	protected String addOrderBy(List<String> orderByFields) {
		String returnString = " ";
		if (orderByFields != null && orderByFields.size() > 0) {
			returnString = "order by " + String.join(",", orderByFields);
		}
		return returnString;
	}

	protected RowMapper<T> getRowMapper() {
		if (rowMapper == null) {
			return new RowMapper<T>() {
				@Override
				public T mapRow(ResultSet rs, int rowNum) throws SQLException {
					return mapResult(rs);
				}
			};
		}
		return rowMapper;
	}

	protected T mapResult(ResultSet rs) throws SQLException {
		T t = getInstanceOfT();
		for (FieldsDefinition fd : fieldsDefinitionList) {
			setTreeValue(rs, fd, t, tableName());
		}
		return t;
	}

	private void setTreeValue(ResultSet rs, FieldsDefinition fd, T t, String tableName) throws SQLException {
		String dataBaseField = tableName + "." + fd.getJdbcColumn().field();
		Object object = rs.getObject(dataBaseField);
		if (object != null) {
			try {
				if (fd.getJdbcColumn().joinField().isEmpty()) {
					setDataBaseValue(t, fd, object);
				} else {
					for (FieldsDefinition childFd : fd.getFieldsDefinitionList()) {
						setTreeValue(rs, childFd, t,
								dataServiceUtils.getDataServiceFromJdbcObject(fd.getField().getType()).tableName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void setDataBaseValue(T t, FieldsDefinition fd, Object object) throws Exception {
		String fieldNameObject = fd.getFieldNameObjectToRoot();
		if (fd.getField().getType().isAssignableFrom(Double.class)) {
			setValue(t, fieldNameObject, ((Number) object).doubleValue());
		} else if (fd.getField().getType().isAssignableFrom(Integer.class)) {
			setValue(t, fieldNameObject, ((Number) object).intValue());
		} else if (fd.getField().getType().isAssignableFrom(Long.class)) {
			setValue(t, fieldNameObject, ((Number) object).longValue());
		} else if (fd.getField().getType().isAssignableFrom(Boolean.class) && !(object instanceof Boolean)) {
			setValue(t, fieldNameObject, Boolean.valueOf(object.toString()));
		} else if (fd.getField().getType().isAssignableFrom(String.class) && object != null
				&& !(object instanceof String)) {
			setValue(t, fieldNameObject, object.toString());
		} else {
			setValue(t, fieldNameObject, object);
		}
	}
	
	protected void save(List<T> list, List<JdbcException> exceptionList) {
		try {
			save(list);
		} catch (Exception e) {
			exceptionList.add(new JdbcException(e));
		}
	}

	protected void save(List<T> list, int selectBatchSize, int saveBatchSize) {
		List<T> partialList = new ArrayList<T>();
		int count = 0;
		for (T t : list) {
			partialList.add(t);
			count++;
			if (count % saveBatchSize == 0) {
				save(partialList, selectBatchSize);
				partialList = new ArrayList<T>();
			}
		}
		if (partialList.size() > 0) {
			save(partialList, selectBatchSize);
		}
	}

	private void save(List<T> list, int selectBatchSize) {
		List<T> selectList = getObjectsFromDb(list, selectBatchSize);
		List<T> updateList = new ArrayList<T>();
		List<T> insertList = new ArrayList<T>();
		buildSaveLists(list, selectList, updateList, insertList);
		batchInsert(insertList);
		for (T t : updateList) {
			update(t);
		}
	}

	private void buildSaveLists(List<T> saveList, List<T> selectList, List<T> updateList, List<T> insertList) {
		for (T t : saveList) {
			int index = selectList.indexOf(t);
			if (index > -1) {
				T upt = selectList.get(index);
				if (!areUpdateValuesSame(t, upt)) {
					updateObject(t, upt);
					updateList.add(upt);
				}
			} else {
				insertList.add(t);
			}
		}
	}

	protected void updateObject(T sourceJdbcObject, T targetJdbcObject) {
		for (FieldsDefinition w : getValueDefinitionList()) {
			try {
				setValue(targetJdbcObject, w.getField().getName(), sourceJdbcObject.getValue(w.getField().getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setValue(T t, String fieldName, Object object) throws Exception {
		if (fieldName.contains(".")) {
			int pointIndex = fieldName.indexOf(".");
			String parentFieldName = fieldName.substring(0, pointIndex);
			T elementT = (T) t.getValue(parentFieldName);
			if (elementT == null) {
				elementT = (T) fieldNameFieldsDefinitionMap.get(parentFieldName).getField().getType().newInstance();
			}
			setValue(elementT, fieldName.substring(pointIndex + 1), object);
			setValue(t, parentFieldName, elementT);
		} else {
			Class type = PropertyUtils.getPropertyType(t, fieldName);
			if (type.isEnum() && object instanceof String) {
				PropertyUtils.setProperty(t, fieldName, Enum.valueOf(type, (String) object));
			} else {
				PropertyUtils.setProperty(t, fieldName, object);
			}

		}
	}

	protected List<FieldsDefinition> getValueEqualityFields() {
		return getValueEqualityFields(null);
	}

	protected List<FieldsDefinition> getValueEqualityFields(List<String> excludedFieldNameObjectList) {
		if (excludedFieldNameObjectList == null || excludedFieldNameObjectList.size() == 0) {
			return getValueDefinitionList();
		} else {
			List<FieldsDefinition> returnList = new ArrayList<FieldsDefinition>();
			for (FieldsDefinition f : getValueDefinitionList()) {
				if (!excludedFieldNameObjectList.contains(f.getField().getName())) {
					returnList.add(f);
				}
			}
			return returnList;
		}
	}

	protected boolean areUpdateValuesSame(Base thisJdbcObject, Base otherJdbcObject) {
		for (FieldsDefinition w : getValueEqualityFields()) {
			Object thisObject = thisJdbcObject.getValue(w.getField().getName());
			Object otherObject = otherJdbcObject.getValue(w.getField().getName());
			if (thisObject == null && otherObject != null || thisObject != null && otherObject == null) {
				return false;
			}
			if (thisObject != null && otherObject != null) {
				if (thisObject instanceof String) {
					if (!((String) thisObject).equalsIgnoreCase((String) otherObject)) {
						return false;
					}
				} else if (thisObject instanceof Double) {
					if (((Double) thisObject).doubleValue() != ((Double) otherObject).doubleValue()) {
						return false;
					}
				} else if (thisObject instanceof Integer) {
					if (((Integer) thisObject).intValue() != ((Integer) otherObject).intValue()) {
						return false;
					}
				} else if (thisObject instanceof BigDecimal) {
					if (((BigDecimal) thisObject).compareTo((BigDecimal) otherObject) != 0) {
						return false;
					}
				} else {
					if (!thisObject.equals(otherObject)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void delete(T t) {
		Object[] args = new Object[getKeyDefinitionList().size()];
		String sql = "delete from " + tableName() + " where ";
		int index = 0;
		for (FieldsDefinition def : getKeyDefinitionList()) {
			sql += def.getJdbcColumn().field() + "=? and ";
			Object o = t.getValue(def.getFieldNameObjectToLeaf());
			args[index++] = o instanceof Enum<?> ? ((Enum<?>) o).name() : o;
		}
		sql = sql.substring(0, sql.length() - 4);
		jdbcTemplate.update(sql, args);
	}
	
//	@Override
//	public void delete(int id) {
//		Object[] args = new Object[getKeyDefinitionList().size()];
//		args[0] = id;
//		String sql = "delete from " + tableName() + " where id=?";
//		jdbcTemplate.update(sql, args);
//	}
	
	@Override
	public void update(T t) {
		Object[] args = new Object[getValueDefinitionList().size() + getKeyDefinitionList().size()];
		int index = 0;
		String sql = "update " + tableName() + " set ";
		for (FieldsDefinition def : getValueDefinitionList()) {
			sql += def.getJdbcColumn().field() + "=?,";
			Object o = t.getValue(def.getFieldNameObjectToLeaf());
			args[index++] = o instanceof Enum<?> ? ((Enum<?>) o).name() : o;
		}
		sql = sql.substring(0, sql.length() - 1) + " where ";
		for (FieldsDefinition def : getKeyDefinitionList()) {
			Object o = t.getValue(def.getFieldNameObjectToLeaf());
			if (o == null) {
				sql += def.getJdbcColumn().field() + " is null and  ";
				args = ArrayUtils.remove(args, index);
			} else {
				sql += def.getJdbcColumn().field() + "=? and   ";
				args[index++] = o instanceof Enum<?> ? ((Enum<?>) o).name() : o;
			}
		}
		sql = sql.substring(0, sql.length() - 6);
		jdbcTemplate.update(sql, args);
	}

	protected void update(String whereClause, String setClause) {
		String sql = "update " + tableName() + " set ";
		sql += setClause + " where ";
		sql += whereClause + ";";
		jdbcTemplate.execute(sql);
	}

	@Override
	public int insert(T t) throws DuplicateKeyException{
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		for (FieldsDefinition def : getValueDefinitionList()) {
			Object o = t.getValue(def.getFieldNameObjectToLeaf());
			parameters.addValue(def.getJdbcColumn().field(), o instanceof Enum<?> ? ((Enum<?>) o).name() : o);
		}
		for (FieldsDefinition def : getKeyDefinitionList()) {
			Object o = t.getValue(def.getFieldNameObjectToLeaf());
			parameters.addValue(def.getJdbcColumn().field(), o instanceof Enum<?> ? ((Enum<?>) o).name() : o);
		}
		return getSimpleJdbcInsert().executeAndReturnKey(parameters).intValue();
	}

	protected void batchInsert(final List<T> list) {
		if (list.size() > 0) {
			String insertSql = "insert into " + tableName() + " (";
			for (FieldsDefinition def : getKeyDefinitionList()) {
				insertSql += def.getJdbcColumn().field() + ",";
			}
			for (FieldsDefinition def : getValueDefinitionList()) {
				insertSql += def.getJdbcColumn().field() + ",";
			}
			insertSql = insertSql.substring(0, insertSql.length() - 1) + ") values ("
					+ StringUtils.repeat("?", ",", getKeyDefinitionList().size() + getValueDefinitionList().size())
					+ ")";
			jdbcTemplate.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					T t = list.get(i);
					int index = 1;
					for (FieldsDefinition def : getKeyDefinitionList()) {
						Object o = t.getValue(def.getFieldNameObjectToLeaf());
						ps.setObject(index++, o instanceof Enum<?> ? ((Enum<?>) o).name() : o);
					}
					for (FieldsDefinition def : getValueDefinitionList()) {
						Object o = t.getValue(def.getFieldNameObjectToLeaf());
						ps.setObject(index++, o instanceof Enum<?> ? ((Enum<?>) o).name() : o);
					}
				}

				@Override
				public int getBatchSize() {
					return list.size();
				}
			});
		}
	}

	private List<T> getObjectsFromDb(List<T> list, int batchSize) {
		if (list == null || list.size() == 0 || getKeyDefinitionListJoined() == null
				|| getKeyDefinitionListJoined().size() == 0) {
			return new ArrayList<T>();
		}
		List<MultiJdbcFilter> multiFilterList = new ArrayList<MultiJdbcFilter>();
		List<T> returnList = new ArrayList<T>();
		int count = 0;
		for (T t : list) {
			multiFilterList.add(new MultiJdbcFilter(MultiJdbcFilter.Condition.AND, createSimpleFilterArrayFromObject(t)));
			count++;
			if (count % batchSize == 0) {
				returnList.addAll(queryResultList(new MultiJdbcFilter(MultiJdbcFilter.Condition.OR,
						multiFilterList.toArray(new MultiJdbcFilter[multiFilterList.size()]))));
				multiFilterList = new ArrayList<MultiJdbcFilter>();
			}
		}
		if (multiFilterList.size() > 0) {
			returnList.addAll(queryResultList(new MultiJdbcFilter(MultiJdbcFilter.Condition.OR,
					multiFilterList.toArray(new MultiJdbcFilter[multiFilterList.size()]))));
		}
		return returnList;
	}

	private SimpleJdbcFilter[] createSimpleFilterArrayFromObject(T t) {
		SimpleJdbcFilter[] simpleFilterArray = new SimpleJdbcFilter[getKeyDefinitionListJoined().size()];
		int i = 0;
		for (FieldsDefinition def : getKeyDefinitionListJoined()) {
			Object o = t.getValue(def.getFieldNameObjectToRoot());
			simpleFilterArray[i++] = new SimpleJdbcFilter(def.getJdbcColumn().field(), SimpleJdbcFilter.Relation.EQ,
					o instanceof Enum<?> ? ((Enum<?>) o).name() : o);
		}
		return simpleFilterArray;
	}

	protected SimpleJdbcInsert getSimpleJdbcInsert() {
		if (simpleJdbcInsert == null) {
			simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(tableName())
					.usingGeneratedKeyColumns("id");
		}
		return simpleJdbcInsert;
	}

	private void createFieldsDefinition(Class<?> c, FieldsDefinition parentFieldsDefinition) {
		List<Field> fieldList = new ArrayList<Field>();
		for (Field f : Utils.getAllFields(c)) {
			JdbcColumn j = f.getAnnotation(JdbcColumn.class);
			if (j != null) {
				fieldList.add(f);
			}
		}
		fieldList.sort(new Comparator<Field>() {
			@Override
			public int compare(Field o1, Field o2) {
				JdbcColumn j1 = o1.getAnnotation(JdbcColumn.class);
				JdbcColumn j2 = o2.getAnnotation(JdbcColumn.class);
				return Integer.compare(j1.order(), j2.order());
			}
		});
		for (Field f : fieldList) {
			JdbcColumn j = f.getAnnotation(JdbcColumn.class);
			FieldsDefinition fd = new FieldsDefinition(f, j);
			if (j.joinField().length() > 0) {
				createFieldsDefinition(f.getType(), fd);
			}
			if (parentFieldsDefinition == null) {
				fieldsDefinitionList.add(fd);
			} else {
				parentFieldsDefinition.addChildFieldsDefinition(fd);
			}
			fieldNameFieldsDefinitionMap.put(fd.getFieldNameObjectToRoot(), fd);
		}
	}

	private void fillKeyDefinitionListJoined() {
		keyDefinitionListJoined = new ArrayList<FieldsDefinition>();
		for (FieldsDefinition fd : fieldsDefinitionList) {
			keyDefinitionListJoined.addAll(fd.getJoinedKeyFieldsDefinitions());
		}
	}

	private List<FieldsDefinition> getKeyDefinitionListJoined() {
		return keyDefinitionListJoined;
	}

	private void fillValueDefinitionList() {
		valueDefinitionList = new ArrayList<FieldsDefinition>();
		for (FieldsDefinition fd : fieldsDefinitionList) {
			if (fd.getJdbcColumn().keyValue() == JdbcColumn.VALUE) {
				valueDefinitionList.add(fd);
			}
		}
	}

	protected List<FieldsDefinition> getValueDefinitionList() {
		return valueDefinitionList;
	}

	protected FieldsDefinition getFieldsDefinition(String fieldNameObject) {
		return fieldNameFieldsDefinitionMap.get(fieldNameObject);
	}

	private void fillKeyDefinitionList() {
		keyDefinitionList = new ArrayList<FieldsDefinition>();
		for (FieldsDefinition fd : fieldsDefinitionList) {
			if (fd.getJdbcColumn().keyValue() == JdbcColumn.KEY) {
				keyDefinitionList.add(fd);
			}
		}
	}

	protected List<FieldsDefinition> getKeyDefinitionList() {
		return keyDefinitionList;
	}

	protected List<FieldsDefinition> getFieldsDefinitionList() {
		return fieldsDefinitionList;
	}
}
