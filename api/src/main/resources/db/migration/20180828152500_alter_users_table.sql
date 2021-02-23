-- email_activation_key
update users set email_activation_key = null
 where registration_status is true;

-- activation_key_expiration
update users set activation_key_expiration = null
 where registration_status is true;

 ALTER table users ADD CONSTRAINT check_activation_key_and_registration_status CHECK
    ((registration_status is true AND email_activation_key is null AND activation_key_expiration is null) OR
     (registration_status is false AND email_activation_key is not null AND activation_key_expiration is not null));


