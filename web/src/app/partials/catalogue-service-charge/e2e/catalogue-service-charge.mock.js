let catalogueServiceChargeNoId = {
    charge_class: 1,
    category: "Test",
    type: "Test",
    subtype: 1,
    description: "Test",
    minimum_amount: 1,
    maximum_amount: 10,
    amount: 5,
    charge_basis: "Fixed Price",
    invoice_category: "Lease"
};

let catalogueServiceChargeWithId = {
    id: 1
};

Object.assign(catalogueServiceChargeWithId, catalogueServiceChargeNoId);

module.exports = [
  {
    request: {
      path: '/service-charge-catalogues',
      method: 'GET'
    },
    response: {
      data: {content: [catalogueServiceChargeWithId]}
    }
  },
  {
    request: {
      path: '/service-charge-catalogues',
      method: 'POST',
      data: catalogueServiceChargeNoId
    },
    response: {
      data: {content: [catalogueServiceChargeWithId]}
    }
  },
  {
    request: {
      path: '/service-charge-catalogues',
      method: 'PUT',
      data: catalogueServiceChargeWithId
    },
    response: {
      data: {content: [catalogueServiceChargeWithId]}
    }
  },
  {
    request: {
      path: '/service-charge-catalogues/1',
      method: 'DELETE'
    },
    response: ''
  }
];
