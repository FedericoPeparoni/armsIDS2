let enrouteAirNavigationChargeNoId = {
    'upper_limit': 1,
    'domestic_formula': "Test",
    'regional_departure_formula': "TestDeparture",
    'regional_arrival_formula': "TestArrival",
    'regional_overflight_formula': "TestOverflight",
    'international_departure_formula': "TestInternationalDeparture",
    'international_arrival_formula': "TestInternationalArrival",
    'international_overflight_formula': "TestInternationalOverflight"
};

let enrouteAirNavigationChargeWithId = {
    "id": 1
};

Object.assign(enrouteAirNavigationChargeWithId, enrouteAirNavigationChargeNoId);

module.exports = [{
        request: {
            path: '/navigation-billing-formulas',
            method: 'GET'
        },
        response: {
            data: {content: [enrouteAirNavigationChargeWithId]}
        }
    },
    {
        request: {
            path: '/navigation-billing-formulas',
            method: 'POST',
            data: enrouteAirNavigationChargeNoId
        },
        response: {
            data: {content: [enrouteAirNavigationChargeWithId]}
        }
    },
    {
        request: {
            path: '/navigation-billing-formulas',
            method: 'PUT',
            data: enrouteAirNavigationChargeWithId
        },
        response: {
            data: {content: [enrouteAirNavigationChargeWithId]}
        }
    },
    {
        request: {
            path: '/navigation-billing-formulas/1',
            method: 'DELETE'
        },
        response: ''
    }
];