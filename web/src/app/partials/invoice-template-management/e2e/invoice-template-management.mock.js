let invoiceNoId = {
    invoice_template_name: 'test',
    invoice_category: 'iata-avi',
    template_document: null
};

let invoiceWithId = {
  id: 1
};

Object.assign(invoiceWithId, invoiceNoId);

module.exports = [
  {
    request: {
      path: '/invoice-templates',
      method: 'GET'
    },
    response: {
      data: {content: [invoiceWithId]}
    }
  },
  {
    request: {
      path: '/invoice-templates',
      method: 'POST',
      data: invoiceNoId
    },
    response: {
      data: {content: [invoiceWithId]}
    }
  },
  {
    request: {
      path: '/invoice-templates/1',
      method: 'DELETE'
    },
    response: {
      data: {content: [invoiceWithId]}
    }
  }
];
