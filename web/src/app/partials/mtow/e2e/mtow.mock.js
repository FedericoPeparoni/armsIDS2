let mtowNoId = {
  "upper_limit": 1,
  "average_mtow_factor": 2,
};

let mtowWithId = {
  "id": 1
};

for (let item in mtowNoId) {
  if (mtowNoId.hasOwnProperty(item)) {
    mtowWithId[item] = mtowNoId[item];
  }
}

module.exports = [
  {
    request: {
      path: '/average-mtow-factors',
      method: 'GET'
    },
    response: {
      data: {content: [mtowWithId]}
    }
  },
  {
    request: {
      path: '/average-mtow-factors',
      method: 'POST',
      data: mtowNoId
    },
    response: {
      data: {content: [mtowWithId]}
    }
  },
  {
    request: {
      path: '/average-mtow-factors',
      method: 'PUT',
      data: mtowWithId
    },
    response: {
      data: {content: [mtowWithId]}
    }
  },
  {
    request: {
      path: '/average-mtow-factors/1',
      method: 'DELETE'
    },
    response: ''
  }
];
