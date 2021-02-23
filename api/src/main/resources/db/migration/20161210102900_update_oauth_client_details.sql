--liquibase formatted sql
--changeset mike:20161210102900_update_oauth_client_details.sql dbms:postgresql splitStatements:false

/* Change Oauth access_token_validity and refresh_token_validity to not refresh as often */
/* Our Angular front end needs to handle oauth better, it can make multiple calls at once where one call will be valid and other calls will not */
UPDATE oauth_client_details
SET access_token_validity = 2592000, refresh_token_validity = 2147483647
WHERE client_id = 'abms_web';
