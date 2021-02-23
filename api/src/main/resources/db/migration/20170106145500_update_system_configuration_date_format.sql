--liquibase formatted sql
--changeset Mike DiDomizio:20170106145500_update_system_configuration_date_format dbms:postgresql splitStatements:false

/*
    The reason for this change is to match the way AngularJS does dates
    https://docs.angularjs.org/api/ng/filter/date
 */

UPDATE system_configurations set
    range = 'yyyy-MM-dd yyyy-MMM-dd dd-MM-yyyy MM-dd-yyyy',
    default_value = 'yyyy-MMM-dd',
    current_value = 'yyyy-MMM-dd'
WHERE item_name = 'Date format'
