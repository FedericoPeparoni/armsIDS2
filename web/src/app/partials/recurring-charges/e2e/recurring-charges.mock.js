let recurringChargesNoId = {
    service_charge_catalogue: 15,
    account: 7,
    start_date: '2017-Feb-15',
    end_date: '2017-Feb-27',
};

let recurringChargesWithId = {
    id: 2
};

let account = {
    id: 7,
    name: 'Mock Account'
};

let serviceChargeCatalogue = {
    id: 15,
    charge_class: 'Test',
    amount: 45,
    category: "Test",
    charge_basis: "fixed",
    description: "Test",
    invoice_category: "utility",
    maximum_amount: 10,
    minimum_amount: 5,
    subtype: "Test",
    type: "Test"
};

Object.assign(recurringChargesWithId, recurringChargesNoId);

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
            path: '/service-charge-catalogues',
            method: 'GET'
        },
        response: {
            data: {content: [serviceChargeCatalogue]}
        }
    },
    {
        request: {
            path: '/recurring-charges',
            method: 'GET'
        },
        response: {
            data: {content: [recurringChargesWithId]}
        }
    },
    {
        request: {
            path: '/recurring-charges',
            method: 'POST',
            data: recurringChargesNoId
        },
        response: {
            data: {content: [recurringChargesWithId]}
        }
    },
    {
        request: {
            path: '/recurring-charges',
            method: 'PUT',
            data: recurringChargesWithId
        },
        response: {
            data: {content: [recurringChargesWithId]}
        }
    },
    {
        request: {
            path: '/recurring-charges/2',
            method: 'DELETE'
        },
        response: ''
    }
];
