delete from invoice_templates;

alter table invoice_templates
    drop constraint invoice_templates_invoice_template_name_key;
	
alter table invoice_templates
    add constraint invoice_templates_invoice_category_key unique (invoice_category);