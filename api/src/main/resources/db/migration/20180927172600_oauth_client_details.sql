-- OAuth client for external apps
insert into oauth_client_details (
	client_id,
	resource_ids,
	client_secret,
	scope,
	authorized_grant_types,
	web_server_redirect_uri,
	authorities,
	access_token_validity,
	refresh_token_validity,
	additional_information,
	autoapprove
)
values (
    'abms_external_client',
    'abms',
    '',
    'read,write',
    'password,refresh_token,authorization_code,implicit',
    '',
    '',
    2592000,
    2147483647,
    '{}',
    'true'
);
