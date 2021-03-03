/*
 * This script prints out all columns of all tables/views in navdb* schemas.
 * Useful for comparing the structure of 2 databases.
 *
 * For example (Linux/bash):
 *
 *   # Save all tables & columns from database "old_navdb" to a file
 *   navdb --dbname="old_navdb" -f all_columns.sql >old_navdb.txt
 *
 *   # Save all tables & columns from database "new_navdb" to a file
 *   navdb --dbname="new_navdb" -f all_columns.sql >new_navdb.txt
 *
 *   # Check for differences
 *   diff -u old_navdb.txt new_navdb.txt
 *
 */
select
	c.table_schema as table_schema,
	c.table_name as table_name,
	t.table_type as table_type,
	c.column_name as column_name,
	c.data_type as data_type
FROM
	information_schema.columns as c
		JOIN
	information_schema.tables as t
		ON c.table_schema = t.table_schema and c.table_name = t.table_name
WHERE
	t.table_schema in ('navdb_common', 'navdb', 'navdb_staging')
ORDER BY
	1, 2, 3, 4, 5
;
