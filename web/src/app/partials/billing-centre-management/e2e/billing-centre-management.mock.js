let billingCentreNoId = {
    'name': 'Test',
    'prefix_invoice_number': 'TT',
    'invoice_sequence_number': 123
};

let billingCentreWithId = {
    "id": 1
};

Object.assign(billingCentreWithId, billingCentreNoId);

module.exports = [{
        request: {
            path: '/billing-centers',
            method: 'GET'
        },
        response: {
            data: {content: [billingCentreWithId]}
        }
    },
    {
        request: {
            path: '/billing-centers',
            method: 'POST',
            data: billingCentreNoId
        },
        response: {
            data: {content: [billingCentreWithId]}
        }
    },
    {
        request: {
            path: '/billing-centers',
            method: 'PUT',
            data: billingCentreWithId
        },
        response: {
            data: {content: [billingCentreWithId]}
        }
    },
    {
        request: {
            path: '/billing-centers/1',
            method: 'DELETE'
        },
        response: ''
    }
];