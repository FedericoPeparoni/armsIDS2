let exemptAccountNoId = {
    enroute: true,
    landing: true,
    departure: true,
    parking: true,
    adult: true,
    child: true,
    approach_fees_exempt: true,
    aerodrome_fees_exempt: true,
    account_name: 'Mock Account',
    flight_notes: 'Mock Flight Notes'
};

let exemptAccountWithId = {
    id: 2
};

let account = {
    id: 7,
    name: 'Mock Account'
};

Object.assign(exemptAccountWithId, exemptAccountNoId);

module.exports = [{
        request: {
            path: '/accounts',
            method: 'GET'
        },
        response: {
            data: {content: [account]}
        }
    },
    {
        request: {
            path: '/account-exemptions',
            method: 'GET'
        },
        response: {
            data: {content: [exemptAccountWithId]}
        }
    },
    {
        request: {
            path: '/account-exemptions',
            method: 'POST',
            data: exemptAccountNoId
        },
        response: {
            data: {content: [exemptAccountWithId]}
        }
    },
    {
        request: {
            path: '/account-exemptions',
            method: 'PUT',
            data: exemptAccountWithId
        },
        response: {
            data: {content: [exemptAccountWithId]}
        }
    },
    {
        request: {
            path: '/account-exemptions/2',
            method: 'DELETE'
        },
        response: ''
    }
];
