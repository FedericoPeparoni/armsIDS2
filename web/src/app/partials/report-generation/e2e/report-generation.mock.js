let account = {
    id: 7,
    name: 'Mock Account'
};

module.exports = [{
        request: {
            path: '/accounts',
            method: 'GET'
        },
        response: {
            data: {content: [account]}
        }
    }
];


