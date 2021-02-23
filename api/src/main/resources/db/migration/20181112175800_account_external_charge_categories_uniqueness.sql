/**
  * Make sure the [account, category] combination is unique.
  *
  * See TFS 96635
  */

-- Remove duplicates; see also
--   http://www.postgresonline.com/journal/archives/22-Deleting-Duplicate-Records-in-a-Table.html
delete
from    account_external_charge_categories
where   id not in (
    select  max(id)
    from    account_external_charge_categories
    group by account_id, external_charge_category_id
);

-- Add unique constraint
alter table account_external_charge_categories
    add constraint account_external_charge_categories_key_1
        unique (account_id, external_charge_category_id)
;
