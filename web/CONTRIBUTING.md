### Sidebar and Form Title

- Please follow the project owner's *Headings* order in the requirements.  If it does not exist, use your best judgment on where it should be.
- Verify that the label in the sidebar is correct (see tech spec).  We have agreed that the word “management” used in the tech spec will not be included in the sidebar labels.  (i.e. “Aerodrome Management” in the tech spec is simply “Aerodromes” in the sidebar)
- Verify that the label in the sidebar and the form label are the same
- Verify that access to sidebar option and form are restricted correctly based on the privileges – both for xx_view and xx_modify

### Tables

- Table columns should be set up differently depending on the type of data they may show
- Verify that table sorting works on all and multiple columns
- Verify that the correct default sort order is set (see tech spec)
- Verify that the correct list of table fields is displayed (see tech spec)
- Verify that the fields are justified properly (text: left, date: left, time: left, numeric: right)
- Verify that decimal numeric fields have the correct number of decimal places
- Verify that the text filter works correctly on displayed text fields only
- Verify that the table control scrolls independently of the entire page
- Verify dates follow the format defined in System Configuration (date format)
- If paging is implemented, verify that paging works correctly
- If paging is implemented, verify that the text filter and sorting works on all pages when refresh is clicked, not just the currently displayed page

### Data Entry Forms

- Verify that the field names are the same as the column titles in the table
- Verify that the data entry form title matches the main form title.  (i.e. if main form is aerodromes, then the data edit form should be “Create an aerodrome” or “Edit an aerodrome”.  Verify that the form title contains the “a/an” to be grammatically correct
- Verify that tab stop ordering is sequential on the data entry form and that tab stops are only on modifiable fields
- Verify that reset form does not generate highlighted empty boxes or other errors
- Verify that non-mandatory fields can be left blank during create, update operations (see tech spec table description)
- Verify that field specific validation is performed (see tech spec table description)
- Verify that an attempt to create a duplicate record is reported
- Verify that list boxes are populated with the correct items
- Verify that long list boxes scroll independently of the form
- Verify that list boxes implement a text filter such that only entries matching the characters entered are displayed
- Verify that create, update, delete functions work
- Verify that feedback options for create, update, delete operations work
- Verify that confirm options for update, delete operations work
- Verify that create, update, delete functions report an error if the underlying table is not accessible (rename or remove write access from table for testing)
- Verify that fields have the appropriate write access set (i.e. user cannot modify read-only fields)
- Verify that a user without modify privileges cannot create, update or delete data, nor modify any of the data entry controls when data is displayed.

### Currencies

- Currency headers should be text aligned to the right
- Currency data should be right justified
- Number of decimal places for each currency is defined in the currency table (needs implementation)
- Currencies should *not* display any currency symbol

### Dates

- Should be a consistent format (defined in System Configuration)
- Verify that dates are sent/received in UTC format

### Time

- Should be non-timepicker
- Should range from 0000 to 2359 (24 hour format)
- Should be 4 numeric characters
- Verify that dates are sent/received in 24hr UTC format
- Zulu time zone indicator ‘Z’ should not be used.  If time zone is being specified, it should be UTC
