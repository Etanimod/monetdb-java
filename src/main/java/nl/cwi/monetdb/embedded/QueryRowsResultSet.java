/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0.  If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016 MonetDB B.V.
 */

package nl.cwi.monetdb.embedded;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.ListIterator;

/**
 * A row set retrieved from an embedded MonetDB query result. All the values in this set are already mapped
 * to Java classes a priori.
 *
 * @author <a href="mailto:pedro.ferreira@monetdbsolutions.com">Pedro Ferreira</a>
 */
public class QueryRowsResultSet implements Iterable {

    /**
     * A single row in a result set.
     *
     * @author <a href="mailto:pedro.ferreira@monetdbsolutions.com">Pedro Ferreira</a>
     */
    static public class QueryResulSetSingleRow implements Iterable {

        /**
         * The original row result set from this row.
         */
        private final QueryRowsResultSet resultSet;

        /**
         * The columns values as Java objects.
         */
        private final Object[] columns;

        protected QueryResulSetSingleRow(QueryRowsResultSet resultSet, Object[] columns) {
            this.resultSet = resultSet;
            this.columns = columns;
        }

        /**
         * Gets the original row result set from this row.
         *
         * @return The original row result set from this row
         */
        public QueryRowsResultSet getRowResultSet() { return resultSet; }

        /**
         * Gets the columns values as Java objects.
         *
         * @return The columns values as Java objects
         */
        public Object[] getAllColumns() {
            return columns;
        }

        /**
         * Gets the number of columns.
         *
         * @return The number of columns
         */
        public int getNumberOfColumns() { return columns.length; }

        /**
         * Gets a column value as a Java class.
         *
         * @param <T> A Java class mapped to a MonetDB data type
         * @param index The index of the column
         * @param javaClass The Java class
         * @return The column value as a Java class
         */
        public <T> T getColumn(int index, Class<T> javaClass) {
            return javaClass.cast(columns[index]);
        }

        /**
         * Gets a column value as a Java class using the default mapping.
         *
         * @param <T> A Java class mapped to a MonetDB data type
         * @param index The index of the column
         * @return The column value as a Java class
         */
        public <T> T getColumn(int index) {
            Class<T> javaClass = this.resultSet.mappings[index].getJavaClass();
            return javaClass.cast(columns[index]);
        }

        @Override
        public ListIterator<Object> iterator() {
            return Arrays.asList(this.columns).listIterator();
        }
    }

    /**
     * The original query result set this row set belongs.
     */
    private final AbstractQueryResultSet queryResultSet;

    /**
     * The MonetDB-To-Java mappings of the columns.
     */
    private final MonetDBToJavaMapping[] mappings;

    /**
     * The rows of this set.
     */
    private final QueryResulSetSingleRow[] rows;

    protected QueryRowsResultSet(AbstractQueryResultSet queryResultSet, MonetDBToJavaMapping[] mappings,
                                 Object[][] rows) {
        this.queryResultSet = queryResultSet;
        this.mappings = mappings;
        this.rows = new QueryResulSetSingleRow[mappings.length];
        for(int i = 0 ; i < mappings.length ; i++) {
            this.rows[i] = new QueryResulSetSingleRow(this, rows[i]);
        }
    }

    /**
     * Gets the original query result set this row set belongs.
     *
     * @return The original query result set this row set belongs
     */
    public AbstractQueryResultSet getQueryResultSet() {
        return queryResultSet;
    }

    /**
     * Gets all rows of this set.
     *
     * @return All rows of this set
     */
    public QueryResulSetSingleRow[] getAllRows() { return rows; }

    /**
     * Gets the number of rows in this set.
     *
     * @return The number of rows in this set
     */
    public int getNumberOfRows() { return rows.length; }

    /**
     * Gets the number of columns in this set.
     *
     * @return The number of columns in this set
     */
    public int getNumberOfColumns() { return mappings.length; }

    /**
     * Gets a single row in this set.
     *
     * @param row The index of the row to retrieve
     * @return A single row in this set
     */
    public QueryResulSetSingleRow getSingleRow(int row) {
        return rows[row];
    }

    /**
     * Gets a single value in this set as a Java class.
     *
     * @param <T> A Java class mapped to a MonetDB data type
     * @param row The index of the row to retrieve
     * @param column The index of the column to retrieve
     * @param javaClass The Java class to map
     * @return The value mapped to a instance of the provided class
     */
    public <T> T getSingleValue(int row, int column, Class<T> javaClass) {
        return javaClass.cast(this.rows[row].getColumn(column));
    }

    /**
     * Gets a single value in this set as a Java class using the default mapping.
     *
     * @param <T> A Java class mapped to a MonetDB data type
     * @param row The index of the row to retrieve
     * @param column The index of the column to retrieve
     * @return The value mapped to a instance of the provided class
     */
    public <T> T getSingleValue(int row, int column) {
        Class<T> javaClass = this.mappings[column].getJavaClass();
        return javaClass.cast(this.rows[row].getColumn(column));
    }

    /**
     * Gets a column in this set as a Java class.
     *
     * @param <T> A Java class mapped to a MonetDB data type
     * @param column The index of the column to retrieve
     * @param javaClass The Java class
     * @return The value mapped to a instance of the provided class
     */
    @SuppressWarnings("unchecked")
    public <T> T[] getColumn(int column, Class<T> javaClass) {
        T[] res = (T[]) Array.newInstance(javaClass, this.rows.length);
        for(int i = 0 ; i < this.rows.length ; i++) {
            res[i] = this.rows[i].getColumn(column);
        }
        return res;
    }

    /**
     * Gets a column in this set as a Java class using the default mapping.
     *
     * @param <T> A Java class mapped to a MonetDB data type
     * @param column The index of the column to retrieve
     * @return The value mapped to a instance of the provided class
     */
    @SuppressWarnings("unchecked")
    public <T> T[] getColumn(int column) {
        T[] res = (T[]) Array.newInstance(this.mappings[column].getJavaClass(), this.rows.length);
        for(int i = 0 ; i < this.rows.length ; i++) {
            res[i] = this.rows[i].getColumn(column);
        }
        return res;
    }

    @Override
    public ListIterator<QueryResulSetSingleRow> iterator() {
        return Arrays.asList(this.rows).listIterator();
    }
}