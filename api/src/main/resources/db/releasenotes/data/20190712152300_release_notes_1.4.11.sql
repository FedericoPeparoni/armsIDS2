do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.4.11 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.4.11';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.4.11'');';

    -- bugs
    execute format(v_query) using 'Aerodromes - data entry interface needs to prevent international airports from being entered', '106281', release_category_id('bug');
    execute format(v_query) using 'Navdb - Add aerodromes FYIM FYUS', '114451', release_category_id('bug');
    execute format(v_query) using 'Flight movements - duplicate flight detection does not detect duplicate international departures', '114379', release_category_id('bug');
    execute format(v_query) using 'Account balance report is empty', '114595', release_category_id('bug');
    execute format(v_query) using 'FIR boundary displayed correctly in airspace management but not in flight movements', '107540', release_category_id('bug');
    execute format(v_query) using 'Aircraft types - add to aircraft types database', '101779', release_category_id('bug');
    execute format(v_query) using 'Spatia - flights from spatia are being rejected: collection with cascade="all-delete-orphan" was no longer referenced', '101901', release_category_id('bug');
    execute format(v_query) using 'CRONOS query generates internal server error', '102000', release_category_id('bug');
    execute format(v_query) using 'Flight movements - duplicate and missing flight detection does not work with THRU flights, show actual destination does not work either', '102155', release_category_id('bug');
    execute format(v_query) using 'System integration plugins - connection pool exhausted', '102360', release_category_id('bug');
    execute format(v_query) using 'eAIP integration - remote document is not updated and errors are not reported (3 issues)', '102180', release_category_id('bug');
    execute format(v_query) using 'ERP integration - Receipt handling - invoice and flight movement state is not updated', '102166', release_category_id('bug');
    execute format(v_query) using 'ERP integration - all logical operation must be executed in a transaction which either succeeds or fails atomically', '102191', release_category_id('bug');
    execute format(v_query) using 'ERP integration - duplicate sales headers are being created', '102409', release_category_id('bug');
    execute format(v_query) using 'ERP integration - two not null columns added to ERP table', '102164', release_category_id('bug');
    execute format(v_query) using 'AATIS integration - Wrong query is used to select unpaid aatis permit', '102178', release_category_id('bug');
    execute format(v_query) using 'ERP Integration - Null pointer exception on receipt handling', '102165', release_category_id('bug');
    execute format(v_query) using 'Point of sale – aviation invoices AATIS - no data entry control for the AATIS permit number', '102177', release_category_id('bug');
    execute format(v_query) using 'Point of sale - check for permit number after each keystroke is not user-friendly - check on lost focus instead', '102179', release_category_id('bug');
    execute format(v_query) using 'Flight reader from spatia - flight movements being rejected from spatia with exception "already exists"', '102150', release_category_id('bug');
    execute format(v_query) using 'Flights from spatia - rejected due to null pointer exception', '103795', release_category_id('bug');
    execute format(v_query) using 'Spatia flight reader exception - "This item already exists"', '101737', release_category_id('bug');
    execute format(v_query) using 'Rejected message “invoiced or cancelled”. These are two very different issues requiring different actions', '102160', release_category_id('bug');
    execute format(v_query) using 'Rejects - exception trying to correct radar flight strip', '101957', release_category_id('bug');
    execute format(v_query) using 'Radar loading – Radar summary generates an ‘already exists’ error - this condition cannot occur', '102161', release_category_id('bug');
    execute format(v_query) using 'Radar loading - radar is matched to a cancelled flight when there was an uncancelled flight it should have been matched to', '102185', release_category_id('bug');
    execute format(v_query) using 'Flight movement form - flight type field problem', '105944', release_category_id('bug');
    execute format(v_query) using 'Process for retrieving cronos/radar data hangs', '105877', release_category_id('bug');
    execute format(v_query) using 'Point of sale - aviation - handling flight assigned to an account other than system assigned', '102361', release_category_id('bug');
    execute format(v_query) using 'Flight movements – edit form - account should be set automatically', '90176', release_category_id('bug');
    execute format(v_query) using 'Calculations using EET of THRU plans are not correct', '102157', release_category_id('bug');
    execute format(v_query) using 'Invoices - Exchange rate for USD-ANSP is displayed incorrectly on invoice data grid', '93546', release_category_id('bug');
    execute format(v_query) using 'Transactions - Exchange rate for USD-ANSP does not use system setting to display inverse exchange rate', '93562', release_category_id('bug');
    execute format(v_query) using 'Demonstration of a fix for the problem with double charges on duplicate flights', '105631', release_category_id('bug');
    execute format(v_query) using 'Route parser - scrambled route HKJK-FAOR', '107213', release_category_id('bug');
    execute format(v_query) using 'Captcha key should be added to system configuration', '89981', release_category_id('bug');
    execute format(v_query) using 'Aerodrome - lat/long - the error indicates the text entered does not match the format but does not indicate what the format is', '101679', release_category_id('bug');
    execute format(v_query) using 'All datagrids - Download to PDF - remove this function - retain download to .csv only', '101747', release_category_id('bug');
    execute format(v_query) using 'Invoice generation - invoices show 3 places of decimal for USD', '103243', release_category_id('bug');
    execute format(v_query) using 'Better detection of duplicate tower movement when THRU PLAN is A-B-C-A and tower log is segment C-A or D-A', '104328', release_category_id('bug');
    execute format(v_query) using 'Spatia - Flight which is cancelled, then a new flight plan is submitted does not get a flight movement created', '104493', release_category_id('bug');
    execute format(v_query) using 'Flight movement builder - where domestic and international passenger charges are 0, the resolution error for no passenger data should not be flagged', '106262', release_category_id('bug');
    execute format(v_query) using 'Flight movement builder - Cash flights are already invoiced before flight plan is received. This should not generate an error', '106272', release_category_id('bug');
    execute format(v_query) using 'Invoices - during display, the entities associated with the invoice cannot be exported (flight movements, line items, transactions)', '106279', release_category_id('bug');
    execute format(v_query) using 'Transactions - export function is missing', '106280', release_category_id('bug');
    execute format(v_query) using 'Flight movement builder - parking time resolution error should not be flagged if no parking charges are specified for the aerodrome category', '106336', release_category_id('bug');
    execute format(v_query) using 'Point of sale - calculate invoice - in/out dates are blank', '107331', release_category_id('bug');
    execute format(v_query) using 'Local aircraft registry - updated records generate duplicate primary key error instead of updating existing record', '107335', release_category_id('bug');
    execute format(v_query) using 'Incorrect crossing distance used', '107545', release_category_id('bug');
    execute format(v_query) using 'Flight movement builder - FBCO is omitted from FBMN-FBCO-FBXG-FBKE flights', '107590', release_category_id('bug');
    execute format(v_query) using 'Cesium no longer renders in Google Chrome v75.0 when selecting a flight movement or airspace', '107640', release_category_id('bug');
    execute format(v_query) using 'Recalculate - unknown aerodrome results in SQL error actual_dep_ad/actual_dest_ad cannot be null', '107784', release_category_id('bug');
    execute format(v_query) using 'Route parser - failure on HRYR-FZQA - KNM UL432 KMI UB527 LUB mishandled', '107785', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - crossing distance, not d_factor is shown in the IATA invoice', '114408', release_category_id('bug');
    execute format(v_query) using 'Route parser exception on null point on "depAd FLKS - route: DCT FLCM DCT (S1158 E03015) - destAd: FLAA"', '114435', release_category_id('bug');
    execute format(v_query) using 'Route parser - exception on ERROR: geometry requires more points', '114436', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - iata invoice sometimes has entry/exit points reversed', '114470', release_category_id('bug');
    execute format(v_query) using 'IATA invoice entry/exit points are incorrect - wrong points used', '114472', release_category_id('bug');
    execute format(v_query) using 'Credit / debit notes - displays an incorrect warning (related to roundoff error) and fails', '114505', release_category_id('bug');
    execute format(v_query) using 'Navdb routing and waypoint issues', '114572', release_category_id('bug');
    execute format(v_query) using 'Import from spatia - MTOW categories defined causes general error - missing or duplicate value', '114573', release_category_id('bug');
    execute format(v_query) using 'Import from spatia - item has been modified in the mean time', '114575', release_category_id('bug');
    execute format(v_query) using 'Import from spatia - reject due to duplicate primary key error when the detection of a duplicate flight plan does not detect the duplicate', '114576', release_category_id('bug');
    execute format(v_query) using 'Reports - many reports are missing', '114579', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - preview reports generates an error if another user is running recalculate', '114580', release_category_id('bug');
    execute format(v_query) using 'Flight movement builder - Small foreign aircraft always generates an invalid CoA error', '114582', release_category_id('bug');
    execute format(v_query) using 'Flight movement builder - OTHER type flights are reported zero-length-track error', '114596', release_category_id('bug');
    execute format(v_query) using 'FLKK/HQ prefix was used instead of FLSK prefix when user is in the FLSK billing centre', '114597', release_category_id('bug');
    execute format(v_query) using 'Reports - Account status report outstanding balances shown are incorrect', '114598', release_category_id('bug');
    execute format(v_query) using 'Inconsistent handling of ZZZZ flights', '114620', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - amount in full words on the invoice is not translated into spanish', '114621', release_category_id('bug');
    execute format(v_query) using 'Flight movement builder - Cannot resolve point Z to a location', '114632', release_category_id('bug');
    execute format(v_query) using 'Wrong Aerodrome - Recalculation doesn''t affect the FM status', '114645', release_category_id('bug');

    -- changes
    execute format(v_query) using 'Accounts – set default parking exemption to 0', '114610', release_category_id('change');
    execute format(v_query) using 'Users - set default billing centre to the one identified as being headquarters', '114617', release_category_id('change');
    execute format(v_query) using 'Request for ARMS security documentation', '105488', release_category_id('change');
    execute format(v_query) using 'Flight movement builder - when zzzz aircraft type is specified resolve aircraft type via registration number and types in other flights', '102008', release_category_id('change');
    execute format(v_query) using 'South Sudan issue resolution (HJ) and HSSS flights classed as regional', '105875', release_category_id('change');
    execute format(v_query) using 'International arrivals with an enroute distance of zero', '101671', release_category_id('change');
    execute format(v_query) using 'Allow [APPROACH_FEE] and [AERODROME_FEE] to be used in enroute formulas', '107764', release_category_id('change');
    execute format(v_query) using 'Aircraft type exemptions - exemption must be a percentage 0-100, not a boolean flag', '107765', release_category_id('change');
    execute format(v_query) using 'Nominal routes - add a bi-directional indicator to allow A-B routes to have a different direction from B-A routes', '107782', release_category_id('change');
    execute format(v_query) using 'Aviation billing - Contact on invoices and receipt templates is out of date', '114581', release_category_id('change');
    execute format(v_query) using 'Non-IATA invoice should have separate subtotals on invoice page - domestic/international/overflights for each charge type', '114602', release_category_id('change');
    execute format(v_query) using 'Invoices - Add functionality to allow user to void a CASH invoice', '102415', release_category_id('change');

    -- features
    execute format(v_query) using 'Self-Care Portal Flight Schedules & Self-Care Portal Aircraft Registrations', '97063', release_category_id('feature');

end $$;
