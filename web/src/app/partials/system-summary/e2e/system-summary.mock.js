let systemSummary = {
  flight_movement_internationa_arrivals: {
    total: 10,
    val: 4,
    percent: 40,
    name: 'International Arrivals'
  }
};

module.exports = [{
    request: {
      path: '/system-summary',
      method: 'GET'
    },
    response: {
      data: systemSummary
    }
  }
];