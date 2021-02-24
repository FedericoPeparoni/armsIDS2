let systemConfiguration = {
  id: 46,
  item_name: 'Banking Information',
  item_class: {
    id: 7,
    name: 'ansp',
  },
  data_type: {
    id: 2,
    name: 'string',
  },
  units: null,
  range: null,
  default_value: 'test',
  current_value: 'test2'
};

module.exports = [{
    request: {
      path: '/system-configurations',
      method: 'GET'
    },
    response: {
      data: {content: [systemConfiguration]}
    }
  },
  {
    request: {
      path: '/system-configurations',
      method: 'PUT',
      data: [systemConfiguration]
    },
    response: {
      data: {content: [systemConfiguration]}
    }
  }
];
