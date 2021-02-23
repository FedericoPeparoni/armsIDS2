--change data type "psql" to data type "string"
UPDATE system_configurations
	SET data_type = (SELECT id FROM system_data_types WHERE NAME = 'string')
		WHERE system_configurations.data_type = (SELECT id FROM system_data_types WHERE NAME = 'psql');

--delete data type "psql" from the system_data_types table
DELETE FROM system_data_types WHERE name = 'psql';
